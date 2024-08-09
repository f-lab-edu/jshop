package jshop.core.domain.product.service;

import jakarta.persistence.EntityManager;
import java.util.Optional;
import jshop.core.domain.category.entity.Category;
import jshop.core.domain.category.repository.CategoryRepository;
import jshop.core.domain.category.service.CategoryService;
import jshop.core.domain.inventory.entity.Inventory;
import jshop.core.domain.inventory.repository.InventoryRepository;
import jshop.core.domain.product.dto.CreateProductDetailRequest;
import jshop.core.domain.product.dto.CreateProductRequest;
import jshop.core.domain.product.dto.UpdateProductDetailRequest;
import jshop.core.domain.product.repository.ProductDetailRepository;
import jshop.core.domain.product.repository.ProductRepository;
import jshop.core.domain.product.dto.OwnProductsResponse;
import jshop.core.domain.product.entity.Product;
import jshop.core.domain.product.entity.ProductDetail;
import jshop.core.domain.user.dto.UserType;
import jshop.core.domain.user.entity.User;
import jshop.core.domain.user.repository.UserRepository;
import jshop.common.exception.ErrorCode;
import jshop.common.exception.JshopException;
import jshop.core.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductDetailRepository productDetailRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;
    private final EntityManager em;
    private final InventoryRepository inventoryRepository;
    private final UserService userService;

    @Transactional
    public Long createProduct(CreateProductRequest createProductRequest, Long userId) {
        User user = userService.getUser(userId);

        if (!user.getUserType().equals(UserType.SELLER)) {
            log.error(ErrorCode.USER_NOT_SELLER.getLogMessage(), user.getUserType());
            throw JshopException.of(ErrorCode.USER_NOT_SELLER);
        }

        Category category = categoryService.getCategory(createProductRequest.getCategoryId());
        Product newProduct = Product.of(createProductRequest, category, user);

        productRepository.save(newProduct);

        return newProduct.getId();
    }

    public OwnProductsResponse getOwnProducts(Long userId, int pageNumber, int pageSize) {
        User user = userRepository.getReferenceById(userId);

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<Product> page = productRepository.findByOwner(user, pageRequest);

        return OwnProductsResponse.create(page);
    }

    @Transactional
    public Long createProductDetail(CreateProductDetailRequest createProductDetailRequest, Long productId) {
        Product product = getProduct(productId);

        if (productDetailRepository.existsByAttributeAndProduct(createProductDetailRequest.getAttribute(), product)) {
            log.error(ErrorCode.ALREADY_EXISTS_PRODUCT_DETAIL.getLogMessage(), productId,
                createProductDetailRequest.getAttribute());
            throw JshopException.of(ErrorCode.ALREADY_EXISTS_PRODUCT_DETAIL);
        }

        Inventory inventory = Inventory.create();

        ProductDetail newProductDetail = ProductDetail.of(createProductDetailRequest, product, inventory);
        productDetailRepository.save(newProductDetail);

        return newProductDetail.getId();
    }

    @Transactional
    public void updateProductDetail(Long detailId, UpdateProductDetailRequest updateProductDetailRequest) {
        ProductDetail productDetail = getProductDetail(detailId);
        productDetail.update(updateProductDetailRequest);
    }

    @Transactional
    public void updateProductDetailStock(Long detailId, int quantity) {
        ProductDetail productDetail = getProductDetail(detailId);

        Inventory inventory = getInventory(productDetail.getInventory().getId());
        /**
         * 증가 요청은 양수로, 감소 요청은 음수로 들어오지만, 실제 변화는 addStock, removeStock 으로만 이루어지고,
         * 두 메서드다 파라미터는 양수로만 받음.
         * 코드 가독성을 위해 이런방식으로 동작
         * 때문에 removeStock 일때는 음수로 들어온 요청을 -1 곱해줘 반영
         */

        if (quantity > 0) {
            inventory.addStock(quantity);
        } else if (quantity < 0) {
            inventory.removeStock(-quantity);
        } else {
            log.error(ErrorCode.ILLEGAL_QUANTITY_REQUEST_EXCEPTION.getLogMessage(), quantity);
            throw JshopException.of(ErrorCode.ILLEGAL_QUANTITY_REQUEST_EXCEPTION);
        }
    }

    @Transactional
    public void deleteProductDetail(Long detailId) {
        ProductDetail productDetail = getProductDetail(detailId);
        productDetail.delete();
    }

    public Inventory getInventory(Long inventoryId) {
        Optional<Inventory> optionalInventory = inventoryRepository.findByIdWithPessimisticLock(inventoryId);
        return optionalInventory.orElseThrow(() -> {
            log.error(ErrorCode.INVENTORY_ID_NOT_FOUND.getLogMessage(), inventoryId);
            throw JshopException.of(ErrorCode.INVENTORY_ID_NOT_FOUND);
        });
    }

    public Product getProduct(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        return optionalProduct.orElseThrow(() -> {
            log.error(ErrorCode.PRODUCTID_NOT_FOUND.getLogMessage(), productId);
            throw JshopException.of(ErrorCode.PRODUCTID_NOT_FOUND);
        });
    }

    public ProductDetail getProductDetail(Long detailId) {
        Optional<ProductDetail> optionalProductDetail = productDetailRepository.findById(detailId);
        return optionalProductDetail.orElseThrow(() -> {
            log.error(ErrorCode.PRODUCTDETAIL_ID_NOT_FOUND.getLogMessage(), detailId);
            throw JshopException.of(ErrorCode.PRODUCTDETAIL_ID_NOT_FOUND);
        });
    }
}

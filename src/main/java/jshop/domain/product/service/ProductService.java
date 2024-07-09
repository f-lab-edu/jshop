package jshop.domain.product.service;

import jakarta.persistence.EntityManager;
import java.util.Optional;
import jshop.domain.category.entity.Category;
import jshop.domain.category.repository.CategoryRepository;
import jshop.domain.category.service.CategoryService;
import jshop.domain.inventory.entity.Inventory;
import jshop.domain.inventory.repository.InventoryRepository;
import jshop.domain.product.dto.CreateProductDetailRequest;
import jshop.domain.product.dto.CreateProductRequest;
import jshop.domain.product.dto.OwnProductsResponse;
import jshop.domain.product.dto.UpdateProductDetailRequest;
import jshop.domain.product.entity.Product;
import jshop.domain.product.entity.ProductDetail;
import jshop.domain.product.repository.ProductDetailRepository;
import jshop.domain.product.repository.ProductRepository;
import jshop.domain.user.dto.UserType;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import jshop.global.jwt.dto.CustomUserDetails;
import jshop.global.utils.ProductUtils;
import jshop.global.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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

    @Transactional
    public Long createProduct(CreateProductRequest createProductRequest, Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        User user = UserUtils.getUserOrThrow(optionalUser, userId);

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
        Optional<Inventory> optionalInventory = inventoryRepository.findById(inventoryId);
        return ProductUtils.getInventoryOrThrow(optionalInventory, inventoryId);
    }

    private Product getProduct(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        Product product = ProductUtils.getProductOrThrow(optionalProduct, productId);
        return product;
    }

    public ProductDetail getProductDetail(Long detailId) {
        Optional<ProductDetail> optionalProductDetail = productDetailRepository.findById(detailId);
        return ProductUtils.getProductDetailOrThrow(optionalProductDetail, detailId);
    }

    public boolean checkProductOwnership(UserDetails userDetails, Long productId) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Long userId = customUserDetails.getId();

        Product product = productRepository.findById(productId).orElseThrow(() -> {
            log.error(ErrorCode.PRODUCTID_NOT_FOUND.getLogMessage(), productId);
            throw JshopException.of(ErrorCode.PRODUCTID_NOT_FOUND);
        });

        if (product.getOwner().getId().equals(userId)) {
            return true;
        }
        log.error(ErrorCode.UNAUTHORIZED.getLogMessage(), "Product", productId, userId);
        throw JshopException.of(ErrorCode.UNAUTHORIZED);
    }


    public boolean checkProductDetailOwnership(UserDetails userDetails, Long detailId, Long productId) {
        Object transactionId = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("ProductStockSyncTest.init transactionId = {}", transactionId);
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Long userId = customUserDetails.getId();

        ProductDetail productDetail = productDetailRepository.findById(detailId).orElseThrow(() -> {
            log.error(ErrorCode.PRODUCTDETAIL_ID_NOT_FOUND.getLogMessage(), detailId);
            throw JshopException.of(ErrorCode.PRODUCTDETAIL_ID_NOT_FOUND);
        });

        Product product = productDetail.getProduct();

        if (!product.getId().equals(productId)) {
            log.error("상세 상품이 상품에 속하지 않습니다. 상품 ID : [{}], 상세 상품 ID : [{}]", productId, detailId);
            throw JshopException.of(ErrorCode.BAD_REQUEST);
        }

        if (product.getOwner().getId().equals(userId)) {
            return true;
        }
        log.error(ErrorCode.UNAUTHORIZED.getLogMessage(), "ProductDetail", detailId, userId);
        throw JshopException.of(ErrorCode.UNAUTHORIZED);
    }
}

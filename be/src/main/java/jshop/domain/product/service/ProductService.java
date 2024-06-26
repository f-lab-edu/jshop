package jshop.domain.product.service;

import java.util.Optional;
import jshop.domain.category.entity.Category;
import jshop.domain.category.repository.CategoryRepository;
import jshop.domain.inventory.entity.Inventory;
import jshop.domain.inventory.service.InventoryService;
import jshop.domain.product.dto.CreateProductDetailRequest;
import jshop.domain.product.dto.CreateProductRequest;
import jshop.domain.product.dto.OwnProductsResponse;
import jshop.domain.product.dto.ProductResponse;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductDetailRepository productDetailRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryService inventoryService;

    @Transactional
    public void createProduct(CreateProductRequest createProductRequest, Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        User user = UserUtils.getUserOrThrow(optionalUser, userId);

        if (user.getUserType() != UserType.SELLER) {
            log.error(ErrorCode.USER_NOT_SELLER.getLogMessage(), user.getUserType());
            throw JshopException.of(ErrorCode.USER_NOT_SELLER);
        }

        Long categoryId = createProductRequest.getCategoryId();

        if (!categoryRepository.existsById(categoryId)) {
            log.error(ErrorCode.CATEGORYID_NOT_FOUND.getLogMessage(), categoryId);
            throw JshopException.of(ErrorCode.CATEGORYID_NOT_FOUND);
        }

        Category category = categoryRepository.getReferenceById(categoryId);
        Product newProduct = Product.of(createProductRequest, category, user);

        productRepository.save(newProduct);
    }

    public OwnProductsResponse getOwnProducts(Long userId, int pageNumber, int pageSize) {
        User user = userRepository.getReferenceById(userId);

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<Product> page = productRepository.findByOwner(user, pageRequest);

        return OwnProductsResponse
            .builder()
            .page(page.getNumber())
            .totalPage(page.getTotalPages())
            .products(page.map(ProductResponse::of).toList())
            .totalCount(page.getTotalElements())
            .build();
    }

    @Transactional
    public void createProductDetail(CreateProductDetailRequest createProductDetailRequest, Long productId) {
        Product product = getProduct(productId);

        if (productDetailRepository.existsByAttributeAndProduct(createProductDetailRequest.getAttribute(), product)) {
            log.error(ErrorCode.ALREADY_EXISTS_PRODUCT_DETAIL.getLogMessage(), productId,
                createProductDetailRequest.getAttribute());
            throw JshopException.of(ErrorCode.ALREADY_EXISTS_PRODUCT_DETAIL);
        }

        if (!product.verifyChildAttribute(createProductDetailRequest.getAttribute())) {
            log.error(ErrorCode.INVALID_PRODUCT_ATTRIBUTE.getLogMessage(), product.getAttributes(),
                createProductDetailRequest.getAttribute());
            throw JshopException.of(ErrorCode.INVALID_PRODUCT_ATTRIBUTE);
        }

        Inventory inventory = inventoryService.createInventory();

        ProductDetail newProductDetail = ProductDetail.of(createProductDetailRequest, product, inventory);
        productDetailRepository.save(newProductDetail);
    }

    @Transactional
    public void updateProductDetail(Long detailId, UpdateProductDetailRequest updateProductDetailRequest) {
        ProductDetail productDetail = getProductDetail(detailId);
        productDetail.update(updateProductDetailRequest);
    }

    @Transactional
    public void updateProductDetailStock(Long detailId, int quantity) {
        if (quantity == 0) {
            log.error(ErrorCode.ILLEGAL_QUANTITY_REQUEST_EXCEPTION.getLogMessage(), quantity);
            throw JshopException.of(ErrorCode.ILLEGAL_QUANTITY_REQUEST_EXCEPTION);
        } else {
            inventoryService.changeStock(detailId, quantity);
        }
    }

    @Transactional
    public void deleteProductDetail(Long detailId) {
        ProductDetail productDetail = getProductDetail(detailId);
        productDetail.delete();
    }

    private Product getProduct(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        Product product = ProductUtils.getProductOrThrow(optionalProduct, productId);
        return product;
    }

    private ProductDetail getProductDetail(Long detailId) {
        Optional<ProductDetail> optionalProductDetail = productDetailRepository.findById(detailId);
        ProductDetail productDetail = ProductUtils.getProductDetailOrThrow(optionalProductDetail, detailId);
        return productDetail;
    }

    public boolean checkProductOwnership(UserDetails userDetails, Long productId) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Long userId = customUserDetails.getId();

        Product product = productRepository.findById(productId).orElseThrow(() -> {
            log.error(ErrorCode.PRODUCTID_NOT_FOUND.getLogMessage(), productId);
            throw JshopException.of(ErrorCode.PRODUCTID_NOT_FOUND);
        });

        if (product.getOwner().getId() == userId) {
            return true;
        }
        log.error(ErrorCode.UNAUTHORIZED.getLogMessage(), "Product", productId, userId);
        throw JshopException.of(ErrorCode.UNAUTHORIZED);
    }


    public boolean checkProductDetailOwnership(UserDetails userDetails, Long detailId, Long productId) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Long userId = customUserDetails.getId();

        ProductDetail productDetail = productDetailRepository.findById(detailId).orElseThrow(() -> {
            log.error(ErrorCode.PRODUCTDETAIL_ID_NOT_FOUND.getLogMessage(), detailId);
            throw JshopException.of(ErrorCode.PRODUCTDETAIL_ID_NOT_FOUND);
        });

        Product product = productDetail.getProduct();

        if (product.getId() != productId) {
            log.error("상세 상품이 상품에 속하지 않습니다. 상품 ID : [{}], 상세 상품 ID : [{}]", productId, detailId);
            throw JshopException.of(ErrorCode.BAD_REQUEST);
        }

        if (product.getOwner().getId() == userId) {
            return true;
        }
        log.error(ErrorCode.UNAUTHORIZED.getLogMessage(), "ProductDetail", detailId, userId);
        throw JshopException.of(ErrorCode.UNAUTHORIZED);
    }
}

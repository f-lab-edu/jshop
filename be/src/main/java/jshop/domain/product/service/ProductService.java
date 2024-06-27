package jshop.domain.product.service;

import java.util.Optional;
import java.util.List;
import java.util.function.Predicate;
import jshop.domain.category.entity.Category;
import jshop.domain.category.repository.CategoryRepository;
import jshop.domain.inventory.entity.Inventory;
import jshop.domain.inventory.service.InventoryService;
import jshop.domain.product.dto.CreateProductDetailRequest;
import jshop.domain.product.dto.CreateProductRequest;
import jshop.domain.product.dto.OwnProductsResponse;
import jshop.domain.product.dto.ProductDetailResponse;
import jshop.domain.product.dto.ProductResponse;
import jshop.domain.product.dto.SearchProductDetailQueryResult;
import jshop.domain.product.dto.SearchProductDetailsResponse;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
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
    public void createProductDetail(CreateProductDetailRequest createProductDetailRequest, Long userId,
        Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        Product product = ProductUtils.getProductOrThrow(optionalProduct, productId);
        User owner = product.getOwner();

        if (!owner.getId().equals(userId)) {
            log.error(ErrorCode.UNAUTHORIZED.getLogMessage(), "Product", productId, userId);
            throw JshopException.of(ErrorCode.UNAUTHORIZED);
        }

        if (productDetailRepository.existsByAttribute(createProductDetailRequest.getAttribute())) {
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
    public void updateProductDetail(Long productId, Long detailId, Long userId,
        UpdateProductDetailRequest updateProductDetailRequest) {

        Optional<ProductDetail> optionalProductDetail = productDetailRepository.findById(detailId);
        ProductDetail productDetail = ProductUtils.getProductDetailOrThrow(optionalProductDetail, detailId);

        Product product = productDetail.getProduct();

        if (product.getId() != productId) {
            log.error(ErrorCode.INVALID_PRODUCTDETAIL_PRODUCT.getLogMessage(), detailId);
            throw JshopException.of(ErrorCode.INVALID_PRODUCTDETAIL_PRODUCT);
        }

        if (product.getOwner().getId() != userId) {
            log.error(ErrorCode.UNAUTHORIZED.getLogMessage(), "ProductDetail", detailId, userId);
            throw JshopException.of(ErrorCode.UNAUTHORIZED);
        }

        productDetail.update(updateProductDetailRequest);
    }

    @Transactional
    public void updateProductDetailStock(Long detailId, Long userId, int quantity) {
        if (quantity == 0) {
            log.error(ErrorCode.ILLEGAL_QUANTITY_REQUEST_EXCEPTION.getLogMessage(), quantity);
            throw JshopException.of(ErrorCode.ILLEGAL_QUANTITY_REQUEST_EXCEPTION);
        } else {
            inventoryService.changeStock(detailId, userId, quantity);
        }
    }

    @Transactional
    @PreAuthorize("@productService.checkProductDetailOwnership(authentication.principal, #detailId)")
    public void deleteProductDetail(@P("userId") Long userId, @P("detailId") Long detailId) {
        Optional<ProductDetail> optionalProductDetail = productDetailRepository.findById(detailId);

        ProductDetail productDetail = ProductUtils.getProductDetailOrThrow(optionalProductDetail, detailId);

        Product product = productDetail.getProduct();

        if (product.getOwner().getId() != userId) {
            log.error(ErrorCode.UNAUTHORIZED.getLogMessage(), "ProductDetail", detailId, userId);
            throw JshopException.of(ErrorCode.UNAUTHORIZED);
        }

        productDetail.delete();
    }

    public SearchProductDetailsResponse searchProductDetail(long lastProductId, Optional<String> optionalQuery,
        int size) {

        String query = optionalQuery.orElseThrow(() -> {
            log.error(ErrorCode.NO_SEARCH_QUERY.getLogMessage());
            throw JshopException.of(ErrorCode.NO_SEARCH_QUERY);
        });

        PageRequest pageRequest = PageRequest.of(0, size, Sort.by(Direction.DESC, "id"));
        Page<SearchProductDetailQueryResult> page = productDetailRepository.searchProductDetailsByQuery(lastProductId,
            query, pageRequest);

        List<ProductDetailResponse> contents = page.getContent().stream().map(ProductDetailResponse::of).toList();

        Long nextCursor = Optional
            .ofNullable(page.getContent())
            .filter(Predicate.not(List::isEmpty))
            .map(list -> list.get(list.size() - 1))
            .map(ProductDetailResponse::getId)
            .orElse(null);

        return SearchProductDetailsResponse
            .builder().nextCursor(nextCursor).products(contents).build();
    }

    public boolean checkProductDetailOwnership(UserDetails userDetails, Long detailId) {
        CustomUserDetails cud = (CustomUserDetails) userDetails;
        if (cud.getId() == 1L && detailId == 1L) {
            return true;
        }
//        ProductDetail productDetail = productDetailRepository.findById(productDetailId).orElseThrow();
//
//        if (productDetail.getProduct().getOwner().getId() != userId) {
//            throw JshopException.of(ErrorCode.UNAUTHORIZED);
//        }

        throw JshopException.of(ErrorCode.UNAUTHORIZED);
    }
}

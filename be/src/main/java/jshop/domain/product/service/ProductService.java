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
import jshop.domain.product.dto.SearchProductDetailsResponse;
import jshop.domain.product.entity.Product;
import jshop.domain.product.entity.ProductDetail;
import jshop.domain.product.repository.ProductDetailRepository;
import jshop.domain.product.repository.ProductRepository;
import jshop.domain.user.dto.UserType;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties.Error;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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
        User user = optionalUser.orElseThrow(() -> {
            log.error(ErrorCode.USERID_NOT_FOUND.getLogMessage(), userId);
            throw JshopException.of(ErrorCode.USERID_NOT_FOUND);
        });

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

        Product product = productRepository.findById(productId).orElseThrow(() -> {
            log.error(ErrorCode.PRODUCTID_NOT_FOUND.getLogMessage(), productId);
            throw JshopException.of(ErrorCode.PRODUCTID_NOT_FOUND);
        });
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

        productDetailRepository.save(ProductDetail.of(createProductDetailRequest, product, inventory));
    }

    public SearchProductDetailsResponse searchProductDetail(long lastProductId, Optional<String> optionalQuery,
        int size) {

        String query = optionalQuery.orElseThrow(() -> {
            log.error(ErrorCode.NO_SEARCH_QUERY.getLogMessage());
            throw JshopException.of(ErrorCode.NO_SEARCH_QUERY);
        });

        PageRequest pageRequest = PageRequest.of(0, size, Sort.by(Direction.DESC, "id"));
        Page<ProductDetailResponse> page = productDetailRepository.searchProductDetailsByQuery(lastProductId, query,
            pageRequest);

        Long nextCursor = Optional
            .ofNullable(page.getContent())
            .filter(Predicate.not(List::isEmpty))
            .map(List::getLast)
            .map(ProductDetailResponse::getId)
            .orElse(null);

        return SearchProductDetailsResponse
            .builder().nextCursor(nextCursor).products(page.getContent()).build();
    }
}

package jshop.domain.product.service;

import jshop.domain.category.entity.Category;
import jshop.domain.category.repository.CategoryRepository;
import jshop.domain.inventory.entity.Inventory;
import jshop.domain.inventory.service.InventoryService;
import jshop.domain.product.dto.CreateProductDetailRequest;
import jshop.domain.product.dto.CreateProductRequest;
import jshop.domain.product.dto.OwnProductsResponse;
import jshop.domain.product.dto.ProductResponse;
import jshop.domain.product.entity.Product;
import jshop.domain.product.entity.ProductDetail;
import jshop.domain.product.repository.ProductDetailRepository;
import jshop.domain.product.repository.ProductRepository;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
        User user = userRepository.getReferenceById(userId);
        Long categoryId = createProductRequest.getCategoryId();

        if (!categoryRepository.existsById(categoryId)) {
            log.error(ErrorCode.CATEGORYID_NOT_FOUND.getLogMessage(), categoryId);
            throw JshopException.ofErrorCode(ErrorCode.CATEGORYID_NOT_FOUND);
        }

        Category category = categoryRepository.getReferenceById(categoryId);
        Product newProduct = Product.ofCreateProductRequest(createProductRequest, category, user);

        productRepository.save(newProduct);
    }

    @Transactional
    public OwnProductsResponse getOwnProducts(Long userId, int pageNumber) {
        User user = userRepository.getReferenceById(userId);

        PageRequest pageRequest = PageRequest.of(pageNumber, 10);
        Page<Product> page = productRepository.findByOwner(user, pageRequest);

        return OwnProductsResponse
            .builder()
            .page(page.getNumber())
            .totalPage(page.getTotalPages())
            .products(page.map(ProductResponse::ofProduct).toList())
            .totalCount(page.getTotalElements())
            .build();
    }

    @Transactional
    public void createProductDetail(CreateProductDetailRequest createProductDetailRequest,
        Long userId, Long productId) {
        User user = userRepository.getReferenceById(userId);

        Product product = productRepository.findById(productId).orElseThrow(() -> {
            log.error(ErrorCode.PRODUCTID_NOT_FOUND.getLogMessage(), productId);
            throw JshopException.ofErrorCode(ErrorCode.PRODUCTID_NOT_FOUND);
        });

        if (!product.getOwner().getId().equals(user.getId())) {
            log.error(ErrorCode.UNAUTHORIZED.getLogMessage(), "Product", productId, userId);
            throw JshopException.ofErrorCode(ErrorCode.UNAUTHORIZED);
        }

        if (productDetailRepository.existsByAttribute(createProductDetailRequest.getAttribute())) {
            log.error(ErrorCode.ALREADY_EXISTS_PRODUCT_DETAIL.getLogMessage(), productId, createProductDetailRequest.getAttribute());
            throw JshopException.ofErrorCode(ErrorCode.ALREADY_EXISTS_PRODUCT_DETAIL);
        }

        if (!product.verifyChildAttribute(createProductDetailRequest.getAttribute())) {
            log.error(ErrorCode.INVALID_PRODUCT_ATTRIBUTE.getLogMessage(), product.getAttributes(), createProductDetailRequest.getAttribute());
            throw JshopException.ofErrorCode(ErrorCode.INVALID_PRODUCT_ATTRIBUTE);
        }

        Inventory inventory = inventoryService.createInventory();

        productDetailRepository.save(ProductDetail.ofCreateProductDetailRequest(createProductDetailRequest, product, inventory));
    }
}

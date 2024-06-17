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
import jshop.domain.product.entity.Product;
import jshop.domain.product.entity.ProductDetail;
import jshop.domain.product.repository.ProductDetailRepository;
import jshop.domain.product.repository.ProductRepository;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import jshop.global.common.ErrorCode;
import jshop.global.exception.common.AlreadyExistsException;
import jshop.global.exception.common.NoSuchEntityException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new RuntimeException();
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
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException();
        }
        Product product = productRepository.getReferenceById(productId);

        if (!product.getOwner().getId().equals(user.getId())) {
            /**
             * 예외 변경
             */
            throw new RuntimeException("나중에 공통 예외로 yml 처리");
        }

        if (productDetailRepository.existsByAttribute(createProductDetailRequest.getAttribute())) {
            throw new AlreadyExistsException("동일한 속성의 상세상품이 존재합니다.", ErrorCode.ALREADY_EXISTS_PRODUCT_DETAIL);
        }

        if (!product.verifyChildAttribute(createProductDetailRequest.getAttribute())) {
            throw new RuntimeException("공통 예외 처리");
        }

        Inventory inventory = inventoryService.createInventory();

        productDetailRepository.save(ProductDetail.ofCreateProductDetailRequest(createProductDetailRequest, product, inventory));
    }
}

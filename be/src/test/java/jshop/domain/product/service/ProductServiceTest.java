package jshop.domain.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
@DisplayName("[단위 테스트] ProductService")
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductDetailRepository productDetailRepository;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Captor
    private ArgumentCaptor<Product> productCaptor;

    @Captor
    private ArgumentCaptor<ProductDetail> productDetailArgumentCaptor;


    @Nested
    @DisplayName("상품 생성 테스트")
    class CreateProduct {

        private static final Map<String, List<String>> attributes = new HashMap<>();
        private final String productName = "product";
        private final String productManufacturer = "apple";
        private final String productDescription = "description";

        @BeforeAll
        public static void init() {
            attributes.put("attr1", List.of("1", "2"));
        }

        @Test
        @DisplayName("유저가 판매자이고 상품 생성 요청에 문제가 없다면 상품을 생성함")
        public void createProduct_success() {
            // given
            CreateProductRequest createProductRequest = CreateProductRequest
                .builder()
                .name(productName)
                .categoryId(1L)
                .manufacturer(productManufacturer)
                .description(productDescription)
                .attributes(attributes)
                .build();

            User user = User
                .builder().id(1L).username("1").userType(UserType.SELLER).build();

            Category category = Category
                .builder().id(1L).name("category").build();

            // when
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(categoryRepository.existsById(1L)).thenReturn(true);
            when(categoryRepository.getReferenceById(1L)).thenReturn(category);

            productService.createProduct(createProductRequest, 1L);

            // then
            verify(productRepository, times(1)).save(productCaptor.capture());

            Product captorProduct = productCaptor.getValue();
            assertAll("상품 검증", () -> assertThat(captorProduct.getOwner()).isEqualTo(user),
                () -> assertThat(captorProduct.getManufacturer()).isEqualTo(productManufacturer),
                () -> assertThat(captorProduct.getDescription()).isEqualTo(productDescription),
                () -> assertThat(captorProduct.getCategory()).isEqualTo(category),
                () -> assertThat(captorProduct.getName()).isEqualTo(createProductRequest.getName()),
                () -> assertThat(captorProduct.getAttributes()).isEqualTo(attributes));
        }

        @Test
        @DisplayName("유저가 판매자가 아니라면 상품 생성을 할 수 없음")
        public void createProduct_noSeller() {
            // given
            CreateProductRequest createProductRequest = CreateProductRequest
                .builder()
                .name(productName)
                .categoryId(1L)
                .manufacturer(productManufacturer)
                .description(productDescription)
                .attributes(attributes)
                .build();

            User user = User
                .builder().id(1L).username("1").userType(UserType.USER).build();

            // when
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            // then
            JshopException jshopException = assertThrows(JshopException.class,
                () -> productService.createProduct(createProductRequest, 1L));

            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_SELLER);
        }

        @Test
        @DisplayName("요청으로 전달된 카테고리가 없다면 상품을 생성할 수 없음")
        public void createProduct_NoCategory() {
            // given
            CreateProductRequest createProductRequest = CreateProductRequest
                .builder()
                .name(productName)
                .categoryId(1L)
                .manufacturer(productManufacturer)
                .description(productDescription)
                .attributes(attributes)
                .build();

            User user = User
                .builder().id(1L).username("1").userType(UserType.SELLER).build();

            // when
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(categoryRepository.existsById(1L)).thenReturn(false);

            // then
            JshopException jshopException = assertThrows(JshopException.class,
                () -> productService.createProduct(createProductRequest, 1L));

            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.CATEGORYID_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("자신의 상품 가져오기 테스트")
    class GetOwnProduct {

        @Test
        @DisplayName("판매자는 자신의 상품을 페이징으로 가져올 수 있음")
        public void getOwnProducts_success() {
            // given
            int pageNumber = 0;
            int pageSize = 10;
            User user = User
                .builder().id(1L).username("1").build();
            List<Product> products = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                products.add(Product
                    .builder().name("product" + i).owner(user).build());
            }
            PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

            // when
            Page<Product> page = new PageImpl<>(products);
            when(userRepository.getReferenceById(1L)).thenReturn(user);
            when(productRepository.findByOwner(user, pageRequest)).thenReturn(page);

            // then
            OwnProductsResponse ownProducts = productService.getOwnProducts(1L, pageNumber, pageSize);
            assertAll("페이징 검증", () -> assertThat(ownProducts.getPage()).isEqualTo(pageNumber),
                () -> assertThat(ownProducts.getTotalPage()).isEqualTo(1),
                () -> assertThat(ownProducts.getTotalCount()).isEqualTo(10),
                () -> assertThat(ownProducts.getProducts()).isEqualTo(
                    products.stream().map(ProductResponse::of).toList()));
        }
    }

    @Nested
    @DisplayName("상세 상품 생성 요청")
    class CreateProductDetail {

        private Product product;
        private Inventory inventory;

        @BeforeEach
        public void init() {
            inventory = Inventory
                .builder().quantity(0).minQuantity(0).build();

            Map<String, List<String>> attributes = new HashMap<>();
            attributes.put("size", List.of("90", "95", "100"));
            attributes.put("color", List.of("red", "blue", "yellow"));

            product = Product
                .builder().name("test_product").attributes(attributes).build();
        }

        @Test
        @DisplayName("상세 상품의 속성이 이미 등록된 상품이 아니고, 상품에 정의된 속성에 포함된다면 상세 상품 정보와 상품 ID로 상세 상품을 생성할 수 있음")
        public void createProductDetail_success() {
            // given
            Map<String, String> attribute = new HashMap<>();
            attribute.put("color", "red");
            attribute.put("size", "100");

            CreateProductDetailRequest createProductDetailRequest = CreateProductDetailRequest
                .builder().price(1000L).attribute(attribute).build();

            // when
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(inventoryService.createInventory()).thenReturn(inventory);
            when(productDetailRepository.existsByAttributeAndProduct(any(Map.class), any(Product.class))).thenReturn(
                false);

            // then
            productService.createProductDetail(createProductDetailRequest, 1L);
            verify(productDetailRepository, times(1)).save(productDetailArgumentCaptor.capture());

            ProductDetail argProductDetail = productDetailArgumentCaptor.getValue();
            assertThat(argProductDetail.getProduct()).isEqualTo(product);
            assertThat(argProductDetail.getInventory()).isEqualTo(inventory);
            assertThat(product.verifyChildAttribute(argProductDetail.getAttribute())).isTrue();
        }

        @Test
        @DisplayName("상품에 동일한 속성을 가진 상세 상품이 존재한다면 상세 상품을 생성할 수 없음")
        public void createProductDetail_dupAttribute() {
            // given
            Map<String, String> attribute = new HashMap<>();
            attribute.put("color", "red");
            attribute.put("size", "100");

            CreateProductDetailRequest createProductDetailRequest = CreateProductDetailRequest
                .builder().price(1000L).attribute(attribute).build();

            // when
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productDetailRepository.existsByAttributeAndProduct(any(Map.class), any(Product.class))).thenReturn(
                true);

            // then
            JshopException jshopException = assertThrows(JshopException.class,
                () -> productService.createProductDetail(createProductDetailRequest, 1L));
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.ALREADY_EXISTS_PRODUCT_DETAIL);
        }

        @Test
        @DisplayName("상세 상품이 상품에 정의된 속성에 맞지 않는 속성을 갖는다면 생성할 수 없음")
        public void createProductDetail_invalidAttribute() {
            // given
            Map<String, String> attribute = new HashMap<>();
            attribute.put("color", "cyan");
            attribute.put("size", "1001");

            CreateProductDetailRequest createProductDetailRequest = CreateProductDetailRequest
                .builder().price(1000L).attribute(attribute).build();

            // when
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productDetailRepository.existsByAttributeAndProduct(any(Map.class), any(Product.class))).thenReturn(
                true);

            // then
            assertThrows(RuntimeException.class,
                () -> productService.createProductDetail(createProductDetailRequest, 1L));
        }
    }

    @Nested
    @DisplayName("상세 상품 정보 변경")
    class UpdateProductDetail {

        private static Product product;
        private static ProductDetail productDetail;
        private static User user;

        @BeforeAll
        public static void init() {
            user = User
                .builder().id(1L).username("kim").build();
            product = Product
                .builder().id(1L).name("product").owner(user).build();
            productDetail = ProductDetail
                .builder().id(1L).price(100L).product(product).build();
        }

        @Test
        @DisplayName("사용자는 자신의 상세 상품의 정보(가격)을 변경할 수 있다.")
        public void updateProductDetail_success() {
            // given
            UpdateProductDetailRequest updateProductDetailRequest = UpdateProductDetailRequest
                .builder().price(1000L).build();

            // when
            when(productDetailRepository.findById(1L)).thenReturn(Optional.of(productDetail));

            // then
            productService.updateProductDetail(1L, updateProductDetailRequest);
            assertThat(productDetail.getPrice()).isEqualTo(1000L);
        }
    }

    @Nested
    @DisplayName("상세 상품 재고 변경")
    class UpdateProductDetailQuantity {

        private Inventory inventory;
        private ProductDetail productDetail;

        private Long inventoryId = 1L;
        private Long productDetailId = 1L;

        @BeforeEach
        public void init() {
            inventory = Inventory
                .builder().id(inventoryId).quantity(0).minQuantity(0).build();

            productDetail = ProductDetail
                .builder().inventory(inventory).id(productDetailId).build();
        }

        @Test
        @DisplayName("변경 수량이 0이 아니면 재고 변경을 할 수 있음")
        public void changeStock() {
            productService.updateProductDetailStock(productDetailId, 10);
            verify(inventoryService, times(1)).changeStock(productDetailId, 10);

            productService.updateProductDetailStock(productDetailId, -5);
            verify(inventoryService, times(1)).changeStock(productDetailId, -5);
        }

        @Test
        @DisplayName("변경 수량이 0 이면 ILLEGAL_QUANTITY_REQUEST_EXCEPTION 예외")
        public void invalid_update_stock() {
            // then
            JshopException jshopException = assertThrows(JshopException.class,
                () -> productService.updateProductDetailStock(1L, 0));

            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.ILLEGAL_QUANTITY_REQUEST_EXCEPTION);
        }
    }


    @Nested
    @DisplayName("상세 상품 삭제")
    class DeleteProductDetail {

        private Product product;
        private ProductDetail productDetail;
        private User user;

        @BeforeEach
        public void init() {
            user = User
                .builder().id(1L).username("kim").build();
            product = Product
                .builder().id(1L).name("product").owner(user).build();
            productDetail = ProductDetail
                .builder().id(1L).price(100L).product(product).build();
        }

        @Test
        @DisplayName("사용자는 자신의 상세 상품을 삭제할 수 있다")
        public void deleteProductDetail_success() {
            // when
            when(productDetailRepository.findById(1L)).thenReturn(Optional.of(productDetail));

            // then
            productService.deleteProductDetail(1L);
            assertThat(productDetail.getIsDeleted()).isTrue();
        }
    }
}
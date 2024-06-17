package jshop.domain.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import jshop.domain.product.entity.Product;
import jshop.domain.product.entity.ProductDetail;
import jshop.domain.product.repository.ProductDetailRepository;
import jshop.domain.product.repository.ProductRepository;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
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


    @Test
    public void 상품생성() {
        // given
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("attr1", List.of("1", "2"));

        CreateProductRequest createProductRequest = CreateProductRequest
            .builder()
            .name("product")
            .categoryId(1L)
            .manufacturer("apple")
            .description("description")
            .attributes(attributes)
            .build();

        User user = User
            .builder().id(1L).username("1").build();

        Category category = Category
            .builder().id(1L).name("category").build();

        // when
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.getReferenceById(1L)).thenReturn(category);

        productService.createProduct(createProductRequest, 1L);

        // then
        verify(productRepository, times(1)).save(productCaptor.capture());

        assertThat(productCaptor.getValue().getOwner()).isEqualTo(user);
        assertThat(productCaptor.getValue().getCategory()).isEqualTo(category);
        assertThat(productCaptor.getValue().getName()).isEqualTo(createProductRequest.getName());
    }

    @Test
    public void 상세상품생성() {
        // given
        User user = createUser1();
        Product product = createProduct1(user);
        Inventory inventory = createInventory();

        Map<String, String> attribute = new HashMap<>();
        attribute.put("color", "red");
        attribute.put("size", "100");

        CreateProductDetailRequest createProductDetailRequest = CreateProductDetailRequest
            .builder().price(1000L).attribute(attribute).build();

        // when
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryService.createInventory()).thenReturn(inventory);
        when(productDetailRepository.existsByAttribute(anyMap())).thenReturn(false);

        // then
        productService.createProductDetail(createProductDetailRequest, 1L, 1L);
        verify(productDetailRepository, times(1)).save(productDetailArgumentCaptor.capture());

        ProductDetail argProductDetail = productDetailArgumentCaptor.getValue();
        assertThat(argProductDetail.getProduct()).isEqualTo(product);
        assertThat(argProductDetail.getProduct().getOwner()).isEqualTo(user);
        assertThat(argProductDetail.getInventory()).isEqualTo(inventory);
        assertThat(product.verifyChildAttribute(argProductDetail.getAttribute())).isTrue();
    }

    @Test
    public void 상세상품생성_주인아닌유저() {
        // given
        User user = createUser1();
        User user2 = createUser2();
        Product product = createProduct1(user);
        Inventory inventory = createInventory();

        Map<String, String> attribute = new HashMap<>();
        attribute.put("color", "red");
        attribute.put("size", "100");

        CreateProductDetailRequest createProductDetailRequest = CreateProductDetailRequest
            .builder().price(1000L).attribute(attribute).build();

        // when
        when(userRepository.getReferenceById(1L)).thenReturn(user2);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // then
        JshopException jshopException = assertThrows(JshopException.class, () -> productService.createProductDetail(createProductDetailRequest, 1L, 1L));
        assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED);
    }

    @Test
    public void 상세상품생성_동일상품예외() {
        // given
        User user = createUser1();
        Product product = createProduct1(user);
        Inventory inventory = createInventory();

        Map<String, String> attribute = new HashMap<>();
        attribute.put("color", "red");
        attribute.put("size", "100");

        CreateProductDetailRequest createProductDetailRequest = CreateProductDetailRequest
            .builder().price(1000L).attribute(attribute).build();

        // when
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productDetailRepository.existsByAttribute(anyMap())).thenReturn(true);

        // then
        JshopException jshopException = assertThrows(JshopException.class, () -> productService.createProductDetail(createProductDetailRequest, 1L, 1L));
        assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.ALREADY_EXISTS_PRODUCT_DETAIL);
    }

    @Test
    public void 상세상품생성_잘못된속성() {
        // given
        User user = createUser1();
        Product product = createProduct1(user);
        Inventory inventory = createInventory();

        Map<String, String> attribute = new HashMap<>();
        attribute.put("color", "cyan");
        attribute.put("size", "1001");

        CreateProductDetailRequest createProductDetailRequest = CreateProductDetailRequest
            .builder().price(1000L).attribute(attribute).build();

        // when
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productDetailRepository.existsByAttribute(anyMap())).thenReturn(true);

        // then
        assertThrows(RuntimeException.class, () -> productService.createProductDetail(createProductDetailRequest, 1L, 1L));
    }


    private User createUser1() {
        return User
            .builder().id(1L).username("user").build();
    }

    private User createUser2() {
        return User
            .builder().id(2L).username("user2").build();
    }

    private Inventory createInventory() {
        return Inventory
            .builder().quantity(0).minQuantity(0).build();
    }

    private Product createProduct1(User user) {
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("size", List.of("90", "95", "100"));
        attributes.put("color", List.of("red", "blue", "yellow"));

        return Product
            .builder().owner(user).name("test_product").attributes(attributes).build();
    }
}
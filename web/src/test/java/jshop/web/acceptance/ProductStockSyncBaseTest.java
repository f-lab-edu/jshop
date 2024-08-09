package jshop.web.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import jshop.core.domain.category.dto.CreateCategoryRequest;
import jshop.core.domain.category.repository.CategoryRepository;
import jshop.core.domain.category.service.CategoryService;
import jshop.core.domain.inventory.entity.Inventory;
import jshop.core.domain.inventory.repository.InventoryHistoryRepository;
import jshop.core.domain.inventory.repository.InventoryRepository;
import jshop.core.domain.product.dto.CreateProductDetailRequest;
import jshop.core.domain.product.dto.CreateProductRequest;
import jshop.core.domain.product.entity.ProductDetail;
import jshop.core.domain.product.repository.ProductDetailRepository;
import jshop.core.domain.product.repository.ProductRepository;
import jshop.core.domain.product.service.ProductService;
import jshop.core.domain.user.dto.JoinUserRequest;
import jshop.core.domain.user.dto.UserType;
import jshop.core.domain.user.repository.UserRepository;
import jshop.core.domain.user.service.UserService;
import jshop.common.test.BaseTestContainers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@SpringBootTest
@DisplayName("[통합 테스트] ProductController - sync")
public class ProductStockSyncBaseTest extends BaseTestContainers {

    private Long sellerUserId;
    private String sellerUserToken;
    private Long categoryId;
    private Long productId;
    private Long productDetailId;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @PersistenceContext
    EntityManager em;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryHistoryRepository inventoryHistoryRepository;
    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    CategoryService categoryService;

    @BeforeEach
    public void init() throws Exception {
        CreateCategoryRequest createCategoryRequest = CreateCategoryRequest
            .builder().name("category").build();
        JoinUserRequest joinUserRequest = JoinUserRequest
            .builder()
            .email("email@email.com")
            .username("username")
            .password("password")
            .userType(UserType.SELLER)
            .build();

        List<CreateProductDetailRequest> createProductDetailRequests = new ArrayList<>();
        String[] props = {"a", "b", "c"};

        for (String prop : props) {
            Map<String, String> attribute = new HashMap<>();
            attribute.put("attr1", prop);
            createProductDetailRequests.add(CreateProductDetailRequest
                .builder().price(1000L).attribute(attribute).build());
        }



        sellerUserId = userService.joinUser(joinUserRequest);
        categoryId = categoryService.createCategory(createCategoryRequest);

        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("attr1", List.of("a", "b", "c"));
        CreateProductRequest createProductRequest = CreateProductRequest
            .builder()
            .name("product")
            .categoryId(categoryId)
            .manufacturer("manufacturer")
            .description("description")
            .attributes(attributes)
            .build();

        productId = productService.createProduct(createProductRequest, sellerUserId);
        productDetailId = productService.createProductDetail(createProductDetailRequests.get(0),
            productId);
    }

    @Test
    @DisplayName("동시에 여러번의 재고 변화 요청이 있더라도, 모두 반영되어야 함. (동시성 문제)")
    public void changeStock_sync() throws Exception {
        // given
        ExecutorService executors = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            executors.submit(() -> {
                productService.updateProductDetailStock(productDetailId, 10);
            });
        }
        executors.shutdown();
        executors.awaitTermination(1, TimeUnit.MINUTES);

        ProductDetail productDetail = productService.getProductDetail(productDetailId);
        Inventory inventory = inventoryRepository.findById(productDetail.getInventory().getId()).get();
        assertThat(inventory.getQuantity()).isEqualTo(100);
    }

    @AfterEach
    public void terminate() {
        productDetailRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }
}

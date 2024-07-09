package jshop.integration.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import jshop.domain.category.repository.CategoryRepository;
import jshop.domain.category.service.CategoryService;
import jshop.domain.inventory.entity.Inventory;
import jshop.domain.inventory.repository.InventoryHistoryRepository;
import jshop.domain.inventory.repository.InventoryRepository;
import jshop.domain.product.dto.CreateProductDetailRequest;
import jshop.domain.product.entity.ProductDetail;
import jshop.domain.product.repository.ProductDetailRepository;
import jshop.domain.product.repository.ProductRepository;
import jshop.domain.product.service.ProductService;
import jshop.domain.user.repository.UserRepository;
import jshop.domain.user.service.UserService;
import jshop.utils.dto.CategoryDtoUtils;
import jshop.utils.dto.ProductDtoUtils;
import jshop.utils.dto.UserDtoUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("[통합 테스트] 상품 재고 동기화 테스트")
public class ProductStockSyncTest {

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

    @BeforeEach
    public void init(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper,
        @Autowired CategoryService categoryService, @Autowired ProductService productService) throws Exception {

        sellerUserId = userService.joinUser(UserDtoUtils.getJoinUserRequestDto());
        categoryId = categoryService.createCategory(CategoryDtoUtils.getCreateCategoryRequest());
        productId = productService.createProduct(ProductDtoUtils.getCreateProductRequest(categoryId), sellerUserId);
        productDetailId = productService.createProductDetail(ProductDtoUtils.getCreateProductDetailRequest().get(0),
            productId);
    }

    @Test
    @DisplayName("동시에 여러번의 재고 변화 요청이 있더라도, 모두 반영되어야 함. (동시성 문제)")
    public void changeStock_sync() throws Exception {
        /**
         * TODO
         * 다른 테스트에 영향을 주지 않기 위해 하나의 트랜잭션 내부에서 비동기 요청으로 10개의 요청을 보내는것이 가능한가?
         */

        // given
        String updateProductDetailStockRequestStr = """
            { "quantity" : 10 }
            """;

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

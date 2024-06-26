package jshop.integration.domain.product;


import jshop.domain.product.service.ProductService;
import jshop.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootTest
@DisplayName("통합테스트 AddressController")
public class ProductServiceIntegrationTest {

    @Autowired
    ProductService productService;

    @Autowired
    private UserRepository userRepository;


    @Test
    public void test() {

        productService.deleteProductDetail(2L, 1L);
    }
}
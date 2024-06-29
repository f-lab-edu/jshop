package jshop.integration.domain.product;


import static jshop.utils.MockSecurityContextUtil.mockUserSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import jshop.domain.product.service.ProductService;
import jshop.domain.user.repository.UserRepository;
import jshop.global.jwt.dto.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("통합테스트 AddressController")
public class ProductServiceIntegrationTest {

    @Autowired
    ProductService productService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void test() throws Exception {
        CustomUserDetails customUserDetails = CustomUserDetails
            .builder().id(1L).username("kim").password("lee").build();
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null,
            customUserDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authToken);
        SecurityContextHolder.setContext(context);
        String str = """
            { "price" : 1000}
            """;
        mockMvc.perform(post("/api/products/1/details")
            .with(mockUserSecurityContext())
            .contentType(MediaType.APPLICATION_JSON)
            .content(str));
    }
}
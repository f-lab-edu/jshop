package jshop.domain.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jshop.domain.category.entity.Category;
import jshop.domain.category.repository.CategoryRepository;
import jshop.domain.product.dto.CreateProductRequest;
import jshop.domain.product.entity.Product;
import jshop.domain.product.repository.ProductRepository;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Captor
    private ArgumentCaptor<Product> productCaptor;


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
            .builder().username("1").build();

        Category category = Category
            .builder().name("category").build();

        // when
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        productService.createProduct(createProductRequest, 1L);

        // then
        verify(productRepository, times(1)).save(productCaptor.capture());

        assertThat(productCaptor.getValue().getOwner()).isEqualTo(user);
        assertThat(productCaptor.getValue().getCategory()).isEqualTo(category);
        assertThat(productCaptor.getValue().getName()).isEqualTo(createProductRequest.getName());
    }
}
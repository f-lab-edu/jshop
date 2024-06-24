package jshop.domain.product.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jshop.domain.product.entity.Product;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void 상품생성() {
        // given
        Map<String, List<String>> attributes = new HashMap<>();
        List<String> colorList = List.of("red", "blue", "yellow");
        attributes.put("color", colorList);
        List<String> sizeList = List.of("95", "100", "105");
        attributes.put("size", sizeList);
        Product p = Product
            .builder().name("test").attributes(attributes).build();
        // when

        productRepository.save(p);

        // then
        Optional<Product> optionalFindProduct = productRepository.findById(p.getId());
        optionalFindProduct.ifPresentOrElse((product) -> {
            assertThat(product.getName()).isEqualTo("test");
            assertThat(product.getAttributes().get("color")).isEqualTo(colorList);
            assertThat(product.getAttributes().get("size")).isEqualTo(sizeList);
        }, () -> {
            Assertions.fail("fail");
        });

        Product byAttributes = productRepository.findByAttributes(attributes);

        System.out.println(byAttributes);
    }

    @Test
    public void 유저상품가져오기() {
        // given
        User user = User
            .builder().username("kim").build();

        Product product1 = Product
            .builder().name("아이폰15").owner(user).build();

        Product product2 = Product
            .builder().name("아이폰14").owner(user).build();

        Product product3 = Product
            .builder().name("아이폰13").owner(user).build();

        userRepository.save(user);
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        // when
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Direction.ASC, "id"));
        Page<Product> productPage = productRepository.findByOwner(user, pageRequest);

        PageRequest pageRequest2 = PageRequest.of(1, 2, Sort.by(Direction.ASC, "id"));
        Page<Product> productPage2 = productRepository.findByOwner(user, pageRequest2);

        // then

        assertThat(productPage.getTotalElements()).isEqualTo(3L);
        assertThat(productPage.getTotalPages()).isEqualTo(2);
        assertThat(productPage.getContent().contains(product1)).isTrue();

        assertThat(productPage2.getTotalElements()).isEqualTo(3L);
        assertThat(productPage2.getTotalPages()).isEqualTo(2);
        assertThat(productPage2.getNumberOfElements()).isEqualTo(1);
        assertThat(productPage2.getContent().contains(product3)).isTrue();
    }

}
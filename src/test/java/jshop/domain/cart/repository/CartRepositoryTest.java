package jshop.domain.cart.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jshop.domain.cart.entity.Cart;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@DisplayName("[단위 테스트] CartRepository")
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("유저이름으로 장바구니 찾기 검증")
    public void findCartByUserId_success() {
        // given
        Cart cart = Cart
            .builder().build();

        User user = User
            .builder().cart(cart).build();

        // when
        userRepository.save(user);

        // then
        cartRepository.findCartByUserId(user.getId()).ifPresentOrElse(foundCart -> {
            assertThat(foundCart).isNotNull();
        }, () -> {
            Assertions.fail();
        });
    }
}
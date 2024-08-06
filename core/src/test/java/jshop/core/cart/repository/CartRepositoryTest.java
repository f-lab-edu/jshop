package jshop.core.cart.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jshop.core.domain.cart.entity.Cart;
import jshop.core.domain.cart.repository.CartRepository;
import jshop.core.domain.user.entity.User;
import jshop.core.domain.user.repository.UserRepository;
import jshop.common.test.BaseTestContainers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@DisplayName("[단위 테스트] CartRepository")
@Transactional
@AutoConfigureTestDatabase(replace = Replace.NONE)
class CartRepositoryTest extends BaseTestContainers {

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
package jshop.domain.user.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import jshop.domain.address.dto.AddressInfoResponse;
import jshop.domain.address.entity.Address;
import jshop.domain.address.repository.AddressRepository;
import jshop.domain.product.entity.Product;
import jshop.domain.product.repository.ProductRepository;
import jshop.domain.user.dto.UserInfoResponse;
import jshop.domain.user.dto.UserType;
import jshop.domain.user.entity.User;
import jshop.domain.wallet.entity.Wallet;
import jshop.global.config.P6SpyConfig;
import jshop.global.exception.JshopException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(P6SpyConfig.class)
public class UserRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;


    @Test
    public void test() {
        User user = User
            .builder().username("kim").build();
        userRepository.save(user);

        em.flush();
        em.clear();

        User referenceById = userRepository.getReferenceById(user.getId());
        Product product = Product
            .builder().owner(referenceById).build();

        productRepository.save(product);

        em.flush();
        em.clear();

        Optional<Product> byId = productRepository.findById(product.getId());
        byId.ifPresent((p) -> {
            System.out.println("==================================");
            System.out.println(p.getOwner().getUsername());
        });
    }

    @Test
    public void 회원가입() {
        // given
        User user = User
            .builder()
            .email("test")
            .userType(UserType.USER)
            .username("kim")
            .password("kim")
            .build();

        // when
        userRepository.save(user);

        // then
        User findUser = userRepository.findByEmail("test");
        assertThat(findUser).isEqualTo(user);
    }

    @Test
    public void 중복이메일_제약조건() {
        // given
        User user1 = User
            .builder()
            .email("test")
            .userType(UserType.USER)
            .username("kim")
            .password("kim")
            .build();

        User user2 = User
            .builder()
            .email("test")
            .userType(UserType.USER)
            .username("kim1")
            .password("kim2")
            .build();

        // when
        userRepository.save(user1);
        em.flush();

        // then
        userRepository.save(user2);
        assertThrows(ConstraintViolationException.class, () -> em.flush());
    }

    @Test
    public void 패치조인확인() {
        /**
         * findById의 패치조인 확인.
         */
        // given
        Wallet wallet = Wallet
            .builder().balance(0L).build();

        User user = User
            .builder()
            .email("test")
            .userType(UserType.USER)
            .username("kim")
            .password("kim")
            .wallet(wallet)
            .build();

        Address address = Address
            .builder()
            .receiverName("김재현")
            .receiverNumber("010-1234-5678")
            .province("경기도")
            .city("광주시")
            .district("송정동")
            .street("경안천로")
            .detailAddress1("123-1234")
            .detailAddress2(null)
            .message("문앞에 놔주세요")
            .user(user)
            .build();

        userRepository.save(user);
        addressRepository.save(address);

        // when
        em.flush();
        em.clear();

        System.out.println("user 조회 before");
        Optional<User> optionalFindUser = userRepository.findById(user.getId());
        System.out.println("user 조회 after");
        User findUser = optionalFindUser.orElseThrow(JshopException::new);

        System.out.println("address 조회 before");
        List<AddressInfoResponse> findAddresses = addressRepository
            .findByUser(findUser)
            .stream()
            .map(AddressInfoResponse::of)
            .toList();
        System.out.println("address 조회 after");

        UserInfoResponse userInfoResponse = UserInfoResponse
            .builder()
            .username(findUser.getUsername())
            .email(findUser.getEmail())
            .userType(findUser.getUserType())
            .balance(findUser.getWallet().getBalance())
            .addresses(findAddresses)
            .build();
        // then
        System.out.println("userInfoResponse 출력");
        System.out.println(userInfoResponse);
    }
}
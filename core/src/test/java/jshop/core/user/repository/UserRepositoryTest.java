package jshop.core.user.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import jshop.core.domain.address.dto.AddressInfoResponse;
import jshop.core.domain.address.entity.Address;
import jshop.core.domain.address.repository.AddressRepository;
import jshop.core.domain.product.repository.ProductRepository;
import jshop.core.domain.user.dto.UserInfoResponse;
import jshop.core.domain.user.dto.UserType;
import jshop.core.domain.user.entity.User;
import jshop.core.domain.user.repository.UserRepository;
import jshop.core.domain.wallet.entity.Wallet;
import jshop.core.config.P6SpyConfig;
import jshop.common.exception.JshopException;
import jshop.common.test.BaseTestContainers;
import org.hibernate.Hibernate;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Import(P6SpyConfig.class)
@DisplayName("[단위 테스트] UserRepository")
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserRepositoryTest extends BaseTestContainers {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;


    @Test
    @DisplayName("이메일이 중복되지 않는 유저를 생성할 수 있다.")
    public void createUser() {
        // given
        User user = User
            .builder().email("test").userType(UserType.USER).username("kim").password("kim").build();

        // when
        userRepository.save(user);

        // then
        User findUser = userRepository.findByEmail("test");
        assertThat(findUser).isEqualTo(user);
    }

    @Test
    @DisplayName("이메일이 중복된다면 중복 이메일 제약조건에 걸린다.")
    public void constraints_dup_email() {
        // given
        User user1 = User
            .builder().email("test").userType(UserType.USER).username("kim").password("kim").build();

        User user2 = User
            .builder().email("test").userType(UserType.USER).username("kim1").password("kim2").build();

        // when
        userRepository.save(user1);
        em.flush();

        // then
        userRepository.save(user2);
        assertThrows(ConstraintViolationException.class, () -> em.flush());
    }

    @Test
    @DisplayName("findById 는 Wallet 만 패치조인 한다.")
    public void check_patchjoin() {
        // given
        Wallet wallet = Wallet
            .builder().balance(0L).build();

        User user = User
            .builder().email("test").userType(UserType.USER).username("kim").password("kim").wallet(wallet).build();

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

        Optional<User> optionalFindUser = userRepository.findById(user.getId());
        User findUser = optionalFindUser.orElseThrow(JshopException::new);

        // then
        assertThat(Hibernate.isInitialized(findUser.getAddresses())).isFalse();
        assertThat(Hibernate.isInitialized(findUser.getWallet())).isTrue();
    }

    @Test
    @DisplayName("findUserWithWalletAndAddressById 는 유저 조회시 Wallet, Address 정보를 같이 가져온다.")
    public void check_findUserWithWalletAndAddressById() {
        /**
         * findById의 패치조인 확인.
         */
        // given
        Wallet wallet = Wallet
            .builder().balance(0L).build();

        User user = User
            .builder().email("test").userType(UserType.USER).username("kim").password("kim").wallet(wallet).build();

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

        Optional<User> optionalFindUser = userRepository.findUserWithWalletAndAddressById(user.getId());
        User findUser = optionalFindUser.orElseThrow(JshopException::new);


        // then
        assertThat(Hibernate.isInitialized(findUser.getAddresses())).isTrue();
        assertThat(Hibernate.isInitialized(findUser.getWallet())).isTrue();
    }
}
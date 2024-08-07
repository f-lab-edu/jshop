package jshop.core.address.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import jshop.core.domain.address.entity.Address;
import jshop.core.domain.address.repository.AddressRepository;
import jshop.core.domain.user.entity.User;
import jshop.core.domain.user.repository.UserRepository;
import jshop.common.test.BaseTestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@DisplayName("[단위 테스트] AddressRepository")
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class AddressRepositoryBaseTest extends BaseTestContainers {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Nested
    class FindById {

        private User user;
        private Address address1, address2;

        @BeforeEach
        public void init() {
            user = User
                .builder().username("kim").role("ROLE_USER").build();

            address1 = Address
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

            address2 = Address
                .builder()
                .receiverName("김재현")
                .receiverNumber("010-1234-5678")
                .province("부산광역시")
                .city("금정구")
                .district("중앙대로")
                .street("중앙대로")
                .detailAddress1("123-1234")
                .detailAddress2(" ")
                .message("문앞에 놔주세요")
                .user(user)
                .build();
        }

        @Test
        @DisplayName("유저에 속한 모든 주소를 가져올 수 있다.")
        public void findByUser() {
            // given
            userRepository.save(user);

            // when
            addressRepository.save(address1);
            addressRepository.save(address2);

            // then
            List<Address> addresses = addressRepository.findByUser(user);
            assertThat(addresses.size()).isEqualTo(2);
            assertThat(addresses).contains(address1);
            assertThat(addresses).contains(address2);

        }
    }

    @Nested
    @DisplayName("유저가 자신의 주소 삭제 테스트")
    class DeleteUser {

        @Test
        @DisplayName("유저는 자신과 연관된 주소를 삭제할 수 있다")
        public void deleteUser_success() {
            // given
            User user = User
                .builder().username("kim").build();

            Address address = Address
                .builder().city("광주시").user(user).build();

            // when
            userRepository.save(user);
            addressRepository.save(address);
            address.delete();
            em.flush();
            em.clear();
            // then

            Address findAddress = addressRepository.findById(address.getId()).get();
            assertThat(findAddress.getUser()).isNull();

        }
    }
}
package jshop.domain.address.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import jshop.domain.address.entity.Address;
import jshop.domain.user.dto.UserType;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class AddressRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Test
    public void 주소생성() {
        // given
        User u = User
            .builder()
            .username("kim")
            .role("ROLE_USER")
            .build();

        userRepository.save(u);

        Address newAddress = Address
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
            .user(u)
            .build();

        // when
        addressRepository.save(newAddress);

        // then
        Optional<Address> optionalFindAddress = addressRepository.findById(newAddress.getId());
        optionalFindAddress.ifPresentOrElse((address) -> {
            assertThat(address).isEqualTo(newAddress);
        }, () -> {
            Assertions.fail();
        });
    }

    @Test
    public void 유저단위검색() {
        // given
        User u = User
            .builder()
            .username("kim")
            .role("ROLE_USER")
            .build();
        userRepository.save(u);

        Address newAddress1 = Address
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
            .user(u)
            .build();

        Address newAddress2 = Address
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
            .user(u)
            .build();

        // when
        addressRepository.save(newAddress1);
        addressRepository.save(newAddress2);

        // then
        List<Address> addresses = addressRepository.findByUser(u);
        assertThat(addresses.size()).isEqualTo(2);
        assertThat(addresses).contains(newAddress1);
        assertThat(addresses).contains(newAddress2);

    }
}
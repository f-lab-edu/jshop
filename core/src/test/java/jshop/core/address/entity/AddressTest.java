package jshop.core.address.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import jshop.core.domain.address.dto.CreateAddressRequest;
import jshop.core.domain.address.entity.Address;
import jshop.core.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[단위 테스트] Address")
class AddressTest {

    @Test
    @DisplayName("Address 생성 테스트")
    public void of_success() {
        // given
        CreateAddressRequest createAddressRequest = CreateAddressRequest
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
            .build();

        User user = User
            .builder().id(1L).username("user").password("password").email("email@email.com").role("ROLE_USER").build();

        // when
        Address address = Address.of(createAddressRequest, user);

        // then
        assertAll("Adress 생성 검증",
            () -> assertThat(address.getReceiverName()).isEqualTo(createAddressRequest.getReceiverName()),
            () -> assertThat(address.getReceiverNumber()).isEqualTo(createAddressRequest.getReceiverNumber()),
            () -> assertThat(address.getProvince()).isEqualTo(createAddressRequest.getProvince()),
            () -> assertThat(address.getCity()).isEqualTo(createAddressRequest.getCity()),
            () -> assertThat(address.getDistrict()).isEqualTo(createAddressRequest.getDistrict()),
            () -> assertThat(address.getStreet()).isEqualTo(createAddressRequest.getStreet()),
            () -> assertThat(address.getDetailAddress1()).isEqualTo(createAddressRequest.getDetailAddress1()),
            () -> assertThat(address.getDetailAddress2()).isEqualTo(createAddressRequest.getDetailAddress2()),
            () -> assertThat(address.getMessage()).isEqualTo(createAddressRequest.getMessage()));
    }
}
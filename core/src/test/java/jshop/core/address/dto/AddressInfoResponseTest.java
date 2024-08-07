package jshop.core.address.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import jshop.core.domain.address.dto.AddressInfoResponse;
import jshop.core.domain.address.entity.Address;
import jshop.core.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[단위 테스트] ]AddressInfoResponse")
class AddressInfoResponseTest {

    @Test
    @DisplayName("Address로 AddressInfoResponse 생성 검증")
    void of_success() {
        // given
        User user = User
            .builder().id(1L).username("user").password("password").email("email@email.com").role("ROLE_USER").build();

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

        // when
        AddressInfoResponse addressInfoResponse = AddressInfoResponse.of(address);

        // then
        assertAll("AddressInfoResponse  검증",
            () -> assertThat(addressInfoResponse.getReceiverName()).isEqualTo(address.getReceiverName()),
            () -> assertThat(addressInfoResponse.getReceiverNumber()).isEqualTo(address.getReceiverNumber()),
            () -> assertThat(addressInfoResponse.getProvince()).isEqualTo(address.getProvince()),
            () -> assertThat(addressInfoResponse.getCity()).isEqualTo(address.getCity()),
            () -> assertThat(addressInfoResponse.getDistrict()).isEqualTo(address.getDistrict()),
            () -> assertThat(addressInfoResponse.getStreet()).isEqualTo(address.getStreet()),
            () -> assertThat(addressInfoResponse.getDetailAddress1()).isEqualTo(address.getDetailAddress1()),
            () -> assertThat(addressInfoResponse.getDetailAddress2()).isEqualTo(address.getDetailAddress2()),
            () -> assertThat(addressInfoResponse.getMessage()).isEqualTo(address.getMessage()));
    }
}
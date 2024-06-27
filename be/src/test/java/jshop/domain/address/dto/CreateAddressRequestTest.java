package jshop.domain.address.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import jshop.domain.address.entity.Address;
import jshop.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CreaateAddressRequest DTO 테스트")
class CreateAddressRequestTest {

    @Test
    @DisplayName("Address로 CreateAddressRequest 생성 검증")
    public void of_success() {
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
        CreateAddressRequest createAddressRequest = CreateAddressRequest.of(address);

        // then
        assertAll("CreateAddressRequest 검증",
            () -> assertThat(createAddressRequest.getReceiverName()).isEqualTo(address.getReceiverName()),
            () -> assertThat(createAddressRequest.getReceiverNumber()).isEqualTo(address.getReceiverNumber()),
            () -> assertThat(createAddressRequest.getProvince()).isEqualTo(address.getProvince()),
            () -> assertThat(createAddressRequest.getCity()).isEqualTo(address.getCity()),
            () -> assertThat(createAddressRequest.getDistrict()).isEqualTo(address.getDistrict()),
            () -> assertThat(createAddressRequest.getStreet()).isEqualTo(address.getStreet()),
            () -> assertThat(createAddressRequest.getDetailAddress1()).isEqualTo(address.getDetailAddress1()),
            () -> assertThat(createAddressRequest.getDetailAddress2()).isEqualTo(address.getDetailAddress2()),
            () -> assertThat(createAddressRequest.getMessage()).isEqualTo(address.getMessage()));

    }
}
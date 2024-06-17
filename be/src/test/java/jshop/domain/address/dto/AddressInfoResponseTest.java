package jshop.domain.address.dto;

import static org.junit.jupiter.api.Assertions.*;

import jshop.domain.address.entity.Address;
import jshop.domain.user.entity.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AddressInfoResponseTest {

    @Test
    void ofAddress() {
        // given
        User user = User
            .builder()
            .id(1L)
            .username("user")
            .password("password")
            .email("email@email.com")
            .role("ROLE_USER")
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

        // when
        AddressInfoResponse addressInfoResponse = AddressInfoResponse.ofAddress(address);

        // then
        assertThat(addressInfoResponse.getReceiverName()).isEqualTo(address.getReceiverName());
        assertThat(addressInfoResponse.getReceiverNumber()).isEqualTo(address.getReceiverNumber());
        assertThat(addressInfoResponse.getProvince()).isEqualTo(address.getProvince());
        assertThat(addressInfoResponse.getCity()).isEqualTo(address.getCity());
        assertThat(addressInfoResponse.getDistrict()).isEqualTo(address.getDistrict());
        assertThat(addressInfoResponse.getStreet()).isEqualTo(address.getStreet());
        assertThat(addressInfoResponse.getDetailAddress1()).isEqualTo(address.getDetailAddress1());
        assertThat(addressInfoResponse.getDetailAddress2()).isEqualTo(address.getDetailAddress2());
        assertThat(addressInfoResponse.getMessage()).isEqualTo(address.getMessage());
    }
}
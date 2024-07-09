package jshop.utils.dto;

import jshop.domain.address.dto.CreateAddressRequest;

public class AddressDtoUtils {

    public static CreateAddressRequest getCreateAddressRequest() {
        return CreateAddressRequest
            .builder()
            .receiverName("username")
            .receiverNumber("1234")
            .province("province")
            .city("city")
            .district("district")
            .street("street")
            .detailAddress1("detail address 1")
            .detailAddress2("detail address 2")
            .message("message")
            .build();
    }
}

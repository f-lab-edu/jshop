package jshop.core.domain.address.dto;

import jshop.core.domain.address.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class AddressInfoResponse {

    private String receiverName;
    private String receiverNumber;
    private String province;
    private String city;
    private String district;
    private String street;

    private String detailAddress1;
    private String detailAddress2;
    private String message;

    public static AddressInfoResponse of(Address address) {
        return AddressInfoResponse
            .builder()
            .receiverName(address.getReceiverName())
            .receiverNumber(address.getReceiverNumber())
            .message(address.getMessage())
            .province(address.getProvince())
            .city(address.getCity())
            .district(address.getDistrict())
            .street(address.getStreet())
            .detailAddress1(address.getDetailAddress1())
            .detailAddress2(address.getDetailAddress2())
            .build();
    }
}

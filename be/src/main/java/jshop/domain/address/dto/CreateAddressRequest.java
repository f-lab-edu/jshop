package jshop.domain.address.dto;

import jakarta.validation.constraints.NotBlank;
import jshop.domain.address.entity.Address;
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
public class CreateAddressRequest {

    @NotBlank(message = "받는 사람은 공백일 수 없습니다.")
    private String receiverName;

    @NotBlank(message = "받는 사람 전화번호는 공백일 수 없습니다.")
    private String receiverNumber;

    @NotBlank(message = "도 / 시 는 공백일 수 없습니다.")
    private String province;

    @NotBlank(message = "시 는 공백일 수 없습니다.")
    private String city;

    @NotBlank(message = "구 / 군 은 공백일 수 없습니다.")
    private String district;

    @NotBlank(message = "도로명은 공백일 수 없습니다.")
    private String street;

    private String detailAddress1;
    private String detailAddress2;
    private String message;

    public static CreateAddressRequest ofAddress(Address address) {
        return CreateAddressRequest
            .builder()
            .receiverName(address.getReceiverName())
            .receiverNumber(address.getReceiverNumber())
            .province(address.getProvince())
            .city(address.getCity())
            .district(address.getDistrict())
            .street(address.getStreet())
            .detailAddress1(address.getDetailAddress1())
            .detailAddress2(address.getDetailAddress2())
            .message(address.getMessage())
            .build();
    }
}

package jshop.domain.address;

import jakarta.validation.constraints.NotBlank;
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
public class SaveAddressDto {

    @NotBlank(message = "받는 사람은 공백일 수 없습니다.")
    private String receiverName;

    @NotBlank(message = "받는 사람 전화번호는 공백일 수 없습니다.")
    private String receiverNumber;

    private String message;

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
}

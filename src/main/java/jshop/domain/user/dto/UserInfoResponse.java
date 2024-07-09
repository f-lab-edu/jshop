package jshop.domain.user.dto;

import java.util.List;
import jshop.domain.address.dto.AddressInfoResponse;
import jshop.domain.address.entity.Address;
import jshop.domain.user.entity.User;
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
public class UserInfoResponse {

    private String username;
    private String email;
    private UserType userType;
    private Long balance;
    private List<AddressInfoResponse> addresses;

    public static UserInfoResponse of(User user, List<Address> addresses) {
        List<AddressInfoResponse> addressInfoResponses = addresses.stream().map(AddressInfoResponse::of).toList();

        return UserInfoResponse
            .builder()
            .username(user.getUsername())
            .email(user.getEmail())
            .balance(user.getWallet().getBalance())
            .userType(user.getUserType())
            .addresses(addressInfoResponses)
            .build();
    }
}

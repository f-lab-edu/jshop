package jshop.domain.user.dto;

import java.util.List;
import jshop.domain.address.entity.Address;
import jshop.domain.wallet.entity.Wallet;
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
    private List<Address> addresses;
}

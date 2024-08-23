package jshop.core.domain.user.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import jshop.core.domain.address.entity.Address;
import jshop.core.domain.cart.entity.Cart;
import jshop.core.domain.user.dto.JoinUserRequest;
import jshop.core.domain.user.dto.UpdateUserRequest;
import jshop.core.domain.user.dto.UserType;
import jshop.core.domain.wallet.entity.Wallet;
import jshop.common.exception.ErrorCode;
import jshop.core.common.entity.BaseEntity;
import jshop.common.exception.JshopException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"cart", "wallet", "addresses"})
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private String username;
    private String password;

    @Column(unique = true)
    private String email;
    private String role;

    /**
     * 사용자는 일반사용자, 판매자 두개의 타입이 있다.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type")
    private UserType userType;

    /**
     * 사용자는 하나의 카트를 갖는다.
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    /**
     * 사용자는 하나의 지갑을 갖는다.
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    public static User of(JoinUserRequest joinUserRequest, String encryptedPassword) {
        Wallet wallet = Wallet.create();
        Cart cart = Cart.create();

        return User
            .builder()
            .username(joinUserRequest.getUsername())
            .password(encryptedPassword)
            .email(joinUserRequest.getEmail())
            .userType(joinUserRequest.getUserType())
            .role("ROLE_USER")
            .wallet(wallet)
            .cart(cart)
            .build();
    }

    public Wallet getWallet() {
        return Optional.ofNullable(wallet).orElseThrow(() -> {
            log.error(ErrorCode.USER_WALLET_NOT_FOUND.getLogMessage(), id);
            throw JshopException.of(ErrorCode.USER_WALLET_NOT_FOUND);
        });
    }

    public void updateUserInfo(UpdateUserRequest updateUserRequest) {
        if (updateUserRequest.getUsername() != null) {
            this.username = updateUserRequest.getUsername();
        } else {
            log.warn("Username은 null 이 될 수 없습니다.");
        }
    }
}

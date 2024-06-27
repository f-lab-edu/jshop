package jshop.domain.user.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jshop.domain.cart.entity.Cart;
import jshop.domain.user.dto.UpdateUserRequest;
import jshop.domain.user.dto.UserType;
import jshop.domain.wallet.entity.Wallet;
import jshop.global.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@ToString(exclude = {"cart", "wallet"})
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

    public void updateUserInfo(UpdateUserRequest updateUserRequest) {
        if (updateUserRequest.getUsername() != null) {
            this.username = updateUserRequest.getUsername();
        } else {
            log.warn("Username은 null 이 될 수 없습니다.");
        }

    }

}

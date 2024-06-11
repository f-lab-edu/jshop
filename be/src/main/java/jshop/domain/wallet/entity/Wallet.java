package jshop.domain.wallet.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jshop.domain.user.entity.User;
import jshop.global.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "wallet")
public class Wallet extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "wallet_id")
    private Long id;

    /**
     * 지갑은 사용자와 일대일 대응된다.
     */
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "wallet")
    private User user;

    private long balance;
}

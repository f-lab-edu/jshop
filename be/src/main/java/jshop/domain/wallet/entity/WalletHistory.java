package jshop.domain.wallet.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "wallet_history")
public class WalletHistory extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "wallet_history_id")
    private Long id;

    /**
     * 하나의 지갑은 여러개의 히스토리를 가질 수 있다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    /**
     * 구매, 판매, 입금, 출금의 타입을 갖는다.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "wallet_change_type")
    private WalletChangeType changeType;

    private Long change_balance;
    private Long old_balance;
    private Long new_balance;
}

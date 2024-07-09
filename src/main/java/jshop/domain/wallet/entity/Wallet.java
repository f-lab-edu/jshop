package jshop.domain.wallet.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jshop.global.common.ErrorCode;
import jshop.global.entity.BaseEntity;
import jshop.global.exception.JshopException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.Audited;

@Slf4j
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
@Table(name = "wallet")
@Audited
public class Wallet extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "wallet_id")
    private Long id;

    private Long balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "wallet_change_type")
    private WalletChangeType walletChangeType;

    public static Wallet create() {
        return create(0L);
    }

    public static Wallet create(Long balance) {
        return Wallet
            .builder().balance(balance).walletChangeType(WalletChangeType.CREATE).build();
    }


    public Long deposit(Long amount) {
        checkAmount(amount);
        walletChangeType = WalletChangeType.DEPOSIT;
        return balance += amount;
    }

    public Long refund(Long amount) {
        checkAmount(amount);
        walletChangeType = WalletChangeType.REFUND;
        return balance += amount;
    }

    public Long purchase(Long amount) {
        checkAmount(amount);
        if (balance - amount < 0) {
            log.error(ErrorCode.WALLET_BALANCE_EXCEPTION.getLogMessage(), balance - amount);
            throw JshopException.of(ErrorCode.WALLET_BALANCE_EXCEPTION);
        }

        walletChangeType = WalletChangeType.PURCHASE;
        return balance -= amount;
    }

    public Long withdraw(Long amount) {
        checkAmount(amount);
        if (balance - amount < 0) {
            log.error(ErrorCode.WALLET_BALANCE_EXCEPTION.getLogMessage(), balance - amount);
            throw JshopException.of(ErrorCode.WALLET_BALANCE_EXCEPTION);
        }

        walletChangeType = WalletChangeType.WITHDRAW;
        return balance -= amount;
    }

    private boolean checkAmount(Long amount) {
        if (amount <= 0) {
            log.error(ErrorCode.ILLEGAL_BALANCE_REQUEST.getLogMessage(), amount);
            throw JshopException.of(ErrorCode.ILLEGAL_BALANCE_REQUEST);
        }
        return true;
    }
}

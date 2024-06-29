package jshop.domain.wallet.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Slf4j
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
@Table(name = "wallet")
public class Wallet extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "wallet_id")
    private Long id;

    private Long balance;

    public void updateBalance(Long balanceDelta) {
        if (this.balance + balanceDelta < 0) {
            log.error(ErrorCode.WALLET_BALANCE_EXCEPTION.getLogMessage(), this.balance + balanceDelta);
            throw JshopException.of(ErrorCode.WALLET_BALANCE_EXCEPTION);
        }

        this.balance += balanceDelta;
    }
}

package jshop.domain.user.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jshop.domain.wallet.entity.WalletChangeType;
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
public class UpdateWalletBalanceRequest {

    @NotNull(message = "변경 잔고는 공백일 수 없습니다.")
    @Positive
    private Long amount;

    @NotNull(message = "변경 타입은 공백일 수 없습니다.")
    private WalletChangeType type;
}

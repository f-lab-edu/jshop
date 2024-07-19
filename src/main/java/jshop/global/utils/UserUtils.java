package jshop.global.utils;

import java.util.Optional;
import jshop.domain.user.entity.User;
import jshop.domain.wallet.entity.Wallet;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserUtils {

    public static User getUserOrThrow(Optional<User> optionalUser, Long userId) {
        return optionalUser.orElseThrow(() -> {
            log.error(ErrorCode.USERID_NOT_FOUND.getLogMessage(), userId);
            throw JshopException.of(ErrorCode.USERID_NOT_FOUND);
        });
    }

    public static Wallet getWalletOrThrow(Optional<Wallet> optionalWallet, Long userId) {
        return optionalWallet.orElseThrow(() -> {
            log.error(ErrorCode.USER_WALLET_NOT_FOUND.getLogMessage(), userId);
            throw JshopException.of(ErrorCode.USER_WALLET_NOT_FOUND);
        });
    }
}

package jshop.global.utils;

import java.util.Optional;
import jshop.domain.user.entity.User;
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
}

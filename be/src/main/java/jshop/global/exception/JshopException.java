package jshop.global.exception;

import jshop.global.common.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JshopException extends RuntimeException {

    private ErrorCode errorCode;

    public JshopException(String message) {
        super(message);
    }

    public static JshopException of(ErrorCode errorCode) {
        return JshopException
            .builder().errorCode(errorCode).build();
    }

}

package jshop.common.exception;

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

    public JshopException(String message, Throwable cause) {
        super(message, cause);
    }

    public static JshopException of(ErrorCode errorCode) {
        return JshopException
            .builder().errorCode(errorCode).build();
    }


}

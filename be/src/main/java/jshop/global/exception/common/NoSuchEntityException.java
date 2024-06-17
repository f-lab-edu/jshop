package jshop.global.exception.common;

import jshop.global.common.ErrorCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NoSuchEntityException extends RuntimeException {

    private ErrorCode errorCode;

    public NoSuchEntityException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}

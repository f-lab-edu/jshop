package jshop.global.exception.common;

import jshop.global.common.ErrorCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AlreadyExistsException extends RuntimeException {

    private ErrorCode errorCode;

    public AlreadyExistsException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}

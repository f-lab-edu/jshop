package jshop.global.exception.common;

import jshop.global.common.ErrorCode;

public class EntityNotFoundException extends RuntimeException {

    private ErrorCode errorCode;
    
    public EntityNotFoundException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}

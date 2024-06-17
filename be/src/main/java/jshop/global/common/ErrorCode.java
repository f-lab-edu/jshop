package jshop.global.common;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorCode {
    ALREADY_REGISTERED_EMAIL(1001),
    JWT_USER_NOT_FOUND(2001),
    BAD_TOKEN(2010),
    TOKEN_EXPIRED(2011),
    USERID_NOT_FOUND(3001),
    INVALID_REQUEST_BODY(10001);


    private int code;

    private ErrorCode(int code) {
        this.code = code;
    }

    @JsonValue
    public int getResponseCode() {
        return code;
    }
}

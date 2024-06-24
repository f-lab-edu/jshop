package jshop.global.common;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorCode {
    ALREADY_REGISTERED_EMAIL(1001),
    JWT_USER_NOT_FOUND(2001),
    BAD_TOKEN(2010),
    BAD_REQUEST(2030),
    TOKEN_EXPIRED(2011),
    USERID_NOT_FOUND(3001),
    INVALID_REQUEST_BODY(10001),
    BAD_REQUEST(10010);


    private final int code;

    ErrorCode(int code) {
        this.code = code;
    }

    @JsonValue
    public int getResponseCode() {
        return code;
    }
}

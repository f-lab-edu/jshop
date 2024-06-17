package jshop.global.common;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorCode {
    ALREADY_REGISTERED_EMAIL(1001),
    ALREADY_EXISTS_PRODUCT_DETAIL(1005),
    JWT_USER_NOT_FOUND(2001),
    BAD_TOKEN(2010),
    TOKEN_EXPIRED(2011),
    USERID_NOT_FOUND(3001),
    PRODUCTID_NOT_FOUND(3002),
    CATEGORYID_NOT_FOUND(3003),
    ADDRESSID_NOT_FOUND(3004),
    UNAUTHORIZED(4001),
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

package jshop.global.common;

public enum ResponseCode {
    SUCCESS(0),
    ALREADY_REGISTERED_EMAIL(101);

    private int code;

    private ResponseCode(int code) {
        this.code = code;
    }

    public int getResponseCode() {
        return code;
    }
}

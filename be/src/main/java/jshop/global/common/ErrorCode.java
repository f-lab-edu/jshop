package jshop.global.common;

public enum ErrorCode {
  ALREADY_REGISTERED_EMAIL(101),
  INVALID_REQUEST_BODY(1001);


  private int code;

  private ErrorCode(int code) {
    this.code = code;
  }

  public int getResponseCode() {
    return code;
  }
}

package jshop.global.common;

public enum ErrorCode {
  ALREADY_REGISTERED_EMAIL(101);

  private int code;

  private ErrorCode(int code) {
    this.code = code;
  }

  public int getResponseCode() {
    return code;
  }
}

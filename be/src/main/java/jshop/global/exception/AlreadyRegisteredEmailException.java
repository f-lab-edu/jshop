package jshop.global.exception;

public class AlreadyRegisteredEmailException extends RuntimeException{
    private String detail;
    public AlreadyRegisteredEmailException(String message, String detail) {
        super(message);
        this.detail = detail;
    }
}

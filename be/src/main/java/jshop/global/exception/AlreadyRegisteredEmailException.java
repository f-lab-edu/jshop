package jshop.global.exception;

public class AlreadyRegisteredEmailException extends RuntimeException {

    public AlreadyRegisteredEmailException(String message) {
        super(message);
    }
}

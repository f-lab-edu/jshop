package jshop.global.exception.user;

public class AlreadyRegisteredEmailException extends RuntimeException {

    public AlreadyRegisteredEmailException(String message) {
        super(message);
    }
}

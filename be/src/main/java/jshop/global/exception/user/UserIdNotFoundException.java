package jshop.global.exception.user;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserIdNotFoundException extends RuntimeException {

    public UserIdNotFoundException(String message) {
        super(message);
    }
}

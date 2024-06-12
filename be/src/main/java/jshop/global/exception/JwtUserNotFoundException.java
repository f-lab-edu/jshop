package jshop.global.exception;

public class JwtUserNotFoundException extends RuntimeException {

    public JwtUserNotFoundException() {
        super("JWT 토큰으로부터 인증정보를 찾을 수 없습니다.");
    }

    public JwtUserNotFoundException(String message) {
        super(message);
    }
}

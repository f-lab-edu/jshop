package jshop.global.controller;

import java.util.Optional;
import jshop.global.common.ErrorCode;
import jshop.global.dto.Response;
import jshop.global.exception.user.AlreadyRegisteredEmailException;
import jshop.global.exception.security.JwtUserNotFoundException;
import jshop.global.exception.user.UserIdNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AlreadyRegisteredEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    protected Response handleCustomException(AlreadyRegisteredEmailException ex) {

        Response response = Response
            .builder()
            .error(ErrorCode.ALREADY_REGISTERED_EMAIL)
            .message(ex.getMessage())
            .data(null)
            .build();

        return response;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Response handleBindException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        String errorMsg = null;

        if (bindingResult.hasFieldErrors()) {
            FieldError fieldError = bindingResult.getFieldErrors().get(0);
            errorMsg = fieldError.getDefaultMessage();
        }

        Response response = Response
            .builder()
            .error(ErrorCode.INVALID_REQUEST_BODY)
            .message(Optional.ofNullable(errorMsg).orElse(ex.getMessage()))
            .data(null)
            .build();

        return response;
    }

    @ExceptionHandler(JwtUserNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected Response handleJwtNotFoundException(JwtUserNotFoundException ex) {
        return Response
            .builder()
            .error(ErrorCode.JWT_USER_NOT_FOUND)
            .message("인증정보가 잘못되었습니다.")
            .build();
    }

    @ExceptionHandler(UserIdNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Response handleUserIdNotFoundException(UserIdNotFoundException ex) {
        return Response
            .builder()
            .error(ErrorCode.USERID_NOT_FOUND)
            .message("유저를 찾지 못했습니다.")
            .build();
    }
}

package jshop.global.controller;

import jakarta.persistence.ElementCollection;
import java.util.Optional;
import jshop.global.common.ErrorCode;
import jshop.global.dto.Response;
import jshop.global.exception.category.AlreadyExistsNameCategory;
import jshop.global.exception.security.UnauthorizedException;
import jshop.global.exception.user.AlreadyRegisteredEmailException;
import jshop.global.exception.security.JwtUserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Response handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        System.out.println(ex.getMessage());
        return Response
            .builder()
            .error(ErrorCode.INVALID_REQUEST_BODY)
            .message("Request Body가 비어있습니다.")
            .build();
    }

    @ExceptionHandler(JwtUserNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected Response handleJwtNotFoundException(JwtUserNotFoundException ex) {
        return Response
            .builder().error(ErrorCode.JWT_USER_NOT_FOUND).message("인증정보가 잘못되었습니다.").build();
    }

    @ExceptionHandler(AlreadyExistsNameCategory.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Response handleAlreadyExistsNameCategory(AlreadyExistsNameCategory ex) {
        return Response
            .builder().error(ErrorCode.BAD_REQUEST).message("중복된 이름입니다.").build();
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected Response handleUnauthorizedException(UnauthorizedException ex) {
        return Response
            .builder().error(ErrorCode.UNAUTHORIZED).message("권한이 없습니다.").build();
    }
}

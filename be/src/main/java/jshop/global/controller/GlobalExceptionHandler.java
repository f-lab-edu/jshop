package jshop.global.controller;

import java.util.Optional;
import jshop.global.common.ErrorCode;
import jshop.global.dto.Response;
import jshop.global.exception.AlreadyRegisteredEmailException;
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

        Response response = Response.builder()
            .error(ErrorCode.ALREADY_REGISTERED_EMAIL)
            .message(ex.getMessage())
            .data(null)
            .build();

        return response;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Response handleBindException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        String errorMsg = null;

        if (bindingResult.hasFieldErrors()) {
            FieldError fieldError = bindingResult.getFieldErrors().get(0);
            errorMsg = fieldError.getDefaultMessage();
        }

        Response response = Response.builder()
            .error(ErrorCode.INVALID_REQUEST_BODY)
            .message(Optional.of(errorMsg).orElse(ex.getMessage()))
            .data(null)
            .build();
        return response;
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Response noArgsException(HttpMessageNotReadableException ex) {

        Response response = Response.builder()
            .error(ErrorCode.INVALID_REQUEST_BODY)
            .message(ex.getMessage())
            .data(null)
            .build();

        return response;
    }
}

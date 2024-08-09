package jshop.web.controller;

import jshop.common.exception.ErrorCode;
import jshop.web.dto.Response;
import jshop.common.exception.JshopException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Response handleBindException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        String errMsg = "";
        if (bindingResult.hasFieldErrors()) {
            FieldError fieldError = bindingResult.getFieldErrors().get(0);
            errMsg = fieldError.getDefaultMessage();
        }
        log.error(errMsg);
        return Response
            .builder().message(errMsg).errorCode(ErrorCode.BAD_REQUEST.getCode()).build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Response handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error(ex.getMessage());
        return Response
            .builder()
            .message(ErrorCode.INVALID_REQUEST_BODY.getMessage())
            .errorCode(ErrorCode.INVALID_REQUEST_BODY.getCode())
            .build();
    }

    @ExceptionHandler(JshopException.class)
    protected ResponseEntity<Response> handleJshopException(JshopException ex) {
        return new ResponseEntity<>(Response.of(ex.getErrorCode()), ex.getErrorCode().getHttpStatus());
    }
}

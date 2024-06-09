package jshop.global.controller;

import jshop.global.common.ErrorCode;
import jshop.global.dto.Response;
import jshop.global.exception.AlreadyRegisteredEmailException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AlreadyRegisteredEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    protected Response handleCustomException(AlreadyRegisteredEmailException ex) {

        Response response = Response.builder().error(ErrorCode.ALREADY_REGISTERED_EMAIL)
            .message(ex.getMessage()).data(null).build();

        return response;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class,
        HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Response handleBindException(Exception ex) {
        Response response = Response.builder().error(ErrorCode.INVALID_REQUEST_BODY)
            .message("입력 형식이 잘못되었습니다.").data(null).build();

        log.error(ex.getMessage());

        return response;
    }
}

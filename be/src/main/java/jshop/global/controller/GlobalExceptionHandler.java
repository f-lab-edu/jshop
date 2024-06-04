package jshop.global.controller;

import jshop.global.common.ResponseCode;
import jshop.global.dto.Response;
import jshop.global.exception.AlreadyRegisteredEmailException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AlreadyRegisteredEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    protected Response handleCustomException(RuntimeException ex) {
        Response response = new Response();
        response.setHeader(ResponseCode.ALREADY_REGISTERED_EMAIL, ex.getMessage());

        return response;
    }
}

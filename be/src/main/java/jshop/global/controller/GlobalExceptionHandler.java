package jshop.global.controller;

import jshop.global.common.ErrorCode;
import jshop.global.dto.Response;
import jshop.global.exception.AlreadyRegisteredEmailException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(AlreadyRegisteredEmailException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  protected Response handleCustomException(RuntimeException ex) {
    
    Response response = Response.builder()
        .error(ErrorCode.ALREADY_REGISTERED_EMAIL)
        .message(ex.getMessage())
        .data(null)
        .build();

    return response;
  }
}

package jshop.global.dto;

import jshop.global.common.ErrorCode;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Response<T> {

  private final T data;
  private final String message;
  private ErrorCode error;
}



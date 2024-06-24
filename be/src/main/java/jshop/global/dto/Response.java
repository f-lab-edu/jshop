package jshop.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jshop.global.common.ErrorCode;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(Include.NON_NULL)
public class Response<T> {

    private final T data;
    private final String message;
    private int errorCode;

    public static Response of(ErrorCode errorCode) {
        return Response
            .builder().errorCode(errorCode.getCode()).message(errorCode.getMessage()).build();
    }
}



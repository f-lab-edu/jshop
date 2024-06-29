package jshop.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jshop.global.common.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@JsonInclude(Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Response<T> {

    private T data;
    private String message;
    private Integer errorCode;

    public static Response of(ErrorCode errorCode) {
        return Response
            .builder().errorCode(errorCode.getCode()).message(errorCode.getMessage()).build();
    }
}



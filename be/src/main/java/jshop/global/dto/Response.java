package jshop.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jshop.global.common.ResponseCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
public class Response<T> {
    private ResponseHeader header;
    private T body;

    public void setHeader(ResponseCode code, String message) {
        this.header = new ResponseHeader(code, message);
    }

    public void setBody (T body) {
        this.body = body;
    }

    @Getter
    @RequiredArgsConstructor
    class ResponseHeader {
        private final ResponseCode code;
        private final String message;
    }
}

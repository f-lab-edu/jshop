package jshop.global.dto;


import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@Getter
@ToString
public class RequestLog {

    private String id;
    private String uri;
    private String method;
    private String client;
    private String protocol;
    private JsonNode body;
    private Map<String, String> headers;
    private String queries;
}

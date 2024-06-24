package jshop.global.dto;


import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collection;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@Getter
@ToString()
public class ResponseLog {

    private String id;
    private Long executeTime;
    private int status;
    private Map<String, String> headers;
    private JsonNode body;

}

package jshop.web.dto;


import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString()
public class ResponseLog {

    private String id;
    private Long executeTime;
    private int status;
    private Map<String, String> headers;
    private String body;

}

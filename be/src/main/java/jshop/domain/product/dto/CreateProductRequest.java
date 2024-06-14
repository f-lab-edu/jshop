package jshop.domain.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class CreateProductRequest {

    @NotBlank(message = "이름은 공백일 수 없습니다.")
    private String name;

    @NotNull(message = "카테고리는 공백일 수 없습니다.")
    private Long categoryId;

    @NotBlank(message = "제조사는 공백일 수 없습니다.")
    private String manufacturer;

    @NotBlank(message = "설명은 공백일 수 없습니다.")
    private String description;

    private Map<String, List<String>> attributes;

}

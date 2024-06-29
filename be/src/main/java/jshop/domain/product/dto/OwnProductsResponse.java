package jshop.domain.product.dto;

import java.util.List;
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
public class OwnProductsResponse {

    private List<ProductResponse> products;
    private int page;
    private int totalPage;
    private long totalCount;
}

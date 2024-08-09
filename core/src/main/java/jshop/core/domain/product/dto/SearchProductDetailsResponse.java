package jshop.core.domain.product.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchProductDetailsResponse {

    private Long totalCount;
    private Integer currentPage;
    private Integer totalPages;
    private List<ProductDetailResponse> products;
}

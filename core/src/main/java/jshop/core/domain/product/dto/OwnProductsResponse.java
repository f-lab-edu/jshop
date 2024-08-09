package jshop.core.domain.product.dto;

import java.util.List;
import jshop.core.domain.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;

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

    public static OwnProductsResponse create(Page<Product> page) {
        return OwnProductsResponse
            .builder()
            .page(page.getNumber())
            .totalPage(page.getTotalPages())
            .products(page.map(ProductResponse::of).toList())
            .totalCount(page.getTotalElements())
            .build();
    }
}

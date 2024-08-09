package jshop.core.domain.cart.dto;

import java.util.List;
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
public class OwnCartInfoResponse {

    private List<CartProductResponse> products;
    private int page;
    private int totalPage;
    private long totalCount;

    public static OwnCartInfoResponse create(Page<CartProductQueryResult> page) {
        return OwnCartInfoResponse
            .builder()
            .page(page.getNumber())
            .totalPage(page.getTotalPages())
            .totalCount(page.getTotalElements())
            .products(page.getContent().stream().map(CartProductResponse::of).toList())
            .build();
    }
}

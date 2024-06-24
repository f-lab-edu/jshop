package jshop.domain.product.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Map;
import jshop.domain.inventory.entity.Inventory;
import jshop.domain.product.dto.CreateProductDetailRequest;
import jshop.global.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_detail")
public class ProductDetail extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "product_detail_id")
    private Long id;

    /**
     * ProductDetail 은 하나의 상품을 참조함.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    /**
     * 상품 속성은 json으로 관리된다
     * attribute에는 ProductDetail의 속성정보가 있다.
     * ex)
     * color: red, storage: 128gb
     */
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> attribute;

    /**
     * 인벤토리와 일대일 관계에 있다.
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    private Long price;

    public static ProductDetail of(CreateProductDetailRequest createProductDetailRequest,
        Product product, Inventory inventory) {
        return ProductDetail
            .builder()
            .product(product)
            .price(createProductDetailRequest.getPrice())
            .attribute(createProductDetailRequest.getAttribute())
            .inventory(inventory)
            .build();
    }
}

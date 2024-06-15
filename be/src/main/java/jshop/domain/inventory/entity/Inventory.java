package jshop.domain.inventory.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jshop.domain.product.entity.ProductDetail;
import jshop.global.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inventory")
public class Inventory extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "inventory_id")
    private Long id;

    /**
     * 상품에 일대일로 대응된다.
     * 상품측이 인벤토리 id를 가지고 있음 상품이 삭제되면 인벤토리도 삭제된다.
     * cascade
     */
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "inventory")
    private ProductDetail productDetail;

    private Integer quantity;
    private Integer min_quantity;
}

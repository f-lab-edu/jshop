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
import java.util.Optional;
import jshop.domain.inventory.entity.Inventory;
import jshop.domain.product.dto.CreateProductDetailRequest;
import jshop.domain.product.dto.UpdateProductDetailRequest;
import jshop.global.common.ErrorCode;
import jshop.global.entity.BaseEntity;
import jshop.global.exception.JshopException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Slf4j
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

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    public Inventory getInventory() {
        return Optional.ofNullable(inventory).orElseThrow(() -> {
            log.error(ErrorCode.INVALID_PRODUCTDETAIL_INVENTORY.getLogMessage(), id);
            throw JshopException.of(ErrorCode.INVALID_PRODUCTDETAIL_INVENTORY);
        });
    }

    public static ProductDetail of(CreateProductDetailRequest createProductDetailRequest, Product product,
        Inventory inventory) {
        if (!product.verifyChildAttribute(createProductDetailRequest.getAttribute())) {
            log.error(ErrorCode.INVALID_PRODUCT_ATTRIBUTE.getLogMessage(), product.getAttributes(),
                createProductDetailRequest.getAttribute());
            throw JshopException.of(ErrorCode.INVALID_PRODUCT_ATTRIBUTE);
        }
        return ProductDetail
            .builder()
            .product(product)
            .price(createProductDetailRequest.getPrice())
            .attribute(createProductDetailRequest.getAttribute())
            .inventory(inventory)
            .build();
    }

    public void update(UpdateProductDetailRequest updateProductDetailRequest) {
        if (updateProductDetailRequest.getPrice() <= 0) {
            log.error(ErrorCode.ILLEGAL_PRICE_EXCEPTION.getLogMessage(), updateProductDetailRequest.getPrice());
            throw JshopException.of(ErrorCode.ILLEGAL_PRICE_EXCEPTION);
        }
        price = updateProductDetailRequest.getPrice();
    }

    public void delete() {
        isDeleted = true;
    }
}

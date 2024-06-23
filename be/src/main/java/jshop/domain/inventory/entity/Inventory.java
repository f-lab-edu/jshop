package jshop.domain.inventory.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jshop.global.common.ErrorCode;
import jshop.global.entity.BaseEntity;
import jshop.global.exception.JshopException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

    private Integer quantity;
    private Integer minQuantity;

    public void increaseStock(int quantity) {
        if (quantity < 0) {
            log.error(ErrorCode.ILLEGAL_QUANTITY_REQUEST_EXCEPTION.getLogMessage(), quantity);
            throw JshopException.of(ErrorCode.ILLEGAL_QUANTITY_REQUEST_EXCEPTION);
        }
        this.quantity += quantity;
    }

    public void decreaseStock(int quantity) {
        if (quantity < 0) {
            log.error(ErrorCode.ILLEGAL_QUANTITY_REQUEST_EXCEPTION.getLogMessage(), quantity);
            throw JshopException.of(ErrorCode.ILLEGAL_QUANTITY_REQUEST_EXCEPTION);
        }

        if (this.quantity - quantity < 0) {
            log.error(ErrorCode.NEGATIVE_QUANTITY_EXCEPTION.getLogMessage(), this.quantity - quantity);
            throw JshopException.of(ErrorCode.NEGATIVE_QUANTITY_EXCEPTION);
        }

        this.quantity -= quantity;

        if (this.quantity < minQuantity) {
            /**
             * TODO
             * 주인에게 어떤 알림이 가도록
             */
            log.warn("최저 수량보다 낮아졌습니다. 최저수량 : [{}], 현재수량 : [{}]", minQuantity, this.quantity);
        }
    }
}

package jshop.domain.inventory.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

@Slf4j
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inventory")
@Audited
public class Inventory extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "inventory_id")
    private Long id;

    private Integer quantity;
    private Integer minQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "inventory_change_type")
    private InventoryChangeType changeType;

    public static Inventory create() {
        return Inventory
            .builder().quantity(0).minQuantity(0).changeType(InventoryChangeType.CREATE).build();
    }

    public void addStock(int quantity) {
        changeType = InventoryChangeType.INCREASE;
        this.quantity += quantity;
    }

    public void refund(int quantity) {
        changeType = InventoryChangeType.REFUND;
        this.quantity += quantity;
    }

    public void removeStock(int quantity) {
        changeType = InventoryChangeType.DECREASE;
        if (this.quantity - quantity < 0) {
            log.error(ErrorCode.NEGATIVE_QUANTITY_EXCEPTION.getLogMessage(), this.quantity - quantity);
            throw JshopException.of(ErrorCode.NEGATIVE_QUANTITY_EXCEPTION);
        }

        this.quantity -= quantity;
    }

    public void purchase(int quantity) {
        if (this.quantity - quantity < 0) {
            log.error(ErrorCode.NEGATIVE_QUANTITY_EXCEPTION.getLogMessage(), this.quantity - quantity);
            throw JshopException.of(ErrorCode.NEGATIVE_QUANTITY_EXCEPTION);
        }

        changeType = InventoryChangeType.PURCHASE;
        this.quantity -= quantity;
    }

    public void changeStock(int quantity) {
        if (this.quantity + quantity < 0) {
            log.error(ErrorCode.NEGATIVE_QUANTITY_EXCEPTION.getLogMessage(), this.quantity - quantity);
            throw JshopException.of(ErrorCode.NEGATIVE_QUANTITY_EXCEPTION);
        }

        this.quantity += quantity;
        if (this.quantity < minQuantity) {
            /**
             * TODO
             * 주인에게 어떤 알림이 가도록
             */
            log.warn("최저 수량보다 낮아졌습니다. 최저수량 : [{}], 현재수량 : [{}]", minQuantity, this.quantity);
        }
    }
}

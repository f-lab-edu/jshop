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
        if (quantity <= 0) {
            log.error(ErrorCode.ILLEGAL_QUANTITY_REQUEST_EXCEPTION.getLogMessage(), quantity);
            throw JshopException.of(ErrorCode.ILLEGAL_QUANTITY_REQUEST_EXCEPTION);
        }
        changeType = InventoryChangeType.INCREASE;
        this.quantity += quantity;
    }

    public void refund(int quantity) {
        if (quantity <= 0) {
            log.error(ErrorCode.ILLEGAL_QUANTITY_REQUEST_EXCEPTION.getLogMessage(), quantity);
            throw JshopException.of(ErrorCode.ILLEGAL_QUANTITY_REQUEST_EXCEPTION);
        }
        changeType = InventoryChangeType.REFUND;
        this.quantity += quantity;
    }

    public void removeStock(int quantity) {
        if (quantity <= 0) {
            log.error(ErrorCode.ILLEGAL_QUANTITY_REQUEST_EXCEPTION.getLogMessage(), quantity);
            throw JshopException.of(ErrorCode.ILLEGAL_QUANTITY_REQUEST_EXCEPTION);
        }
        if (this.quantity - quantity < 0) {
            log.error(ErrorCode.NEGATIVE_QUANTITY_EXCEPTION.getLogMessage(), this.quantity - quantity);
            throw JshopException.of(ErrorCode.NEGATIVE_QUANTITY_EXCEPTION);
        }
        changeType = InventoryChangeType.DECREASE;
        this.quantity -= quantity;
    }

    public void purchase(int quantity) {
        if (quantity <= 0) {
            log.error(ErrorCode.ILLEGAL_QUANTITY_REQUEST_EXCEPTION.getLogMessage(), quantity);
            throw JshopException.of(ErrorCode.ILLEGAL_QUANTITY_REQUEST_EXCEPTION);
        }
        if (this.quantity - quantity < 0) {
            log.error(ErrorCode.NEGATIVE_QUANTITY_EXCEPTION.getLogMessage(), this.quantity - quantity);
            throw JshopException.of(ErrorCode.NEGATIVE_QUANTITY_EXCEPTION);
        }

        changeType = InventoryChangeType.PURCHASE;
        this.quantity -= quantity;
    }
}

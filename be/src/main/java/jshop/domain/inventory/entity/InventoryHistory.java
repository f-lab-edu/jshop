package jshop.domain.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "inventory_history")
public class InventoryHistory extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "inventory_history_id")
    private Long id;

    /**
     * 인벤토리 하나당 여러개의 히스토리를 갖는다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    @Enumerated(EnumType.STRING)
    @Column(name = "inventory_change_type")
    private InventoryChangeType changeType;

    @Column(nullable = false, name = "old_quantity")
    private Integer oldQuantity;
    @Column(nullable = false, name = "new_quantity")
    private Integer newQuantity;
    @Column(nullable = false, name = "change_quantity")
    private Integer changeQuantity;
}

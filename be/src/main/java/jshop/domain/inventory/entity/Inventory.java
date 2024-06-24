package jshop.domain.inventory.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
@Table(name = "inventory")
public class Inventory extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "inventory_id")
    private Long id;

    private Integer quantity;
    private Integer minQuantity;
}

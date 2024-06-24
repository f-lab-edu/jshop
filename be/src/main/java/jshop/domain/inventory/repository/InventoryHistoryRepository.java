package jshop.domain.inventory.repository;

import jshop.domain.inventory.entity.InventoryHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryHistoryRepository extends JpaRepository<InventoryHistory, Long> {

}

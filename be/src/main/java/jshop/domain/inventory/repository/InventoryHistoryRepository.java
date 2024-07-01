package jshop.domain.inventory.repository;

import jshop.domain.inventory.entity.Inventory;
import jshop.domain.inventory.entity.InventoryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

public interface InventoryHistoryRepository extends RevisionRepository<Inventory, Long, Integer> {}

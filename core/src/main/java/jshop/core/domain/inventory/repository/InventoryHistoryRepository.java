package jshop.core.domain.inventory.repository;

import jshop.core.domain.inventory.entity.Inventory;
import org.springframework.data.repository.history.RevisionRepository;

public interface InventoryHistoryRepository extends RevisionRepository<Inventory, Long, Integer> {}

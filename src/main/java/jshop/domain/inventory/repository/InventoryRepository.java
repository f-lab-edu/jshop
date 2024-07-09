package jshop.domain.inventory.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import jshop.domain.inventory.entity.Inventory;
import org.assertj.core.util.VisibleForTesting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Inventory> findById(Long id);
}

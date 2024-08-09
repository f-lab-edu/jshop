package jshop.core.domain.inventory.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import jshop.core.domain.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Inventory i where i.id = :id")
    Optional<Inventory> findByIdWithPessimisticLock(@Param("id") Long id);

    Optional<Inventory> findById(Long id);
}

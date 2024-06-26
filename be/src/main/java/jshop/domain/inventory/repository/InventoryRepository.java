package jshop.domain.inventory.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import javax.swing.text.html.Option;
import jshop.domain.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Inventory> findById(Long id);
}

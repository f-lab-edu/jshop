package jshop.domain.delivery.repository;

import jshop.domain.delivery.entity.DeliveryHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryHistoryRepository extends JpaRepository<DeliveryHistory, Long> {

    
}

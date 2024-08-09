package jshop.core.domain.delivery.repository;

import jshop.core.domain.delivery.entity.Delivery;
import org.springframework.data.repository.history.RevisionRepository;

public interface DeliveryHistoryRepository extends RevisionRepository<Delivery, Long, Integer> {


}

package jshop.domain.delivery.repository;

import jshop.domain.delivery.entity.Delivery;
import org.springframework.data.repository.history.RevisionRepository;

public interface DeliveryHistoryRepository extends RevisionRepository<Delivery, Long, Integer> {


}

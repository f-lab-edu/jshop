package jshop.domain.delivery.service;

import jshop.domain.address.entity.Address;
import jshop.domain.delivery.entity.Delivery;
import jshop.domain.delivery.entity.DeliveryHistory;
import jshop.domain.delivery.entity.DeliveryState;
import jshop.domain.delivery.repository.DeliveryHistoryRepository;
import jshop.domain.delivery.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryHistoryRepository deliveryHistoryRepository;

    public Delivery createDelivery(Address address) {
        Delivery delivery = Delivery.of(address);
        deliveryRepository.save(delivery);

        DeliveryHistory history = DeliveryHistory
            .builder().delivery(delivery).deliveryState(DeliveryState.PREPARING).build();
        deliveryHistoryRepository.save(history);

        return delivery;
    }
}

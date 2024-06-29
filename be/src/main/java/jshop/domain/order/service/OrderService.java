package jshop.domain.order.service;

import jshop.domain.inventory.service.InventoryService;
import jshop.domain.order.dto.CreateOrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final InventoryService inventoryService;

    @Transactional
    public void createOrder(CreateOrderRequest createOrderRequest, Long userId) {
        // 주문 재고 수량 체크
        // 회원 잔고 체크
        // 배송 생성
        // 주문 생성
    }
}

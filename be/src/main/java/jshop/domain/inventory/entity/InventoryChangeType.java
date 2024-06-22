package jshop.domain.inventory.entity;

public enum InventoryChangeType {
    CREATE,      // 생성 (새제품 생성)
    SALE,        // 판매 (감소)
    REFUND,      // 환불 (증가)
    INCOMING,    // 입고 (증가)
    OUTGOING,    // 출고 (감소)
    DELETE       // 삭제
}

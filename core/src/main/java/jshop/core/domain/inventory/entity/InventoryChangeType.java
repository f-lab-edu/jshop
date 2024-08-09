package jshop.core.domain.inventory.entity;

public enum InventoryChangeType {
    CREATE,      // 생성 (새제품 생성)
    INCREASE,        // 증가
    DECREASE,     // 감소
    PURCHASE,     // 구매
    REFUND,       // 환불
    DELETE       // 삭제
}

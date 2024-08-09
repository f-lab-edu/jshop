package jshop.core.inventory.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jshop.core.domain.inventory.entity.Inventory;
import jshop.common.exception.ErrorCode;
import jshop.common.exception.JshopException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("[단위 테스트] Inventory")
class InventoryTest {

    @Nested
    @DisplayName("재고 변경 검증")
    class AddStock {

        @Test
        @DisplayName("추가 수량이 0이 아니라면 재고를 추가할 수 있음 ")
        public void addStock_success() {
            // given
            int initQuantity = 10;
            int quantity = 5;
            Inventory inventory = Inventory
                .builder().quantity(initQuantity).minQuantity(0).build();

            // when
            inventory.addStock(quantity);
            // then
            assertThat(inventory.getQuantity()).isEqualTo(initQuantity + quantity);
        }


        @Test
        @DisplayName("추가 수량이 0보다 작다면 ILLEGAL_QUANTITY_REQUEST_EXCEPTION 발생")
        public void addtock_fail() {
            // given
            int initQuantity = 10;
            int quantity = -1;
            Inventory inventory = Inventory
                .builder().quantity(initQuantity).minQuantity(0).build();

            // when
            JshopException jshopException = assertThrows(JshopException.class, () -> inventory.addStock(quantity));
            // then
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.ILLEGAL_QUANTITY_REQUEST_EXCEPTION);
        }

        @Test
        @DisplayName("변경 수량이 0이 아니라면 재고를 감소할 수 있음")
        public void removeStock_success() {
            // given
            int initQuantity = 10;
            int quantity = 5;
            Inventory inventory = Inventory
                .builder().quantity(initQuantity).minQuantity(0).build();

            // when
            inventory.removeStock(quantity);
            // then
            assertThat(inventory.getQuantity()).isEqualTo(initQuantity - quantity);
        }

        @Test
        @DisplayName("감소 수량이 0보다 작다면 ILLEGAL_QUANTITY_REQUEST_EXCEPTION 발생")
        public void removeStock_fail() {
            // given
            int initQuantity = 10;
            int quantity = -1;
            Inventory inventory = Inventory
                .builder().quantity(initQuantity).minQuantity(0).build();

            // when
            JshopException jshopException = assertThrows(JshopException.class, () -> inventory.removeStock(quantity));
            // then
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.ILLEGAL_QUANTITY_REQUEST_EXCEPTION);
        }

        @Test
        @DisplayName("감소 이후 수량이 0보다 작다면 NEGATIVE_QUANTITY_EXCEPTION 예외 발생")
        public void removeStock_negative_quantity() {
            // given
            Inventory inventory = Inventory
                .builder().quantity(0).minQuantity(0).build();

            // then
            JshopException jshopException = assertThrows(JshopException.class, () -> inventory.removeStock(1));
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.NEGATIVE_QUANTITY_EXCEPTION);
        }
    }
}
package jshop.domain.inventory.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("[단위 테스트] Inventory")
class InventoryTest {

    @Nested
    @DisplayName("재고 변경 검증")
    class AddStock {

        @Test
        @DisplayName("변경 수량이 0이 아니라면 재고를 변경할 수 있음 (추가)")
        public void addStock_success() {
            // given
            Inventory inventory = Inventory
                .builder().quantity(0).minQuantity(0).build();

            // when
            inventory.addStock(5);
            // then
            assertThat(inventory.getQuantity()).isEqualTo(5);
        }

        @Test
        @DisplayName("변경 수량이 0이 아니라면 재고를 변경할 수 있음 (감소)")
        public void removeStock_success() {
            // given
            Inventory inventory = Inventory
                .builder().quantity(10).minQuantity(0).build();

            // when
            inventory.removeStock(5);
            // then
            assertThat(inventory.getQuantity()).isEqualTo(5);
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

        @Test
        @DisplayName("감소 이후 수량이 minQuantity 보다 작다면 경고 발생")
        public void removeStock_smaller_than_minQuantity() {
            // given
            Inventory inventory = Inventory
                .builder().quantity(10).minQuantity(8).build();

            // when
            inventory.removeStock(5);

            // then
            assertThat(inventory.getQuantity()).isEqualTo(5);
        }
    }
}
package jshop.domain.inventory.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Inventory entity 테스트")
class InventoryTest {

    @Nested
    @DisplayName("재고 추가 검증")
    class AddStock {

        @Test
        @DisplayName("추가 수량이 음수가 아니라면 재고를 더할 수 있음")
        public void addStock_success() {
            // given
            Inventory inventory = Inventory
                .builder().quantity(0).minQuantity(0).build();

            // when
            inventory.increaseStock(5);
            // then
            assertThat(inventory.getQuantity()).isEqualTo(5);
        }

        @Test
        @DisplayName("추가 수량이 음수라면 ILLEGAL_QUANTITY_REQUEST_EXCEPTION이 발생함")
        public void addStock_illegal_quantity() {
            // given
            Inventory inventory = Inventory
                .builder().quantity(0).minQuantity(0).build();

            // then
            JshopException jshopException = assertThrows(JshopException.class, () -> inventory.increaseStock(-5));
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.ILLEGAL_QUANTITY_REQUEST_EXCEPTION);
        }
    }

    @Nested
    @DisplayName("재고 감소 검증")
    class RemoveStock {

        @Test
        @DisplayName("감소 수량이 음수가 아니라면 재고를 뺄 수 있음")
        public void removeStock_success() {
            // given
            Inventory inventory = Inventory
                .builder().quantity(10).minQuantity(0).build();

            // when
            inventory.decreaseStock(-5);
            // then
            assertThat(inventory.getQuantity()).isEqualTo(5);
        }

        @Test
        @DisplayName("감소 수량이 양수라면 ILLEGAL_QUANTITY_REQUEST_EXCEPTION이 발생함")
        public void removeStock_illegal_quantity() {
            // given
            Inventory inventory = Inventory
                .builder().quantity(10).minQuantity(0).build();

            // then
            JshopException jshopException = assertThrows(JshopException.class, () -> inventory.decreaseStock(5));
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.ILLEGAL_QUANTITY_REQUEST_EXCEPTION);
        }

        @Test
        @DisplayName("감소 이후 수량이 0보다 작다면 NEGATIVE_QUANTITY_EXCEPTION 예외 발생")
        public void removeStock_negative_quantity() {
            // given
            Inventory inventory = Inventory
                .builder().quantity(0).minQuantity(0).build();

            // then
            JshopException jshopException = assertThrows(JshopException.class, () -> inventory.decreaseStock(-1));
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.NEGATIVE_QUANTITY_EXCEPTION);
        }

        @Test
        @DisplayName("감소 이후 수량이 minQuantity 보다 작다면 경고 발생")
        public void removeStock_smaller_than_minQuantity() {
            // given
            Inventory inventory = Inventory
                .builder().quantity(10).minQuantity(8).build();

            // when
            inventory.decreaseStock(-5);

            // then
            assertThat(inventory.getQuantity()).isEqualTo(5);
        }
    }

}
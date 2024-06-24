package jshop.domain.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import jshop.domain.inventory.entity.Inventory;
import jshop.domain.inventory.entity.InventoryChangeType;
import jshop.domain.inventory.entity.InventoryHistory;
import jshop.domain.inventory.repository.InventoryHistoryRepository;
import jshop.domain.inventory.repository.InventoryRepository;
import jshop.domain.product.entity.Product;
import jshop.domain.product.entity.ProductDetail;
import jshop.domain.product.repository.ProductDetailRepository;
import jshop.domain.user.entity.User;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryService Service 테스트")
class InventoryServiceTest {

    @InjectMocks
    private InventoryService inventoryService;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryHistoryRepository inventoryHistoryRepository;

    @Mock
    private ProductDetailRepository productDetailRepository;

    @Captor
    private ArgumentCaptor<Inventory> inventoryArgumentCaptor;

    @Captor
    private ArgumentCaptor<InventoryHistory> inventoryHistoryArgumentCaptor;

    @Nested
    @DisplayName("인벤토리 생성 테스트")
    class CreateInventory {

        @Test
        @DisplayName("새 인벤토리 생성은 quantity, minQuantity가 0으로 초기화 된다.")
        public void createInventory() {
            // when
            inventoryService.createInventory();

            // then
            verify(inventoryHistoryRepository, times(1)).save(inventoryHistoryArgumentCaptor.capture());
            verify(inventoryRepository, times(1)).save(inventoryArgumentCaptor.capture());

            Inventory savedInventory = inventoryArgumentCaptor.getValue();
            InventoryHistory savedInventoryHistory = inventoryHistoryArgumentCaptor.getValue();

            assertAll("Inventory 검증", () -> assertThat(savedInventory.getQuantity()).isEqualTo(0),
                () -> assertThat(savedInventory.getMinQuantity()).isEqualTo(0));

            assertAll("InventoryHistory 검증", () -> assertThat(savedInventoryHistory.getChangeQuantity()).isEqualTo(0),
                () -> assertThat(savedInventoryHistory.getOldQuantity()).isEqualTo(0),
                () -> assertThat(savedInventoryHistory.getNewQuantity()).isEqualTo(0),
                () -> assertThat(savedInventoryHistory.getChangeType()).isEqualTo(InventoryChangeType.CREATE));

        }
    }

    @Nested
    @DisplayName("재고 추가 테스트")
    class AddStock {

        private User user;
        private Product product;
        private ProductDetail productDetail;
        private Inventory inventory;

        @BeforeEach
        public void init() {
            user = User
                .builder().id(1L).username("kim").build();

            inventory = Inventory
                .builder().quantity(0).minQuantity(0).id(1L).build();

            product = Product
                .builder().id(1L).name("product").owner(user).build();

            productDetail = ProductDetail
                .builder().id(1L).product(product).inventory(inventory).build();
        }

        @Test
        @DisplayName("추가 재고가 양수면 재고를 추가할 수 있다")
        public void addStock_success() {
            // when
            when(productDetailRepository.findById(1L)).thenReturn(Optional.of(productDetail));
            inventoryService.increaseStock(1L, 1L, 10);

            // then
            assertThat(inventory.getQuantity()).isEqualTo(10);
            verify(inventoryHistoryRepository, times(1)).save(inventoryHistoryArgumentCaptor.capture());
            InventoryHistory history = inventoryHistoryArgumentCaptor.getValue();

            assertAll("재고 로그 검증", () -> assertThat(history.getOldQuantity()).isEqualTo(0),
                () -> assertThat(history.getNewQuantity()).isEqualTo(10),
                () -> assertThat(history.getChangeQuantity()).isEqualTo(10),
                () -> assertThat(history.getChangeType()).isEqualTo(InventoryChangeType.INCREASE));
        }

        @Test
        @DisplayName("추가 재고가 음수면 ILLEGAL_QUANTITY_REQUEST_EXCEPTION이 발생")
        public void addStock_illegal_quantity() {
            // when
            when(productDetailRepository.findById(1L)).thenReturn(Optional.of(productDetail));

            // then
            JshopException jshopException = assertThrows(JshopException.class,
                () -> inventoryService.increaseStock(1L, 1L, -10));
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.ILLEGAL_QUANTITY_REQUEST_EXCEPTION);
            assertThat(inventory.getQuantity()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("재고 감소 테스트")
    class decreaseStock {

        private User user;
        private Product product;
        private ProductDetail productDetail;
        private Inventory inventory;

        @BeforeEach
        public void init() {
            user = User
                .builder().id(1L).username("kim").build();

            inventory = Inventory
                .builder().quantity(10).minQuantity(8).id(1L).build();

            product = Product
                .builder().id(1L).name("product").owner(user).build();

            productDetail = ProductDetail
                .builder().id(1L).product(product).inventory(inventory).build();
        }

        @Test
        @DisplayName("감소 재고가 음수고 결과가 0보다 크다면 재고를 감소할 수 있다")
        public void decreaseStock_success() {
            // when
            when(productDetailRepository.findById(1L)).thenReturn(Optional.of(productDetail));
            inventoryService.decreaseStock(1L, 1L, -1);

            // then
            assertThat(inventory.getQuantity()).isEqualTo(9);
            verify(inventoryHistoryRepository, times(1)).save(inventoryHistoryArgumentCaptor.capture());
            InventoryHistory history = inventoryHistoryArgumentCaptor.getValue();

            assertAll("재고 로그 검증", () -> assertThat(history.getOldQuantity()).isEqualTo(10),
                () -> assertThat(history.getNewQuantity()).isEqualTo(9),
                () -> assertThat(history.getChangeQuantity()).isEqualTo(-1),
                () -> assertThat(history.getChangeType()).isEqualTo(InventoryChangeType.DECREASE));
        }

        @Test
        @DisplayName("감소 재고가 양수면 ILLEGAL_QUANTITY_REQUEST_EXCEPTION이 발생")
        public void decreaseStock_illegal_quantity() {
            // when
            when(productDetailRepository.findById(1L)).thenReturn(Optional.of(productDetail));

            // then
            JshopException jshopException = assertThrows(JshopException.class,
                () -> inventoryService.decreaseStock(1L, 1L, 10));
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.ILLEGAL_QUANTITY_REQUEST_EXCEPTION);
            assertThat(inventory.getQuantity()).isEqualTo(10);
        }

        @Test
        @DisplayName("재고 감소 결과가 음수면 NEGATIVE_QUANTITY_EXCEPTION 발생")
        public void decreaseStock_negative_quantity() {
            // when
            when(productDetailRepository.findById(1L)).thenReturn(Optional.of(productDetail));

            // then
            JshopException jshopException = assertThrows(JshopException.class,
                () -> inventoryService.decreaseStock(1L, 1L, -11));
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.NEGATIVE_QUANTITY_EXCEPTION);
            assertThat(inventory.getQuantity()).isEqualTo(10);
        }
    }
}
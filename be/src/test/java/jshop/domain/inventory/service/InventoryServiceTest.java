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

@ExtendWith(MockitoExtension.class)
@DisplayName("[단위 테스트] InventoryService")
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
    @DisplayName("재고 변경 테스트")
    class ChangeStock {

        private User user;
        private Product product;
        private ProductDetail productDetail;
        private Inventory inventory;

        private Long productId = 1L;
        private Long productDetailId = 1L;
        private Long inventoryId = 1L;

        private int initQuantity = 10;
        private int initMinQuantity = 5;
        private String productName = "product";

        @BeforeEach
        public void init() {
            inventory = Inventory
                .builder().quantity(initQuantity).minQuantity(initMinQuantity).id(inventoryId).build();

            product = Product
                .builder().id(productId).name(productName).build();

            productDetail = ProductDetail
                .builder().id(productDetailId).product(product).inventory(inventory).build();
        }

        @Test
        @DisplayName("변경 재고가 양수면 재고를 추가할 수 있다")
        public void addStock_success() {
            // given
            int changeQuantity = 5;

            // when
            when(productDetailRepository.findById(productDetailId)).thenReturn(Optional.of(productDetail));
            inventoryService.changeStock(productDetailId, changeQuantity);

            // then
            assertThat(inventory.getQuantity()).isEqualTo(initQuantity + changeQuantity);
            verify(inventoryHistoryRepository, times(1)).save(inventoryHistoryArgumentCaptor.capture());
            InventoryHistory history = inventoryHistoryArgumentCaptor.getValue();

            assertAll("재고 로그 검증", () -> assertThat(history.getOldQuantity()).isEqualTo(initQuantity),
                () -> assertThat(history.getNewQuantity()).isEqualTo(initQuantity + changeQuantity),
                () -> assertThat(history.getChangeQuantity()).isEqualTo(changeQuantity),
                () -> assertThat(history.getChangeType()).isEqualTo(InventoryChangeType.INCREASE));
        }

        @Test
        @DisplayName("감소 재고가 음수고 결과가 0보다 크다면 재고를 감소할 수 있다")
        public void removeStock_success() {
            // given
            int changeQuantity = -1;

            // when
            when(productDetailRepository.findById(productDetailId)).thenReturn(Optional.of(productDetail));
            inventoryService.changeStock(productDetailId, changeQuantity);

            // then
            assertThat(inventory.getQuantity()).isEqualTo(initQuantity + changeQuantity);
            verify(inventoryHistoryRepository, times(1)).save(inventoryHistoryArgumentCaptor.capture());
            InventoryHistory history = inventoryHistoryArgumentCaptor.getValue();

            assertAll("재고 로그 검증", () -> assertThat(history.getOldQuantity()).isEqualTo(initQuantity),
                () -> assertThat(history.getNewQuantity()).isEqualTo(initQuantity + changeQuantity),
                () -> assertThat(history.getChangeQuantity()).isEqualTo(changeQuantity),
                () -> assertThat(history.getChangeType()).isEqualTo(InventoryChangeType.DECREASE));
        }

        @Test
        @DisplayName("재고 감소 결과가 음수면 NEGATIVE_QUANTITY_EXCEPTION 발생")
        public void changeStock_negative_quantity() {
            // given
            int changeQuantity = -11;
            // when
            when(productDetailRepository.findById(productDetailId)).thenReturn(Optional.of(productDetail));

            // then
            JshopException jshopException = assertThrows(JshopException.class,
                () -> inventoryService.changeStock(productDetailId, changeQuantity));
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.NEGATIVE_QUANTITY_EXCEPTION);
            assertThat(inventory.getQuantity()).isEqualTo(initQuantity);
        }

        @Test
        @DisplayName("상세 상품에 해당하는 인벤토리가 없을경우 INVALID_PRODUCTDETAIL_INVENTORY 예외 발생")
        public void changeStock_noInventory() {
            // given
            ProductDetail noInventoryProductDetail = ProductDetail
                .builder().id(2L).product(product).build();
            int changeQuantity = 1;

            // when
            when(productDetailRepository.findById(2L)).thenReturn(Optional.of(noInventoryProductDetail));

            // then
            JshopException jshopException = assertThrows(JshopException.class,
                () -> inventoryService.changeStock(2L, changeQuantity));

            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.INVALID_PRODUCTDETAIL_INVENTORY);
        }
    }
}
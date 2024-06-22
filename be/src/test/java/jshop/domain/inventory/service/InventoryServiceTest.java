package jshop.domain.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import jshop.domain.inventory.entity.Inventory;
import jshop.domain.inventory.entity.InventoryChangeType;
import jshop.domain.inventory.entity.InventoryHistory;
import jshop.domain.inventory.repository.InventoryHistoryRepository;
import jshop.domain.inventory.repository.InventoryRepository;
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
@DisplayName("InventoryService Service 테스트")
class InventoryServiceTest {

    @InjectMocks
    private InventoryService inventoryService;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryHistoryRepository inventoryHistoryRepository;

    @Captor
    private ArgumentCaptor<Inventory> inventoryArgumentCaptor;

    @Captor
    private ArgumentCaptor<InventoryHistory> inventoryHistoryArgumentCaptor;

    @Nested
    class CreateInventory {

        @Test
        @DisplayName("새 인벤토리 생성")
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
}
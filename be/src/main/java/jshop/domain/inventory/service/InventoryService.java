package jshop.domain.inventory.service;

import jshop.domain.inventory.entity.Inventory;
import jshop.domain.inventory.entity.InventoryChangeType;
import jshop.domain.inventory.entity.InventoryHistory;
import jshop.domain.inventory.repository.InventoryHistoryRepository;
import jshop.domain.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryHistoryRepository inventoryHistoryRepository;

    @Transactional
    public Inventory createInventory() {
        Inventory inventory = Inventory
            .builder().quantity(0).minQuantity(0).build();

        InventoryHistory inventoryHistory = InventoryHistory
            .builder()
            .inventory(inventory)
            .changeQuantity(0)
            .oldQuantity(0)
            .newQuantity(0)
            .changeType(InventoryChangeType.CREATE)
            .build();

        inventoryHistoryRepository.save(inventoryHistory);

        return inventoryRepository.save(inventory);
    }
}

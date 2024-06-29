package jshop.domain.inventory.service;

import java.util.Optional;
import jshop.domain.inventory.entity.Inventory;
import jshop.domain.inventory.entity.InventoryChangeType;
import jshop.domain.inventory.entity.InventoryHistory;
import jshop.domain.inventory.repository.InventoryHistoryRepository;
import jshop.domain.inventory.repository.InventoryRepository;
import jshop.domain.product.entity.ProductDetail;
import jshop.domain.product.repository.ProductDetailRepository;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryHistoryRepository inventoryHistoryRepository;
    private final ProductDetailRepository productDetailRepository;

    @Transactional
    public Inventory createInventory() {
        Inventory inventory = Inventory
            .builder().quantity(0).minQuantity(0).build();

        inventoryRepository.save(inventory);

        InventoryHistory inventoryHistory = InventoryHistory
            .builder()
            .inventory(inventory)
            .changeQuantity(0)
            .oldQuantity(0)
            .newQuantity(0)
            .changeType(InventoryChangeType.CREATE)
            .build();

        inventoryHistoryRepository.save(inventoryHistory);

        return inventory;
    }

    @Transactional
    public void changeStock(Long productDetailId, int quantity) {
        Inventory inventory = getInventory(productDetailId);
        int oldQuantity = inventory.getQuantity();

        inventory.changeStock(quantity);
        int newQuantity = inventory.getQuantity();

        InventoryHistory inventoryHistory = InventoryHistory
            .builder()
            .inventory(inventory)
            .changeQuantity(quantity)
            .oldQuantity(oldQuantity)
            .newQuantity(newQuantity)
            .changeType(quantity > 0 ? InventoryChangeType.INCREASE : InventoryChangeType.DECREASE)
            .build();

        inventoryHistoryRepository.save(inventoryHistory);
    }

    public Inventory getInventory(Long productDetailId) {
        Inventory inventory = productDetailRepository
            .findInventoryByProductDetailId(productDetailId)
            .orElseThrow(() -> {
                log.error(ErrorCode.INVALID_PRODUCTDETAIL_INVENTORY.getLogMessage(), productDetailId);
                throw JshopException.of(ErrorCode.INVALID_PRODUCTDETAIL_INVENTORY);
            });

        return inventory;
    }
}

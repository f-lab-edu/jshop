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

    @Transactional
    public void increaseStock(Long productDetailId, Long userId, int quantity) {
        Inventory inventory = getInventory(productDetailId, userId);
        int oldQuantity = inventory.getQuantity();
        inventory.increaseStock(quantity);
        int newQuantity = inventory.getQuantity();

        InventoryHistory inventoryHistory = InventoryHistory
            .builder()
            .inventory(inventory)
            .changeQuantity(quantity)
            .oldQuantity(oldQuantity)
            .newQuantity(newQuantity)
            .changeType(InventoryChangeType.INCREASE)
            .build();

        inventoryHistoryRepository.save(inventoryHistory);
    }

    @Transactional
    public void decreaseStock(Long productDetailId, Long userId, int quantity) {
        Inventory inventory = getInventory(productDetailId, userId);
        int oldQuantity = inventory.getQuantity();
        inventory.decreaseStock(quantity);
        int newQuantity = inventory.getQuantity();

        InventoryHistory inventoryHistory = InventoryHistory
            .builder()
            .inventory(inventory)
            .changeQuantity(-quantity)
            .oldQuantity(oldQuantity)
            .newQuantity(newQuantity)
            .changeType(InventoryChangeType.DECREASE)
            .build();

        inventoryHistoryRepository.save(inventoryHistory);

    }

    public Inventory getInventory(Long productDetailId, Long userId) {
        Optional<ProductDetail> optionalProductDetail = productDetailRepository.findById(productDetailId);
        ProductDetail productDetail = optionalProductDetail.orElseThrow(() -> {
            log.error(ErrorCode.PRODUCTDETAIL_ID_NOT_FOUND.getLogMessage(), productDetailId);
            throw JshopException.of(ErrorCode.PRODUCTDETAIL_ID_NOT_FOUND);
        });

        if (productDetail.getProduct().getOwner().getId() != userId) {
            log.error(ErrorCode.UNAUTHORIZED.getLogMessage(), "ProductDetail", productDetailId, userId);
            throw JshopException.of(ErrorCode.UNAUTHORIZED);
        }

        Inventory inventory = productDetail.getInventory();
        if (inventory == null) {
            log.error(ErrorCode.PRODUCTDETAIL_NO_INVENTORY.getLogMessage(), productDetailId);
            throw JshopException.of(ErrorCode.PRODUCTDETAIL_NO_INVENTORY);
        }

        return inventory;
    }
}

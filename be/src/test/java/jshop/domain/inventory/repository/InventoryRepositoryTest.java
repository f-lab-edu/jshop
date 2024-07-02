package jshop.domain.inventory.repository;


import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import jshop.domain.inventory.entity.Inventory;
import jshop.global.config.P6SpyConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;


@DataJpaTest
@Import(P6SpyConfig.class)
@DisplayName("[단위 테스트] InventoryRepository")
@Transactional
class InventoryRepositoryTest {


    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;
    private static Inventory inventory;

    @BeforeEach
    public void init() {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            inventory = Inventory
                .builder().quantity(0).build();
            em.persist(inventory);
            transaction.commit();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            transaction.rollback();
        }
    }

    @Test
    @DisplayName("Inventory 재고 변경 동기화 테스트 (비관적 락)")
    @Disabled
    public void changeStock_pessimistic() throws Exception {
        // given
//        ExecutorService executor = Executors.newFixedThreadPool(5);
//        for (int i = 0; i < 5; i++) {
//            try {
//
//                Inventory foundInventory = em.find(Inventory.class, inventory.getId(),
//                    LockModeType.PESSIMISTIC_WRITE);
//                foundInventory.changeStock(1);
//                em.persist(foundInventory);
//                transaction.commit();
//            } catch (Exception e) {
//                System.err.println(e.getMessage());
//                transaction.rollback();
//            }
//        }
//
//        executor.shutdown();
//        executor.awaitTermination(1L, TimeUnit.MINUTES);
//
//        Inventory findInventory = inventoryRepository.findById(inventory.getId()).get();
//        assertThat(findInventory.getQuantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("Inventory 재고 변경 동기화 테스트 (문제 발생)")
    public void changeStock_nolock() throws Exception {
        // given
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            executor.submit(() -> {
                EntityManager em = entityManagerFactory.createEntityManager();
                EntityTransaction transaction = em.getTransaction();

                try {
                    transaction.begin();
                    Inventory foundInventory = em.find(Inventory.class, inventory.getId());
                    foundInventory.addStock(1);
                    em.persist(foundInventory);
                    transaction.commit();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    transaction.rollback();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1L, TimeUnit.MINUTES);

        Inventory findInventory = inventoryRepository.findById(inventory.getId()).get();
        assertThat(findInventory.getQuantity()).isNotEqualTo(5);
    }
}
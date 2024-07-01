package jshop.domain.wallet.repository;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import jshop.domain.wallet.entity.Wallet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Rollback(false)
class WalletRepositoryTest {

    private static Long id;
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletHistoryRepository walletHistoryRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("Audited 테스트")
    @Order(0)
    public void test() {
        Wallet wallet = Wallet.create();

        walletRepository.save(wallet);
        id = wallet.getId();
    }

    @Test
    @DisplayName("Audited 테스트")
    @Order(1)
    public void test2() {
        Wallet wallet = walletRepository.findById(id).get();
        wallet.deposit(100L);
    }

    @Test
    @DisplayName("Audited 테스트")
    @Order(2)
    public void test3() {
        Wallet wallet = walletRepository.findById(id).get();
        wallet.withdraw(100L);
    }

    @Test
    @Order(4)
    public void test4() {
        Revisions<Integer, Wallet> revisions = walletHistoryRepository.findRevisions(id);
        List<Revision<Integer, Wallet>> contents = revisions.getContent();

        for (Revision<Integer, Wallet> content : contents) {
            System.out.println(content);
        }
    }
}
package jshop.domain.wallet.repository;

import jshop.domain.wallet.entity.Wallet;
import jshop.domain.wallet.entity.WalletHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

public interface WalletHistoryRepository extends RevisionRepository<Wallet, Long, Integer> {}

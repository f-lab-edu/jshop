package jshop.core.domain.wallet.repository;

import jshop.core.domain.wallet.entity.Wallet;
import org.springframework.data.repository.history.RevisionRepository;

public interface WalletHistoryRepository extends RevisionRepository<Wallet, Long, Integer> {}

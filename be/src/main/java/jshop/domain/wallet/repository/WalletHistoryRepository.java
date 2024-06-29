package jshop.domain.wallet.repository;

import jshop.domain.wallet.entity.WalletHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletHistoryRepository extends JpaRepository<WalletHistory, Long> {}

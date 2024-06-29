package jshop.domain.wallet.service;

import java.util.Optional;
import jshop.domain.wallet.entity.Wallet;
import jshop.domain.wallet.entity.WalletChangeType;
import jshop.domain.wallet.entity.WalletHistory;
import jshop.domain.wallet.repository.WalletHistoryRepository;
import jshop.domain.wallet.repository.WalletRepository;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletHistoryRepository walletHistoryRepository;

    @Transactional
    public Wallet createWallet() {
        Wallet wallet = Wallet
            .builder().balance(0L).build();

        walletRepository.save(wallet);

        WalletHistory walletHistory = WalletHistory
            .builder()
            .changeType(WalletChangeType.CREATE)
            .changeBalance(0L)
            .oldBalance(0L)
            .newBalance(0L)
            .wallet(wallet)
            .build();

        walletHistoryRepository.save(walletHistory);

        return wallet;
    }

    @Transactional
    public void updateBalance(Long userId, Long deltaBalance) {
        Wallet wallet = getWallet(userId);
        wallet.updateBalance(deltaBalance);
    }

    private Wallet getWallet(Long userId) {
        Optional<Wallet> optionalWallet = walletRepository.findWalletByUserId(userId);
        return optionalWallet.orElseThrow(() -> {
            log.error(ErrorCode.USER_WALLET_NOT_FOUND.getLogMessage(), userId);
            throw JshopException.of(ErrorCode.USER_WALLET_NOT_FOUND);
        });
    }
}

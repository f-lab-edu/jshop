package jshop.core.domain.user.service;

import java.util.Optional;
import jshop.core.domain.address.repository.AddressRepository;
import jshop.core.domain.user.repository.UserRepository;
import jshop.core.domain.wallet.repository.WalletRepository;
import jshop.core.domain.user.dto.JoinUserRequest;
import jshop.core.domain.user.dto.UpdateUserRequest;
import jshop.core.domain.user.dto.UpdateWalletBalanceRequest;
import jshop.core.domain.user.dto.UserInfoResponse;
import jshop.core.domain.user.entity.User;
import jshop.core.domain.wallet.entity.Wallet;
import jshop.common.exception.ErrorCode;
import jshop.common.exception.JshopException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final WalletRepository walletRepository;

    public UserInfoResponse getUserInfo(Long userId) {
        User user = getUserWithWalletAndAddress(userId);

        return UserInfoResponse.of(user);
    }

    @Transactional
    public Long joinUser(JoinUserRequest joinUserRequest) {
        String email = joinUserRequest.getEmail();

        if (userRepository.existsByEmail(email)) {
            log.error(ErrorCode.ALREADY_REGISTERED_EMAIL.getLogMessage(), email);
            throw JshopException.of(ErrorCode.ALREADY_REGISTERED_EMAIL);
        }

        User user = User.of(joinUserRequest, passwordEncoder.encode(joinUserRequest.getPassword()));
        userRepository.save(user);
        return user.getId();
    }

    @Transactional
    public void updateUser(Long userId, UpdateUserRequest updateUserRequest) {
        User user = getUser(userId);
        user.updateUserInfo(updateUserRequest);
    }

    @Transactional
    @Retryable(retryFor = {
        OptimisticLockingFailureException.class}, maxAttempts = 5, backoff = @Backoff(100), recover = "walletUpdateRecover")
    public void updateWalletBalance(Long userId, UpdateWalletBalanceRequest updateWalletBalanceRequest) {
        Wallet wallet = getWallet(userId);

        switch (updateWalletBalanceRequest.getType()) {
            case DEPOSIT:
                wallet.deposit(updateWalletBalanceRequest.getAmount());
                break;

            case WITHDRAW:
                wallet.withdraw(updateWalletBalanceRequest.getAmount());
                break;
        }
    }

    @Recover
    private void walletUpdateRecover(OptimisticLockingFailureException e, Long userId,
        UpdateWalletBalanceRequest updateWalletBalanceRequest) {
        log.error("잔고 변경 재시도 회수를 초과하였습니다. 다시 시도해 주세요. user : [{}]", userId, e);
        throw JshopException.of(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    public User getUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser.orElseThrow(() -> {
            log.error(ErrorCode.USERID_NOT_FOUND.getLogMessage(), userId);
            throw JshopException.of(ErrorCode.USERID_NOT_FOUND);
        });
    }

    public User getUserWithWalletAndAddress(Long userId) {
        Optional<User> optionalUser = userRepository.findUserWithWalletAndAddressById(userId);
        return optionalUser.orElseThrow(() -> {
            log.error(ErrorCode.USERID_NOT_FOUND.getLogMessage(), userId);
            throw JshopException.of(ErrorCode.USERID_NOT_FOUND);
        });
    }

    public Wallet getWallet(Long userId) {
        Optional<Wallet> optionalWallet = walletRepository.findWalletByUserId(userId);
        return optionalWallet.orElseThrow(() -> {
            log.error(ErrorCode.USER_WALLET_NOT_FOUND.getLogMessage(), userId);
            throw JshopException.of(ErrorCode.USER_WALLET_NOT_FOUND);
        });
    }
}

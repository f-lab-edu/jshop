package jshop.domain.user.service;

import java.util.List;
import java.util.Optional;
import jshop.domain.address.entity.Address;
import jshop.domain.address.repository.AddressRepository;
import jshop.domain.user.dto.JoinUserRequest;
import jshop.domain.user.dto.UpdateUserRequest;
import jshop.domain.user.dto.UpdateWalletBalanceRequest;
import jshop.domain.user.dto.UserInfoResponse;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import jshop.domain.wallet.entity.Wallet;
import jshop.domain.wallet.repository.WalletRepository;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import jshop.global.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final WalletRepository walletRepository;

    public UserInfoResponse getUserInfo(Long userId) {
        User user = getUser(userId);
        List<Address> addresses = addressRepository.findByUser(user);

        return UserInfoResponse.of(user, addresses);
    }

    @Transactional
    public Long joinUser(JoinUserRequest joinUserRequest) {
        String email = joinUserRequest.getEmail();

        if (userRepository.existsByEmail(email)) {
            log.error(ErrorCode.ALREADY_REGISTERED_EMAIL.getLogMessage(), email);
            throw JshopException.of(ErrorCode.ALREADY_REGISTERED_EMAIL);
        }

        User user = User.of(joinUserRequest, bCryptPasswordEncoder.encode(joinUserRequest.getPassword()));
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
        return UserUtils.getUserOrThrow(optionalUser, userId);
    }

    public Wallet getWallet(Long userId) {
        Optional<Wallet> optionalWallet = walletRepository.findWalletByUserId(userId);
        return UserUtils.getWalletOrThrow(optionalWallet, userId);
    }
}

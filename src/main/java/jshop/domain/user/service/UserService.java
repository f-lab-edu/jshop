package jshop.domain.user.service;

import java.util.List;
import java.util.Optional;
import jshop.domain.address.entity.Address;
import jshop.domain.address.repository.AddressRepository;
import jshop.domain.user.dto.JoinUserRequest;
import jshop.domain.user.dto.UpdateUserRequest;
import jshop.domain.user.dto.UserInfoResponse;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import jshop.global.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public User getUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        return UserUtils.getUserOrThrow(optionalUser, userId);
    }
}

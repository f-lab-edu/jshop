package jshop.domain.user.service;

import java.util.List;
import java.util.Optional;
import jshop.domain.address.dto.AddressInfoResponse;
import jshop.domain.address.repository.AddressRepository;
import jshop.domain.cart.entity.Cart;
import jshop.domain.user.dto.JoinDto;
import jshop.domain.user.dto.UpdateUserRequest;
import jshop.domain.user.dto.UserInfoResponse;
import jshop.domain.user.dto.UserType;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import jshop.domain.wallet.entity.Wallet;
import jshop.global.common.ErrorCode;
import jshop.global.exception.common.NoSuchEntityException;
import jshop.global.exception.user.AlreadyRegisteredEmailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public UserInfoResponse getUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.orElseThrow(() -> new NoSuchEntityException(
            "ID로 유저를 찾지 못했습니다. : " + userId, ErrorCode.USERID_NOT_FOUND));

        List<AddressInfoResponse> addresses = addressRepository
            .findByUser(user)
            .stream()
            .map(AddressInfoResponse::ofAddress)
            .toList();

        return UserInfoResponse.ofUser(user, addresses);
    }

    @Transactional
    public void joinUser(JoinDto joinDto) {
        String username = joinDto.getUsername();
        String password = joinDto.getPassword();
        String email = joinDto.getEmail();
        UserType userType = joinDto.getUserType();

        if (userRepository.existsByEmail(email)) {
            log.info(String.format("%s 는 이미 가입된 이메일입니다.", email));
            throw new AlreadyRegisteredEmailException("이미 가입된 이메일입니다.");
        }

        Wallet wallet = Wallet
            .builder().balance(0L).build();
        Cart cart = Cart
            .builder().build();

        User user = User
            .builder()
            .username(username)
            .password(bCryptPasswordEncoder.encode(password))
            .email(email)
            .userType(userType)
            .role("ROLE_USER")
            .wallet(wallet)
            .cart(cart)
            .build();

        userRepository.save(user);
    }

    @Transactional
    public void updateUser(Long userId, UpdateUserRequest updateUserRequest) {
        Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.orElseThrow(() -> new NoSuchEntityException(
            "ID로 유저를 찾지 못했습니다. : " + userId, ErrorCode.USERID_NOT_FOUND));
        user.updateUserInfo(updateUserRequest);
    }
}

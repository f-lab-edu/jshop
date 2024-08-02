package jshop.domain.user.controller;

import jakarta.validation.Valid;
import jshop.domain.user.dto.UpdateUserRequest;
import jshop.domain.user.dto.UpdateWalletBalanceRequest;
import jshop.domain.user.dto.UserInfoResponse;
import jshop.domain.user.service.UserService;
import jshop.global.annotation.CurrentUserId;
import jshop.global.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping
    public void updateUser(@CurrentUserId Long userId, @RequestBody @Valid UpdateUserRequest updateUserRequest) {
        userService.updateUser(userId, updateUserRequest);
    }

    @GetMapping
    public Response<UserInfoResponse> getUserInfo(@CurrentUserId Long userId) {
        UserInfoResponse userInfoResponse = userService.getUserInfo(userId);

        return Response
            .<UserInfoResponse>builder().data(userInfoResponse).build();
    }

    @PatchMapping("/balance")
    public void updateWalletBalance(@CurrentUserId Long userId,
        @RequestBody @Valid UpdateWalletBalanceRequest updateWalletBalanceRequest) {
        userService.updateWalletBalance(userId, updateWalletBalanceRequest);
    }
}

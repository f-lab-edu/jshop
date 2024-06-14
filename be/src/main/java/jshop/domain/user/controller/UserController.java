package jshop.domain.user.controller;

import jakarta.validation.Valid;
import java.util.Optional;
import jshop.domain.user.dto.JoinDto;
import jshop.domain.user.dto.UserInfoResponse;
import jshop.domain.user.entity.User;
import jshop.domain.user.service.UserService;
import jshop.global.dto.Response;
import jshop.global.exception.security.JwtUserNotFoundException;
import jshop.global.jwt.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Response<UserInfoResponse> join(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Optional
            .ofNullable(userDetails)
            .orElseThrow(JwtUserNotFoundException::new)
            .getId();
        UserInfoResponse userInfoResponse = userService.getUser(userId);

        return Response
            .<UserInfoResponse>builder().message("유저 정보입니다.").data(userInfoResponse).build();
    }
}

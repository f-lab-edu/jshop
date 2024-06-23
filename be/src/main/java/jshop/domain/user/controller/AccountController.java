package jshop.domain.user.controller;

import jakarta.validation.Valid;
import jshop.domain.user.dto.JoinUserRequest;
import jshop.domain.user.service.UserService;
import jshop.global.jwt.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {

    private final UserService userService;

    @PostMapping("/join")
    public void join(@RequestBody @Valid JoinUserRequest joinUserRequest) {
        userService.joinUser(joinUserRequest);
    }

    @GetMapping("/test")
    public void test(@AuthenticationPrincipal CustomUserDetails user) {
    }
}


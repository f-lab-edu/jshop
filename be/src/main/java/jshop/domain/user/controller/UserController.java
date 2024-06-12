package jshop.domain.user.controller;

import jakarta.validation.Valid;
import jshop.domain.user.dto.JoinDto;
import jshop.domain.user.entity.User;
import jshop.domain.user.service.UserService;
import jshop.global.jwt.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    @ResponseStatus(HttpStatus.OK)
    public void join(@RequestBody @Valid JoinDto joinDto) {
        userService.joinUser(joinDto);
    }

    @GetMapping("/test")
    @ResponseStatus(HttpStatus.OK)
    public String test(@AuthenticationPrincipal CustomUserDetails user) {
        return "test";
    }
}


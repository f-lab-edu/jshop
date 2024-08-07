package jshop.web.controller;

import jakarta.validation.Valid;
import jshop.core.domain.user.dto.JoinUserRequest;
import jshop.core.domain.user.dto.JoinUserResponse;
import jshop.core.domain.user.service.UserService;
import jshop.web.dto.Response;
import jshop.web.security.dto.CustomUserDetails;
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
    public Response<JoinUserResponse> join(@RequestBody @Valid JoinUserRequest joinUserRequest) {
        Long userId = userService.joinUser(joinUserRequest);

        return Response
            .<JoinUserResponse>builder()
            .data(JoinUserResponse
                .builder().id(userId).build())
            .build();
    }

    @GetMapping("/test")
    public String test(@AuthenticationPrincipal CustomUserDetails user) {
        return "test";
    }
}


package jshop.domain.user.controller;

import jshop.global.common.ResponseCode;
import jshop.domain.user.dto.JoinDto;
import jshop.global.dto.Response;
import jshop.global.dto.UserId;
import jshop.domain.user.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/join")
    public Response<UserId> join(@RequestBody JoinDto joinDto) {
        Long userId = userService.joinProcess(joinDto);

        Response<UserId> response = new Response<>();
        response.setHeader(ResponseCode.SUCCESS, "Create User");
        response.setBody(new UserId(userId));

        return response;
    }
}


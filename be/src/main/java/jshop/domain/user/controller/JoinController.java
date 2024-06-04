package jshop.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jshop.global.common.ResponseCode;
import jshop.domain.user.dto.JoinDto;
import jshop.global.dto.Response;
import jshop.global.dto.UserId;
import jshop.domain.user.service.JoinService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class JoinController {

    private final JoinService joinService;
    private final ObjectMapper objectMapper;
    public JoinController(JoinService joinService, ObjectMapper objectMapper) {
        this.joinService = joinService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/api/join")
    public Response<UserId> join(@RequestBody JoinDto joinDto) {
        System.out.println("joinDto = " + joinDto);
        Long userId = joinService.joinProcess(joinDto);

        Response<UserId> response = new Response<>();
        response.setHeader(ResponseCode.SUCCESS, "Create User");
        response.setBody(new UserId(userId));

        return response;
    }

    @PostMapping("/test")
    public String test(JoinDto joinDto) {
        joinService.joinProcess(joinDto);
        return "OK";
    }
}


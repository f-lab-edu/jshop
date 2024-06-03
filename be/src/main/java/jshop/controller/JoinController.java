package jshop.controller;

import jshop.dto.JoinDto;
import jshop.service.JoinService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class JoinController {

    private final JoinService joinService;

    public JoinController(JoinService joinService) {
        this.joinService = joinService;
    }

    @PostMapping("/api/join")
    public String join(@RequestBody JoinDto joinDto) {
        joinService.joinProcess(joinDto);
        return "OK";
    }

    @PostMapping("/test")
    public String test(JoinDto joinDto) {
        joinService.joinProcess(joinDto);
        return "OK";
    }
}


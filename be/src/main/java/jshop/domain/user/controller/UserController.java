package jshop.domain.user.controller;

import jshop.domain.user.dto.request.JoinDto;
import jshop.domain.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/api/join")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void join(@RequestBody JoinDto joinDto) {
    userService.joinProcess(joinDto);
  }
}


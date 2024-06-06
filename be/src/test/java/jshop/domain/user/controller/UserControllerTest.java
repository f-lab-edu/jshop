package jshop.domain.user.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


import jshop.domain.user.dto.JoinDto;
import jshop.domain.user.dto.UserType;
import jshop.domain.user.service.UserService;
import jshop.domain.utils.DtoBuilder;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @InjectMocks
  private UserController userController;

  @Mock
  private UserService userService;

  @Captor
  private ArgumentCaptor<JoinDto> joinDtoCapture;

  private MockMvc mockMvc;

  @BeforeEach
  public void beforeEach() {
    mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
  }

  @Test
  public void 회원가입() throws Exception {
    // given
    String username = "test";
    String email = "email@email.com";
    String password = "test";
    UserType userType = UserType.USER;

    JSONObject requestBody = new JSONObject();
    requestBody.put("username", username);
    requestBody.put("email", email);
    requestBody.put("password", password);
    requestBody.put("userType", "USER");

    JoinDto joinDto = DtoBuilder.getJoinDto(username, email, password, userType);
    // when
    ResultActions perform = mockMvc.perform(
        MockMvcRequestBuilders.post("/api/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody.toString()));

    // then
    verify(userService, times(1)).joinUser(joinDtoCapture.capture());
    JoinDto capturedJoinDto = joinDtoCapture.getValue();
    perform.andExpect(MockMvcResultMatchers.status().isNoContent());
    assertThat(capturedJoinDto).isEqualTo(joinDto);
  }
}
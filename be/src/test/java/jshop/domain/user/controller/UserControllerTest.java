package jshop.domain.user.controller;

import static jshop.utils.SecurityContextUtil.userSecurityContext;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jshop.domain.user.dto.UpdateUserRequest;
import jshop.domain.user.service.UserService;
import jshop.global.controller.GlobalExceptionHandler;
import jshop.utils.TestSecurityConfig;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(UserController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
@DisplayName("UserController Controller 테스트")
class UserControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("현재 인증된 유저의 정보를 가져옴")
    class GetUserInfo {

        @Test
        @DisplayName("토큰에 인증정보가 있다면 회원 정보를 가져올 수 있음")
        public void getUserInfo_success() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/users").with(userSecurityContext()));

            // then
            verify(userService, times(1)).getUser(1L);
            perform.andExpect(status().isOk());
        }

        @Test
        @DisplayName("토큰에 인증정보가 없다면 회원 정보를 가져올 수 없음")
        public void getUserInfo_noAuth() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/users"));
            // then
            perform.andExpect(status().isUnauthorized());

        }
    }


    @Nested
    @DisplayName("현재 인증된 유저의 정보를 갱신함")
    class UpdateUser {

        @Test
        @DisplayName("현재 인증된 유저의 이름을 갱신할 수 있다.")
        public void updateUser_success() throws Exception {
            // given
            JSONObject requestBody = new JSONObject();
            requestBody.put("username", "김재현");

            UpdateUserRequest updateUserRequest = UpdateUserRequest
                .builder().username("김재현").build();

            // when
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/users")
                .with(userSecurityContext())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.toString()));

            // then
            perform.andExpect(status().isOk());
        }

        @Test
        @DisplayName("현재 인증되지 않은 유저의 이름은 갱신할 수 없다.")
        public void updateUser_noAuth() throws Exception {
            // given
            JSONObject requestBody = new JSONObject();
            requestBody.put("username", "김재현");

            UpdateUserRequest updateUserRequest = UpdateUserRequest
                .builder().username("김재현").build();

            // when
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.toString()));

            // then
            perform.andExpect(status().isUnauthorized());
        }
    }
}
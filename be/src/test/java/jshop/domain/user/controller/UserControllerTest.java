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
class UserControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void 회원정보가져오기() throws Exception {
        // given

        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
            .get("/api/users")
            .with(userSecurityContext()));

        // then
        verify(userService, times(1)).getUser(1L);
        perform.andExpect(status().isOk());
    }

    @Test
    public void 회원정보가져오기_인증정보없음() throws Exception {
        // given

        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/users"));
        // then
        perform.andExpect(status().isUnauthorized());

    }

    @Test
    public void 유저정보업데이트() throws Exception {
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
}
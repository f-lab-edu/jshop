package jshop.domain.address.controller;


import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.securityContext;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jshop.domain.address.dto.CreateAddressRequest;
import jshop.domain.address.service.AddressService;
import jshop.domain.user.dto.JoinDto;
import jshop.domain.user.entity.User;
import jshop.domain.utils.TestSecurityConfig;
import jshop.global.jwt.dto.CustomUserDetails;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(AddressController.class)
@Import(TestSecurityConfig.class)
public class AddressControllerTest {

    @MockBean
    private AddressService addressService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    private MockMvc mockMvc;

    @Captor
    private ArgumentCaptor<CreateAddressRequest> createAddressRequestCaptor;

    @Captor
    private ArgumentCaptor<Long> userIdCaptor;

    @Test
    public void 정상주소추가() throws Exception {
        // given
        User u = getUser();
        CustomUserDetails customUserDetails = CustomUserDetails.ofUser(u);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authToken);

        JSONObject requestBody = getCreateAddressRequestJsonObject();

        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/address")
            .with(securityContext(context))
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody.toString()));

        // then
        verify(addressService, times(1)).saveAddress(createAddressRequestCaptor.capture(), userIdCaptor.capture());

        CreateAddressRequest createAddressRequest = createAddressRequestCaptor.getValue();
        Long userId = userIdCaptor.getValue();

        perform.andExpect(status().isCreated());
        assertThat(userId).isEqualTo(1L);
        assertThat(createAddressRequest.getCity()).isEqualTo("광주시");
    }

    @Test
    public void 유저정보없이_주소추가_인증안된유저() throws Exception {
        // given
        JSONObject requestBody = getCreateAddressRequestJsonObject();

        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/address")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody.toString()));

        // then
        perform.andExpect(status().isUnauthorized());

    }

    private JSONObject getCreateAddressRequestJsonObject() throws JSONException {
        JSONObject requestBody = new JSONObject();
        requestBody.put("receiverName", "김재현");
        requestBody.put("receiverNumber", "010-1234-1234");
        requestBody.put("province", "경기도");
        requestBody.put("city", "광주시");
        requestBody.put("district", "송정동");
        requestBody.put("street", "경안천로");
        requestBody.put("detailAddress1", "상세주소1");
        return requestBody;
    }

    private User getUser() {
        User u = User
            .builder()
            .id(1L)
            .username("user")
            .email("email@email.com")
            .password("password")
            .role("ROLE_USER")
            .build();
        return u;
    }
}

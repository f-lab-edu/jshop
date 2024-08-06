package jshop.web.controller;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jshop.core.domain.address.dto.CreateAddressRequest;
import jshop.core.domain.address.dto.UpdateAddressRequest;
import jshop.core.domain.address.service.AddressService;
import jshop.common.exception.ErrorCode;
import jshop.web.config.MockSecurityContextUtil;
import jshop.web.security.service.AuthorizationService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(controllers = {AddressController.class, GlobalExceptionHandler.class}, excludeAutoConfiguration =
    SecurityAutoConfiguration.class)
@DisplayName("[단위 테스트] AddressController")
public class AddressControllerTest {

    @MockBean
    private AddressService addressService;

    @MockBean
    private AuthorizationService authorizationService;

    @Autowired
    private MockMvc mockMvc;

    @Captor
    private ArgumentCaptor<CreateAddressRequest> createAddressRequestCaptor;

    @Captor
    private ArgumentCaptor<UpdateAddressRequest> updateAddressRequestArgumentCaptor;

    @Captor
    private ArgumentCaptor<Long> userIdCaptor;

    @Captor
    private ArgumentCaptor<Long> addressIdCaptor;

    @Nested
    @DisplayName("주소 생성 리퀘스트 바디 검증")
    class CreateAddress {

        private final JSONObject createAddressRequestJson = new JSONObject();

        @BeforeEach
        public void init() throws Exception {
            createAddressRequestJson.put("receiverName", "김재현");
            createAddressRequestJson.put("receiverNumber", "010-1234-1234");
            createAddressRequestJson.put("province", "경기도");
            createAddressRequestJson.put("city", "광주시");
            createAddressRequestJson.put("district", "송정동");
            createAddressRequestJson.put("street", "경안천로");
            createAddressRequestJson.put("detailAddress1", "상세주소1");
        }

        @Test
        @DisplayName("로그인한 일반 유저는 주소를 생성할 수 있다")
        public void createAddress_success() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/addresses")
                .with(MockSecurityContextUtil.mockUserSecurityContext())
                .contentType(MediaType.APPLICATION_JSON)
                .content(createAddressRequestJson.toString()));

            // then
            verify(addressService, times(1)).createAddress(createAddressRequestCaptor.capture(),
                userIdCaptor.capture());

            CreateAddressRequest createAddressRequest = createAddressRequestCaptor.getValue();
            Long userId = userIdCaptor.getValue();

            perform.andExpect(status().isOk());
            assertThat(userId).isEqualTo(MockSecurityContextUtil.getSecurityContextMockUserId());
            assertAll("createAddressRequest 검증",
                () -> assertThat(createAddressRequest.getReceiverName()).isEqualTo("김재현"),
                () -> assertThat(createAddressRequest.getReceiverNumber()).isEqualTo("010-1234-1234"),
                () -> assertThat(createAddressRequest.getProvince()).isEqualTo("경기도"),
                () -> assertThat(createAddressRequest.getCity()).isEqualTo("광주시"),
                () -> assertThat(createAddressRequest.getDistrict()).isEqualTo("송정동"),
                () -> assertThat(createAddressRequest.getStreet()).isEqualTo("경안천로"),
                () -> assertThat(createAddressRequest.getDetailAddress1()).isEqualTo("상세주소1"));
        }

        @Test
        @DisplayName("인증정보가 없다면 주소를 생성할 수 없음")
        public void createAddress_noAuth() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createAddressRequestJson.toString()));

            // then
            perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.JWT_USER_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.JWT_USER_NOT_FOUND.getMessage()));
        }

        @Test
        @DisplayName("인증 정보가 있어도 주소정보가 없다면 주소를 생성할 수 없음")
        public void createAddress_noRequest() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/addresses").with(MockSecurityContextUtil.mockUserSecurityContext()));

            // then
            perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.INVALID_REQUEST_BODY.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_REQUEST_BODY.getMessage()));
        }

        @Test
        @DisplayName("요청 정보에서 필수 정보가 없다면 주소를 생성할 수 없음")
        public void createAddress_noRequiredInfo() throws Exception {
            // given
            JSONObject requestBody = new JSONObject();
            requestBody.put("receiverName", "김재현");

            // when
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.toString()));

            // then
            perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.BAD_REQUEST.getCode()));
        }
    }

    @Nested
    @DisplayName("주소 삭제 패스 파라미터 검증")
    class DeleteAddress {

        @Test
        @DisplayName("주소 삭제시 PathVariable로 주소의 ID를 받는다")
        public void deleteAddress_success() throws Exception {
            // when
            mockMvc.perform(MockMvcRequestBuilders.delete("/api/addresses/1").with(
                MockSecurityContextUtil.mockUserSecurityContext()));

            // then
            verify(addressService, times(1)).deleteAddress(1L);
        }

        @Test
        @DisplayName("주소 삭제시 PathVariable로 주소의 ID를 받지 못하면 405를 내림 (/api/address에는 delete 메서드가 없음)")
        public void deleteAddress_noAddressId() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/addresses").with(MockSecurityContextUtil.mockUserSecurityContext()));

            // then
            perform.andExpect(status().isMethodNotAllowed());
        }
    }

    @Nested
    @DisplayName("주소 갱신 리퀘스트 바디 검증")
    class UpdateAddress {

        private final JSONObject updateAddressRequestJson = new JSONObject();

        @BeforeEach
        public void init() throws Exception {
            updateAddressRequestJson.put("receiverName", "김재현");
            updateAddressRequestJson.put("receiverNumber", "010-1234-1234");
            updateAddressRequestJson.put("province", "경기도");
            updateAddressRequestJson.put("city", "광주시");
            updateAddressRequestJson.put("district", "송정동");
            updateAddressRequestJson.put("street", "경안천로");
            updateAddressRequestJson.put("detailAddress1", "상세주소1");
        }

        @Test
        @DisplayName("주소 갱신시 PathVariable로 주소의 ID를 받는다")
        public void updateAddress_success() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/addresses/1")
                .with(MockSecurityContextUtil.mockUserSecurityContext())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateAddressRequestJson.toString()));

            // then
            verify(addressService, times(1)).updateAddress(updateAddressRequestArgumentCaptor.capture(),
                addressIdCaptor.capture());
        }

        @Test
        @DisplayName("주소 갱신시 PathVariable로 주소의 ID를 받지 못하면 405를 내림 (/api/address에는 put 메서드가 없음)")
        public void updateAddress_noAddressId() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.put("/api/addresses").with(MockSecurityContextUtil.mockUserSecurityContext()));

            // then
            perform.andExpect(status().isMethodNotAllowed());
        }

        @Test
        @DisplayName("주소 갱신시 request body가 없다면 INVALID_REQUEST_BODY 를 내림")
        public void updateAddress_noRequestBody() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.put("/api/addresses/1").with(MockSecurityContextUtil.mockUserSecurityContext()));

            // then
            perform.andExpect(status().isBadRequest());
        }
    }

}
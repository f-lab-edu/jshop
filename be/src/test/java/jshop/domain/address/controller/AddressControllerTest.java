package jshop.domain.address.controller;


import static jshop.utils.SecurityContextUtil.userSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jshop.domain.address.dto.CreateAddressRequest;
import jshop.domain.address.service.AddressService;
import jshop.global.common.ErrorCode;
import jshop.global.controller.GlobalExceptionHandler;
import jshop.utils.TestSecurityConfig;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(AddressController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
@DisplayName("AddressController Controller 테스트")
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


    @Nested
    @DisplayName("주소 생성 테스트")
    class CreateAddress {

        private static final JSONObject createAddressRequestJson = new JSONObject();

        @BeforeAll
        public static void init() throws Exception {
            createAddressRequestJson.put("receiverName", "김재현");
            createAddressRequestJson.put("receiverNumber", "010-1234-1234");
            createAddressRequestJson.put("province", "경기도");
            createAddressRequestJson.put("city", "광주시");
            createAddressRequestJson.put("district", "송정동");
            createAddressRequestJson.put("street", "경안천로");
            createAddressRequestJson.put("detailAddress1", "상세주소1");
        }

        @Test
        @DisplayName("일반 유저는 주소를 생성할 수 있음")
        public void createAddress_success() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/address")
                .with(userSecurityContext())
                .contentType(MediaType.APPLICATION_JSON)
                .content(createAddressRequestJson.toString()));

            // then
            verify(addressService, times(1)).createAddress(createAddressRequestCaptor.capture(),
                userIdCaptor.capture());

            CreateAddressRequest createAddressRequest = createAddressRequestCaptor.getValue();
            Long userId = userIdCaptor.getValue();

            perform.andExpect(status().isCreated());
            assertThat(userId).isEqualTo(1L);
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
                .post("/api/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createAddressRequestJson.toString()));

            // then
            perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.JWT_USER_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.JWT_USER_NOT_FOUND.getMessage()));
        }

        @Test
        @DisplayName("주소정보가 없다면 주소를 생성할 수 없음")
        public void createAddress_noRequest() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/address").with(userSecurityContext()));

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
                .post("/api/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.toString()));

            // then
            perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.BAD_REQUEST.getCode()));
        }
    }

}
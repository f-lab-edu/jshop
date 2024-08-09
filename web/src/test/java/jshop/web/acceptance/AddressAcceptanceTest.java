package jshop.web.acceptance;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;
import jshop.core.config.P6SpyConfig;
import jshop.core.domain.address.dto.CreateAddressResponse;
import jshop.core.domain.address.entity.Address;
import jshop.core.domain.address.repository.AddressRepository;
import jshop.core.domain.user.dto.JoinUserResponse;
import jshop.core.domain.user.dto.UserInfoResponse;
import jshop.core.domain.user.entity.User;
import jshop.core.domain.user.repository.UserRepository;
import jshop.core.domain.user.service.UserService;
import jshop.web.dto.Response;
import jshop.common.test.BaseTestContainers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@Transactional
@DisplayName("[통합 테스트] AddressController")
@Import(P6SpyConfig.class)
public class AddressAcceptanceTest extends BaseTestContainers {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    private Long user1Id;
    private String user1Token;

    @Autowired
    private MockMvc mockMvc;

    private final String createAddressRequestStr = """
        {
            "receiverName" : "kim",
            "receiverNumber" : "1234",
            "province" : "province",
            "city" : "city",
            "district": "district",
            "street" : "street",
            "detailAddress1" : "detailAddress1",
            "detailAddress2" : "detailAddress2",
            "message" : "message" 
        }
        """;

    private final String updateAddressRequestStr = """
                {
            "receiverName" : "kim2",
            "receiverNumber" : "12342",
            "province" : "province2",
            "city" : "city2",
            "district": "district2",
            "street" : "street2",
            "detailAddress1" : "detailAddress12",
            "detailAddress2" : "detailAddress22",
            "message" : "message2"
        }
        """;
    @Autowired
    private UserService userService;

    @BeforeEach
    public void init() throws Exception {
        User admin = User
            .builder()
            .username("admin")
            .password(bCryptPasswordEncoder.encode("admin"))
            .email("admin@admin.com")
            .role("ROLE_ADMIN")
            .build();

        userRepository.save(admin);

        String joinUser1 = """
            { "username" : "username", "email" : "email@email.com", "password" : "password", "userType" : "USER"}""";

        String user1LoginRequest = """
            { "email" : "email@email.com", "password" : "password" }""";

        ResultActions perform = mockMvc.perform(
            post("/api/join").contentType(MediaType.APPLICATION_JSON).content(joinUser1));

        TypeReference<Response<JoinUserResponse>> typeReference = new TypeReference<Response<JoinUserResponse>>() {};
        Response<JoinUserResponse> joinUserResponse = objectMapper.readValue(
            perform.andReturn().getResponse().getContentAsString(), typeReference);
        user1Id = joinUserResponse.getData().getId();

        ResultActions login = mockMvc.perform(
            post("/api/login").contentType(MediaType.APPLICATION_JSON).content(user1LoginRequest));
        user1Token = login.andReturn().getResponse().getHeader("Authorization");
    }

    @Nested
    @DisplayName("주소 생성 API 검증, 생성된 주소가 DB에 잘 생성 되었는지 까지 검증")
    class CreateAddress {

        @Test
        @DisplayName("로그인을 통해 인증된 사용자는 주소를 생성할 수 있다.")
        public void createAddress_success() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(post("/api/addresses")
                .header("Authorization", user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createAddressRequestStr));

            // then
            perform.andExpect(status().isOk());

            List<Address> addresses = addressRepository.findByUser(userRepository.getReferenceById(user1Id));
            assertThat(addresses.size()).isEqualTo(1);
            assertThat(addresses.get(0).getCity()).isEqualTo("city");
            assertThat(addresses.get(0).getProvince()).isEqualTo("province");
            perform.andExpect(jsonPath("$.data.id").value(addresses.get(0).getId()));
        }

        @Test
        @DisplayName("인증이 안된 사용자는 주소를 생성할 수 없다 (토큰 없는 요청)")
        public void createAddress_noAuth() throws Exception {

            // when
            ResultActions perform = mockMvc.perform(
                post("/api/addresses").contentType(MediaType.APPLICATION_JSON).content(createAddressRequestStr));

            // then
            perform.andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("주소 삭제 API 검증, 삭제된 주소가 DB에 반영되나까지 검증")
    class DeleteAddress {

        private Long addressId;

        @BeforeEach
        public void init() throws Exception {
            ResultActions perform = mockMvc.perform(post("/api/addresses")
                .header("Authorization", user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createAddressRequestStr));

            addressId = getAddressIdFromResultActions(perform);
        }

        @Test
        @DisplayName("인증이 된 사용자는 자신의 주소를 삭제할 수 있다. 삭제된 주소는 DB에 남아있으면서 회원 정보 조회시 나타나지 않음")
        public void deleteAddress_success() throws Exception {
            // when
            mockMvc.perform(delete("/api/addresses/{address_id}", addressId).header("Authorization", user1Token));

            // then
            /**
             * 유저 정보 조회시 주소가 나타나지 않음
             */
            UserInfoResponse userInfoResponseResponse = userService.getUserInfo(user1Id);
            assertThat(userInfoResponseResponse.getAddresses().size()).isEqualTo(0);

            /**
             * 주소 조회시 주소는 남아있음
             */
            Optional<Address> softDeletedAddress = addressRepository.findById(addressId);
            softDeletedAddress.ifPresentOrElse((address) -> {
                assertThat(address).isNotNull();
            }, () -> {
                Assertions.fail();
            });
        }

        @Test
        @DisplayName("없는 주소ID를 삭제하면 BAD_REQUEST")
        public void deleteAddress_noAddress() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(
                delete("/api/addresses/{address_id}", 99L).header("Authorization", user1Token));

            // then
            perform.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("자신의 주소가 아닌 주소를 삭제하면 UNAUTHORIZED")
        public void deleteAddress_noOwnership() throws Exception {
            // given
            /**
             * 유저 2로 로그인
             */
            String joinUser2 = """
                { "username" : "username2", "email" : "email2@email.com", "password" : "password", "userType" : "USER"}""";

            String user2LoginRequest = """
                { "email" : "email2@email.com", "password" : "password" }""";

            mockMvc.perform(post("/api/join").contentType(MediaType.APPLICATION_JSON).content(joinUser2));

            ResultActions login = mockMvc.perform(
                post("/api/login").contentType(MediaType.APPLICATION_JSON).content(user2LoginRequest));
            String user2Token = login.andReturn().getResponse().getHeader("Authorization");

            // when
            /**
             * 유저 2로 유저 1이 생성한 주소 삭제
             */
            UserInfoResponse userInfoResponseResponse = userService.getUserInfo(user1Id);
            assertThat(userInfoResponseResponse.getAddresses().size()).isEqualTo(1);

            ResultActions deleteResult = mockMvc.perform(
                delete("/api/addresses/{address_id}", addressId).header("Authorization", user2Token));

            // then
            deleteResult.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("인증이 안된 사용자가 변경하려 하면 Forbidden")
        public void deleteAddress_noAuth() throws Exception {
            // when
            ResultActions updateResult = mockMvc.perform(delete("/api/addresses/{address_id}", addressId));

            // then
            updateResult.andExpect(status().isForbidden());
        }
    }


    @Nested
    @DisplayName("주소 변경, 변경된 주소가 DB에 반영되나 까지 검증")
    class UpdateAddress {

        private Long addressId;

        @BeforeEach
        public void init() throws Exception {
            ResultActions perform = mockMvc.perform(post("/api/addresses")
                .header("Authorization", user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createAddressRequestStr));

            addressId = getAddressIdFromResultActions(perform);
        }

        @Test
        @DisplayName("인증된 사용자는 자신이 소유한 주소 정보를 변경할 수 있다")
        public void updateAddress_success() throws Exception {
            // when
            ResultActions updateResult = mockMvc.perform(put("/api/addresses/{address_id}", addressId)
                .header("Authorization", user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateAddressRequestStr));

            Address address = addressRepository.findById(addressId).orElseThrow(IllegalStateException::new);

            // then
            updateResult.andExpect(status().isOk());
            assertThat(address.getCity()).isEqualTo("city2");
            assertThat(address.getProvince()).isEqualTo("province2");
        }

        @Test
        @DisplayName("없는 주소ID를 변경하면 BAD_REQUEST")
        public void updateAddress_noAddress() throws Exception {
            // when
            ResultActions updateResult = mockMvc.perform(put("/api/addresses/{address_id}", 99L)
                .header("Authorization", user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateAddressRequestStr));

            // then
            updateResult.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("인증이 안된 사용자가 변경하려 하면 Forbidden")
        public void updateAddress_noAuth() throws Exception {
            ResultActions updateResult = mockMvc.perform(put("/api/addresses/{address_id}", addressId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateAddressRequestStr));

            // then
            updateResult.andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("자신의 주소가 아닌 주소를 수정하면 UNAUTHORIZED")
        public void updateAddress_noOwnership() throws Exception {
            // given
            /**
             * 유저 2로 로그인
             */
            String joinUser2 = """
                { "username" : "username2", "email" : "email2@email.com", "password" : "password", "userType" : "USER"}""";

            String user2LoginRequest = """
                { "email" : "email2@email.com", "password" : "password" }""";

            mockMvc.perform(post("/api/join").contentType(MediaType.APPLICATION_JSON).content(joinUser2));

            ResultActions login = mockMvc.perform(
                post("/api/login").contentType(MediaType.APPLICATION_JSON).content(user2LoginRequest));
            String user2Token = login.andReturn().getResponse().getHeader("Authorization");

            // when
            /**
             * 유저 2 토큰으로 유저 1이 생성한 주소 변경
             */
            ResultActions updateResult = mockMvc.perform(put("/api/addresses/{address_id}", addressId)
                .header("Authorization", user2Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateAddressRequestStr));

            // then
            updateResult.andExpect(status().isUnauthorized());
        }
    }

    private Long getAddressIdFromResultActions(ResultActions perform)
        throws JsonProcessingException, UnsupportedEncodingException {
        TypeReference<Response<CreateAddressResponse>> typeReference = new TypeReference<Response<CreateAddressResponse>>() {};
        Response<CreateAddressResponse> joinUserResponse = objectMapper.readValue(
            perform.andReturn().getResponse().getContentAsString(), typeReference);
        Long user1AddressId = joinUserResponse.getData().getId();
        return user1AddressId;
    }
}

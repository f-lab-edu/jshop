package jshop.integration.domain.address;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;
import jshop.domain.address.dto.CreateAddressResponse;
import jshop.domain.address.entity.Address;
import jshop.domain.address.repository.AddressRepository;
import jshop.domain.user.dto.JoinUserResponse;
import jshop.domain.user.dto.UserInfoResponse;
import jshop.domain.user.repository.UserRepository;
import jshop.domain.user.service.UserService;
import jshop.global.dto.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("통합테스트 AddressController")
public class AddressControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    private Long user1Id;
    private String user1Token;

    @Autowired
    private MockMvc mockMvc;

    private String createAddressRequestStr = """
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

    private String updateAddressRequestStr = """
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
    @DisplayName("주소 생성")
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
            perform.andExpect(status().isCreated());
            List<Address> addresses = addressRepository.findByUser(userRepository.getReferenceById(user1Id));
            assertThat(addresses.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("인증이 안된 사용자는 주소를 생성할 수 없다 (토큰 없는 요청)")
        public void createAddress_noAuth() throws Exception {
            ResultActions perform = mockMvc.perform(
                post("/api/addresses").contentType(MediaType.APPLICATION_JSON).content(createAddressRequestStr));

            // then
            perform.andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("주소 삭제")
    class DeleteAddress {

        @Test
        @DisplayName("인증이 된 사용자는 자신의 주소를 삭제할 수 있다. (소프트 삭제)")
        public void deleteAddress_success() throws Exception {
            // given
            ResultActions perform = mockMvc.perform(post("/api/addresses")
                .header("Authorization", user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createAddressRequestStr));

            Long addressId = getAddressIdFromResultActions(perform);

            // when
            mockMvc.perform(delete("/api/addresses/{address_id}", addressId).header("Authorization", user1Token));

            // then
            List<Address> deletedAddresses = addressRepository.findByUser(userRepository.getReferenceById(user1Id));
            Optional<Address> softDeletedAddress = addressRepository.findById(addressId);
            assertThat(deletedAddresses.size()).isEqualTo(0);

            UserInfoResponse userInfoResponseResponse = userService.getUser(user1Id);
            assertThat(userInfoResponseResponse.getAddresses().size()).isEqualTo(0);

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
        public void deleteAddress_noAuth() throws Exception {
            // given
            /**
             * 유저 1로 주소를 생성하고 유저 2로 로그인
             */
            mockMvc.perform(post("/api/addresses")
                .header("Authorization", user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createAddressRequestStr));

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
            List<Address> addresses = addressRepository.findByUser(userRepository.getReferenceById(user1Id));
            assertThat(addresses.size()).isEqualTo(1);

            Long user1AddressId = addresses.getFirst().getId();

            ResultActions deleteResult = mockMvc.perform(
                delete("/api/addresses/{address_id}", user1AddressId).header("Authorization", user2Token));

            // then
            deleteResult.andExpect(status().isUnauthorized());
        }
    }


    @Nested
    @DisplayName("주소 변경")
    class UpdateAddress {

        @Test
        @DisplayName("인증된 사용자는 자신이 소유한 주소 정보를 변경할 수 있다")
        public void updateAddress_success() throws Exception {
            // given
            ResultActions perform = mockMvc.perform(post("/api/addresses")
                .header("Authorization", user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createAddressRequestStr));

            Long addressId = getAddressIdFromResultActions(perform);

            // when
            ResultActions updateResult = mockMvc.perform(put("/api/addresses/{address_id}", addressId)
                .header("Authorization", user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateAddressRequestStr));

            Address address = addressRepository.findById(addressId).orElseThrow(IllegalStateException::new);

            // then
            updateResult.andExpect(status().isOk());
            assertThat(address.getCity()).isEqualTo("city2");
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
        @DisplayName("자신의 주소가 아닌 주소를 수정하면 UNAUTHORIZED")
        public void updateAddress_noAuth() throws Exception {
            // given
            /**
             * 유저 1로 주소를 생성하고 유저 2로 로그인
             */
            ResultActions perform = mockMvc.perform(post("/api/addresses")
                .header("Authorization", user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createAddressRequestStr));

            Long user1AddressId = getAddressIdFromResultActions(perform);

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
            ResultActions updateResult = mockMvc.perform(put("/api/addresses/{address_id}", user1AddressId)
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

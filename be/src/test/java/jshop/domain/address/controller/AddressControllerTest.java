package jshop.domain.address.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import jshop.domain.address.SaveAddressDto;
import jshop.domain.address.service.AddressService;
import jshop.domain.user.entity.User;
import jshop.global.controller.GlobalExceptionHandler;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class AddressControllerTest {

    @InjectMocks
    private AddressController addressController;

    @Mock
    private AddressService addressService;

    @Captor
    private ArgumentCaptor<SaveAddressDto> saveAddressDtoCaptor;

    @Captor
    private ArgumentCaptor<Optional<User>> optionalUserCaptor;

    private MockMvc mockMvc;

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(addressController)
            .setControllerAdvice(GlobalExceptionHandler.class)
            .build();
    }

    @Test
    @WithMockUser
    public void 정상주소추가() throws Exception {
        // given
        String city = "광주시";
        String receiverName = "김재현";

        JSONObject requestBody = new JSONObject();
        requestBody.put("receiverName", receiverName);
        requestBody.put("receiverNumber", "010-1234-1234");
        requestBody.put("province", "경기도");
        requestBody.put("city", city);
        requestBody.put("district", "송정동");
        requestBody.put("street", "경안천로");
        requestBody.put("detailAddress1", "상세주소1");

        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/address")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody.toString()));

        // then
        verify(addressService, times(1)).saveAddress(saveAddressDtoCaptor.capture(),
            optionalUserCaptor.capture());

        perform.andExpect(status().isCreated());

        SaveAddressDto saveAddressDto = saveAddressDtoCaptor.getValue();
        assertThat(saveAddressDto.getCity()).isEqualTo(city);
        assertThat(saveAddressDto.getReceiverName()).isEqualTo(receiverName);
    }
}
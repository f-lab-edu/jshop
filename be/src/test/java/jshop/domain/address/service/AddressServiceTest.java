package jshop.domain.address.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import jshop.domain.address.dto.CreateAddressRequest;
import jshop.domain.address.entity.Address;
import jshop.domain.address.repository.AddressRepository;
import jshop.domain.user.entity.User;
import jshop.global.exception.security.JwtUserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {

    @InjectMocks
    private AddressService addressService;

    @Mock
    private AddressRepository addressRepository;

    @Captor
    private ArgumentCaptor<Address> addressCaptor;

    @Test
    public void 새주소추가() {
        // given
        String city = "광주시";
        User user = User
            .builder().username("kim").email("test").build();

        CreateAddressRequest createAddressRequest = CreateAddressRequest
            .builder()
            .receiverName("김재현")
            .receiverNumber("010-1234-5678")
            .province("경기도")
            .city(city)
            .district("송정동")
            .street("경안천로")
            .detailAddress1("123-1234")
            .message("문앞에 놔주세요")
            .build();

        // when
        addressService.saveAddress(createAddressRequest, user);

        // then
        verify(addressRepository, times(1)).save(addressCaptor.capture());
        Address capturedAddress = addressCaptor.getValue();

        assertThat(capturedAddress.getUser()).isEqualTo(user);
        assertThat(capturedAddress.getCity()).isEqualTo(city);
    }
}
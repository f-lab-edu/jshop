package jshop.domain.address.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jshop.domain.address.dto.CreateAddressRequest;
import jshop.domain.address.entity.Address;
import jshop.domain.address.repository.AddressRepository;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
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

    @Mock
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<Address> addressCaptor;

    @Test
    public void saveAddress_정상새주소추가() {
        // given
        User user = getUser();
        CreateAddressRequest createAddressRequest = getCreateAddressRequest();

        // when
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        addressService.saveAddress(createAddressRequest, 1L);

        // then
        verify(addressRepository, times(1)).save(addressCaptor.capture());
        Address capturedAddress = addressCaptor.getValue();

        assertThat(capturedAddress.getUser()).isEqualTo(user);
        assertThat(CreateAddressRequest.of(capturedAddress)).isEqualTo(createAddressRequest);
    }

    private CreateAddressRequest getCreateAddressRequest() {
        CreateAddressRequest createAddressRequest = CreateAddressRequest
            .builder()
            .receiverName("김재현")
            .receiverNumber("010-1234-5678")
            .province("경기도")
            .city("광주시")
            .district("송정동")
            .street("경안천로")
            .detailAddress1("123-1234")
            .message("문앞에 놔주세요")
            .build();
        return createAddressRequest;
    }

    private User getUser() {
        User user = User
            .builder().id(1L).username("kim").email("test").role("ROLE_USER").build();
        return user;
    }
}
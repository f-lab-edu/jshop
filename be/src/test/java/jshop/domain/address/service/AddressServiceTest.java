package jshop.domain.address.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jshop.domain.address.dto.CreateAddressRequest;
import jshop.domain.address.entity.Address;
import jshop.domain.address.repository.AddressRepository;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddressService Service 테스트")
public class AddressServiceTest {

    @InjectMocks
    private AddressService addressService;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<Address> addressCaptor;

    @Nested
    @DisplayName("주소 생성 테스트")
    class CreateAddress {

        private static CreateAddressRequest createAddressRequest;
        private static User user;

        @BeforeAll
        public static void init() {
            createAddressRequest = CreateAddressRequest
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

            user = User
                .builder().id(1L).username("kim").email("test").role("ROLE_USER").build();
        }

        @Test
        @DisplayName("정상적인 요청과, 인증이 있다면 주소를 생성할 수 있음")
        public void createAddress_success() {
            // when
            when(userRepository.getReferenceById(1L)).thenReturn(user);
            addressService.createAddress(createAddressRequest, 1L);

            // then
            verify(addressRepository, times(1)).save(addressCaptor.capture());
            Address capturedAddress = addressCaptor.getValue();

            assertThat(capturedAddress.getUser()).isEqualTo(user);
            assertThat(CreateAddressRequest.of(capturedAddress)).isEqualTo(createAddressRequest);
        }
    }
}
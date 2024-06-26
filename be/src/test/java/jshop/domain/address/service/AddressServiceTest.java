package jshop.domain.address.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import jshop.domain.address.dto.CreateAddressRequest;
import jshop.domain.address.dto.UpdateAddressRequest;
import jshop.domain.address.entity.Address;
import jshop.domain.address.repository.AddressRepository;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
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

    @Nested
    @DisplayName("주소 삭제 테스트")
    class DeleteAddress {

        @Test
        @DisplayName("유저는 자신의 주소를 삭제할 수 있음")
        public void deleteAddress_success() {
            // given
            User user = User
                .builder().username("kim").id(1L).build();

            Address address = Address
                .builder().id(1L).city("광주시").user(user).build();

            // when
            when(addressRepository.findById(1L)).thenReturn(Optional.of(address));

            // then
            addressService.deleteAddress(1L, 1L);
            assertThat(address.getUser()).isNull();
            assertThat(address.getIsDeleted()).isTrue();
        }

        @Test
        @DisplayName("주소 ID로 찾을 수 없다면 ADDRESSID_NOT_FOUND 예외를 던짐")
        public void deleteAddress_addressNotFound() {
            // given
            User user = User
                .builder().username("kim").id(1L).build();

            // when
            when(addressRepository.findById(1L)).thenReturn(Optional.empty());

            // then
            JshopException jshopException = assertThrows(JshopException.class,
                () -> addressService.deleteAddress(1L, 1L));
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.ADDRESSID_NOT_FOUND);
        }

        @Test
        @DisplayName("유저는 자신의 주소가 아니라면 삭제할 수 없음")
        public void deleteAddress_userNotOwner() {
            // given
            User user = User
                .builder().username("kim").id(1L).build();

            Address address = Address
                .builder().id(1L).city("광주시").user(user).build();

            // when
            when(addressRepository.findById(1L)).thenReturn(Optional.of(address));

            // then
            JshopException jshopException = assertThrows(JshopException.class,
                () -> addressService.deleteAddress(1L, 2L));
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED);
        }
    }

    @Nested
    @DisplayName("주소 갱신 테스트")
    class UpdateAddress {

        @Test
        @DisplayName("유저는 자신의 주소를 갱신할 수 있다")
        public void updateAddress_success() {
            // given
            User user = User
                .builder().username("kim").id(1L).build();

            Address address = Address
                .builder().id(1L).city("광주시").user(user).build();

            UpdateAddressRequest updateAddressRequest = UpdateAddressRequest
                .builder().city("서울특별시").build();

            // when
            when(addressRepository.findById(1L)).thenReturn(Optional.of(address));

            // then
            addressService.updateAddress(updateAddressRequest, 1L, 1L);
            assertThat(address.getCity()).isEqualTo("서울특별시");

        }

        @Test
        @DisplayName("주소 ID로 찾을 수 없다면 ADDRESSID_NOT_FOUND 예외를 던짐")
        public void updateAddress_addressNotFound() {
            // given
            User user = User
                .builder().username("kim").id(1L).build();

            UpdateAddressRequest updateAddressRequest = UpdateAddressRequest
                .builder().build();

            // when
            when(addressRepository.findById(1L)).thenReturn(Optional.empty());

            // then
            JshopException jshopException = assertThrows(JshopException.class,
                () -> addressService.updateAddress(updateAddressRequest, 1L, 1L));
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.ADDRESSID_NOT_FOUND);
        }

        @Test
        @DisplayName("유저는 자신의 주소가 아니라면 갱신할 수 없음")
        public void updateAddress_userNotOwner() {
            // given
            User user = User
                .builder().username("kim").id(1L).build();

            UpdateAddressRequest updateAddressRequest = UpdateAddressRequest
                .builder().build();

            Address address = Address
                .builder().id(1L).city("광주시").user(user).build();

            // when
            when(addressRepository.findById(1L)).thenReturn(Optional.of(address));

            // then
            JshopException jshopException = assertThrows(JshopException.class,
                () -> addressService.updateAddress(updateAddressRequest, 1L, 2L));
            assertThat(jshopException.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED);
        }
    }
}
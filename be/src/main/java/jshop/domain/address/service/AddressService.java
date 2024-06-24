package jshop.domain.address.service;

import java.util.Optional;
import jshop.domain.address.dto.CreateAddressRequest;
import jshop.domain.address.dto.UpdateAddressRequest;
import jshop.domain.address.entity.Address;
import jshop.domain.address.repository.AddressRepository;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createAddress(CreateAddressRequest createAddressRequest, Long userId) {
        User user = userRepository.getReferenceById(userId);
        Address newAddress = Address.of(createAddressRequest, user);
        addressRepository.save(newAddress);
    }

    @Transactional
    public void deleteAddress(Long addressId, Long userId) {
        /**
         * 주소가 삭제되더라도, 주문이나 배송에서는 주소를 조회할 수 있어야 하기 때문에 소프트 삭제
         */
        Address address = getAddress(addressId);
        if (address.getUser().getId() != userId) {
            log.error(ErrorCode.UNAUTHORIZED.getLogMessage(), "address", addressId, userId);
            throw JshopException.of(ErrorCode.UNAUTHORIZED);
        }

        address.delete();
    }

    @Transactional
    public void updateAddress(UpdateAddressRequest updateAddressRequest, Long addressId, Long userId) {
        Address address = getAddress(addressId);
        if (address.getUser().getId() != userId) {
            log.error(ErrorCode.UNAUTHORIZED.getLogMessage(), "address", addressId, userId);
            throw JshopException.of(ErrorCode.UNAUTHORIZED);
        }

        address.update(updateAddressRequest);
    }

    public Address getAddress(Long addressId) {
        Optional<Address> optionalAddress = addressRepository.findById(addressId);
        Address address = optionalAddress.orElseThrow(() -> {
            log.error(ErrorCode.ADDRESSID_NOT_FOUND.getLogMessage(), addressId);
            throw JshopException.of(ErrorCode.ADDRESSID_NOT_FOUND);
        });

        return address;
    }
}

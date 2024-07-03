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
import jshop.global.jwt.dto.CustomUserDetails;
import jshop.global.utils.AddressUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
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
    public Long createAddress(CreateAddressRequest createAddressRequest, Long userId) {
        User user = userRepository.getReferenceById(userId);
        Address newAddress = Address.of(createAddressRequest, user);
        addressRepository.save(newAddress);
        return newAddress.getId();
    }

    @Transactional
    public void deleteAddress(Long addressId) {
        Address address = getAddress(addressId);
        address.delete();
    }

    @Transactional
    public void updateAddress(UpdateAddressRequest updateAddressRequest, Long addressId) {
        Address address = getAddress(addressId);
        address.update(updateAddressRequest);
    }

    public Address getAddress(Long addressId) {
        Optional<Address> optionalAddress = addressRepository.findById(addressId);
        return AddressUtils.getAddressOrThrow(optionalAddress, addressId);
    }

    public boolean checkAddressOwnership(UserDetails userDetails, Long addressId) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Long userId = customUserDetails.getId();

        Address address = getAddress(addressId);

        if (address.getUser().getId().equals(userId)) {
            return true;
        }

        log.error(ErrorCode.UNAUTHORIZED.getLogMessage(), "Address", addressId, userId);
        throw JshopException.of(ErrorCode.UNAUTHORIZED);
    }
}

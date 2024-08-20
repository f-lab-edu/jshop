package jshop.core.domain.address.service;

import java.util.Optional;
import jshop.common.exception.ErrorCode;
import jshop.common.exception.JshopException;
import jshop.core.domain.address.repository.AddressRepository;
import jshop.core.domain.address.dto.CreateAddressRequest;
import jshop.core.domain.address.dto.UpdateAddressRequest;
import jshop.core.domain.address.entity.Address;
import jshop.core.domain.user.entity.User;
import jshop.core.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
        return optionalAddress.orElseThrow(() -> {
            MDC.put("error_code", String.valueOf(ErrorCode.ADDRESSID_NOT_FOUND.getCode()));
            log.error(ErrorCode.ADDRESSID_NOT_FOUND.getLogMessage(), addressId);
            MDC.clear();
            throw JshopException.of(ErrorCode.ADDRESSID_NOT_FOUND);
        });
    }
}

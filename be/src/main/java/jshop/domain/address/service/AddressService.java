package jshop.domain.address.service;

import java.util.Optional;
import jshop.domain.address.dto.CreateAddressRequest;
import jshop.domain.address.entity.Address;
import jshop.domain.address.repository.AddressRepository;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import jshop.global.common.ErrorCode;
import jshop.global.exception.common.NoSuchEntityException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional
    public void saveAddress(CreateAddressRequest createAddressRequest, Long userId) {
        User user = userRepository.getReferenceById(userId);
        Address newAddress = Address.ofCreateAddressRequest(createAddressRequest, user);
        addressRepository.save(newAddress);
    }
}

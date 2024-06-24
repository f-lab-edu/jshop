package jshop.domain.address.service;

import jshop.domain.address.dto.CreateAddressRequest;
import jshop.domain.address.entity.Address;
import jshop.domain.address.repository.AddressRepository;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
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
    public void createAddress(CreateAddressRequest createAddressRequest, Long userId) {
        User user = userRepository.getReferenceById(userId);
        Address newAddress = Address.of(createAddressRequest, user);
        addressRepository.save(newAddress);
    }
}

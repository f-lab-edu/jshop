package jshop.domain.address.service;

import java.util.Optional;
import jshop.domain.address.dto.CreateAddressRequest;
import jshop.domain.address.entity.Address;
import jshop.domain.address.repository.AddressRepository;
import jshop.domain.user.entity.User;
import jshop.global.exception.security.JwtUserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;

    public void saveAddress(CreateAddressRequest createAddressRequest, User user) {

        Address newAddress = Address.ofCreateAddressRequest(createAddressRequest, user);
        addressRepository.save(newAddress);
    }
}

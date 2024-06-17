package jshop.domain.address.service;

import java.util.Optional;
import javax.swing.text.html.Option;
import jshop.domain.address.dto.CreateAddressRequest;
import jshop.domain.address.entity.Address;
import jshop.domain.address.repository.AddressRepository;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import jshop.global.exception.security.JwtUserNotFoundException;
import jshop.global.exception.user.UserIdNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public void saveAddress(CreateAddressRequest createAddressRequest, Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.orElseThrow(UserIdNotFoundException::new);
        Address newAddress = Address.ofCreateAddressRequest(createAddressRequest, user);
        addressRepository.save(newAddress);
    }
}

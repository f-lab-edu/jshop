package jshop.domain.address.service;

import java.util.Optional;
import jshop.domain.address.SaveAddressDto;
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

    public Address saveAddress(SaveAddressDto saveAddressDto, Optional<User> optionalUser) {

        User user = optionalUser.orElseThrow(JwtUserNotFoundException::new);
        Address newAddress = Address
            .builder()
            .receiverName(saveAddressDto.getReceiverName())
            .receiverNumber(saveAddressDto.getReceiverNumber())
            .province(saveAddressDto.getProvince())
            .city(saveAddressDto.getCity())
            .district(saveAddressDto.getDistrict())
            .street(saveAddressDto.getStreet())
            .detailAddress1(saveAddressDto.getDetailAddress1())
            .detailAddress2(saveAddressDto.getDetailAddress2())
            .message(saveAddressDto.getMessage())
            .user(user)
            .build();

        Address savedAddress = addressRepository.save(newAddress);
        return savedAddress;
    }
}

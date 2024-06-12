package jshop.domain.address.service;

import java.util.Optional;
import jshop.domain.address.dto.AddressDto;
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

    public Address saveAddress(AddressDto addressDto, Optional<User> optionalUser) {

        User user = optionalUser.orElseThrow(JwtUserNotFoundException::new);
        Address newAddress = Address
            .builder()
            .receiverName(addressDto.getReceiverName())
            .receiverNumber(addressDto.getReceiverNumber())
            .province(addressDto.getProvince())
            .city(addressDto.getCity())
            .district(addressDto.getDistrict())
            .street(addressDto.getStreet())
            .detailAddress1(addressDto.getDetailAddress1())
            .detailAddress2(addressDto.getDetailAddress2())
            .message(addressDto.getMessage())
            .user(user)
            .build();

        Address savedAddress = addressRepository.save(newAddress);
        return savedAddress;
    }
}

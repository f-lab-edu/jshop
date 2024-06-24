package jshop.domain.address.controller;


import jakarta.validation.Valid;
import jshop.domain.address.dto.CreateAddressRequest;
import jshop.domain.address.service.AddressService;
import jshop.global.annotation.CurrentUserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createAddress(@RequestBody @Valid CreateAddressRequest createAddressRequest,
        @CurrentUserId Long userId) {
        addressService.createAddress(createAddressRequest, userId);
    }
}


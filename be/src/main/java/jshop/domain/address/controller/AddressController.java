package jshop.domain.address.controller;


import jakarta.validation.Valid;
import jshop.domain.address.dto.CreateAddressRequest;
import jshop.domain.address.dto.CreateAddressResponse;
import jshop.domain.address.dto.UpdateAddressRequest;
import jshop.domain.address.service.AddressService;
import jshop.global.annotation.CurrentUserId;
import jshop.global.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public Response createAddress(@RequestBody @Valid CreateAddressRequest createAddressRequest,
        @CurrentUserId Long userId) {
        Long addressId = addressService.createAddress(createAddressRequest, userId);

        return Response
            .<CreateAddressResponse>builder()
            .data(CreateAddressResponse
                .builder().id(addressId).build())
            .build();
    }

    @DeleteMapping("/{id}")
    public void deleteAddress(@PathVariable("id") Long addressId, @CurrentUserId Long userId) {
        addressService.deleteAddress(addressId, userId);
    }

    @PutMapping("/{id}")
    public void updateAddress(@RequestBody @Valid UpdateAddressRequest updateAddressRequest,
        @PathVariable("id") Long addressId, @CurrentUserId Long userId) {
        addressService.updateAddress(updateAddressRequest, addressId, userId);
    }
}


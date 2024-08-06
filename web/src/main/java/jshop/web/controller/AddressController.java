package jshop.web.controller;


import jakarta.validation.Valid;
import jshop.core.domain.address.dto.CreateAddressRequest;
import jshop.core.domain.address.dto.CreateAddressResponse;
import jshop.core.domain.address.dto.UpdateAddressRequest;
import jshop.core.domain.address.service.AddressService;
import jshop.web.security.annotation.CurrentUserId;
import jshop.web.dto.Response;
import jshop.web.security.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
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
    private final AuthorizationService authorizationService;

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
    @PreAuthorize("isAuthenticated() && @authorizationService.checkAddressOwnership(authentication.principal, #addressId)")
    public void deleteAddress(@PathVariable("id") @P("addressId") Long addressId) {
        addressService.deleteAddress(addressId);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() && @authorizationService.checkAddressOwnership(authentication.principal, #addressId)")
    public void updateAddress(@RequestBody @Valid UpdateAddressRequest updateAddressRequest,
        @PathVariable("id") @P("addressId") Long addressId) {
        addressService.updateAddress(updateAddressRequest, addressId);
    }
}


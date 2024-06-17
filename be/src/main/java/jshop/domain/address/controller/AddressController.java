package jshop.domain.address.controller;


import jakarta.validation.Valid;
import java.util.Optional;
import jshop.domain.address.dto.CreateAddressRequest;
import jshop.domain.address.service.AddressService;
import jshop.domain.user.entity.User;
import jshop.global.annotation.CurrentUserId;
import jshop.global.exception.security.JwtUserNotFoundException;
import jshop.global.jwt.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
@Slf4j
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAddress(@RequestBody @Valid CreateAddressRequest createAddressRequest,
        @CurrentUserId Long userId) {
        addressService.saveAddress(createAddressRequest, userId);
    }
}


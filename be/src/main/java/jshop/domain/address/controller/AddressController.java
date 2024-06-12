package jshop.domain.address.controller;


import jakarta.validation.Valid;
import java.util.Optional;
import jshop.domain.address.dto.AddressDto;
import jshop.domain.address.service.AddressService;
import jshop.global.dto.Response;
import jshop.global.jwt.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public Response saveAddress(@RequestBody @Valid AddressDto addressDto,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        addressService.saveAddress(addressDto, Optional.ofNullable(userDetails.getUser()));
        return Response
            .builder().message("정상적으로 저장되었습니다.").build();
    }
}


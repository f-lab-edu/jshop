package jshop.global.utils;

import java.util.Optional;
import jshop.domain.address.entity.Address;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AddressUtils {

    public static Address getAddressOrThrow(Optional<Address> optionalAddress, Long addressId) {
        return optionalAddress.orElseThrow(() -> {
            log.error(ErrorCode.ADDRESSID_NOT_FOUND.getLogMessage(), addressId);
            throw JshopException.of(ErrorCode.ADDRESSID_NOT_FOUND);
        });
    }

}

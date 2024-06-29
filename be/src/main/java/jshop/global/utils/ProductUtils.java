package jshop.global.utils;

import java.util.Optional;
import jshop.domain.product.entity.Product;
import jshop.domain.product.entity.ProductDetail;
import jshop.global.common.ErrorCode;
import jshop.global.exception.JshopException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProductUtils {

    public static Product getProductOrThrow(Optional<Product> optionalProduct, Long productId) {
        return optionalProduct.orElseThrow(() -> {
            log.error(ErrorCode.PRODUCTID_NOT_FOUND.getLogMessage(), productId);
            throw JshopException.of(ErrorCode.PRODUCTID_NOT_FOUND);
        });
    }

    public static ProductDetail getProductDetailOrThrow(Optional<ProductDetail> optionalProductDetail,
        Long productDetailId) {
        return optionalProductDetail.orElseThrow(() -> {
            log.error(ErrorCode.PRODUCTDETAIL_ID_NOT_FOUND.getLogMessage(), productDetailId);
            throw JshopException.of(ErrorCode.PRODUCTDETAIL_ID_NOT_FOUND);
        });
    }
}

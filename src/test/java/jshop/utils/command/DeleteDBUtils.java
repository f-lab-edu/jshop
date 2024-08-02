package jshop.utils.command;

import jshop.domain.cart.repository.CartProductDetailRepository;
import jshop.domain.category.repository.CategoryRepository;
import jshop.domain.coupon.repository.CouponRepository;
import jshop.domain.coupon.repository.UserCouponRepository;
import jshop.domain.order.repository.OrderProductDetailRepository;
import jshop.domain.order.repository.OrderRepository;
import jshop.domain.product.repository.ProductDetailRepository;
import jshop.domain.product.repository.ProductRepository;
import jshop.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteDBUtils {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CouponRepository couponRepository;

    @Autowired
    CartProductDetailRepository cartProductDetailRepository;

    @Autowired
    UserCouponRepository userCouponRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderProductDetailRepository orderProductDetailRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductDetailRepository productDetailRepository;

    @Autowired
    UserRepository userRepository;

    public void destroy() {
        userCouponRepository.deleteAll();
        couponRepository.deleteAll();

        orderProductDetailRepository.deleteAll();
        orderRepository.deleteAll();

        productDetailRepository.deleteAll();
        productRepository.deleteAll();

        userRepository.deleteAll();
        categoryRepository.deleteAll();
    }
}

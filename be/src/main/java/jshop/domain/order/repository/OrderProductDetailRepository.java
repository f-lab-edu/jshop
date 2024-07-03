package jshop.domain.order.repository;

import jshop.domain.order.entity.OrderProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductDetailRepository extends JpaRepository<OrderProductDetail, Long> {}

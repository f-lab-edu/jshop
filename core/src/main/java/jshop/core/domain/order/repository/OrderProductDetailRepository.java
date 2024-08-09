package jshop.core.domain.order.repository;

import jshop.core.domain.order.entity.OrderProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductDetailRepository extends JpaRepository<OrderProductDetail, Long> {}

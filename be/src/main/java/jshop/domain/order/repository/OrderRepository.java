package jshop.domain.order.repository;

import java.util.Optional;
import jshop.domain.order.entity.Order;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"delivery"})
    Optional<Order> findById(Long id);
}

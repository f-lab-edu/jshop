package jshop.domain.order.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import jshop.domain.order.entity.Order;
import jshop.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"delivery"})
    Optional<Order> findById(Long id);

    @Query("select o from Order o where o.user = :user and o.createdAt < :lastOrderTime")
    Page<Order> findOrdersByQuery(@Param("user") User user, Pageable pageable,
        @Param("lastOrderTime") LocalDateTime lastOrderCreateTime);

    Page<Order> findOrdersByUserAndCreatedAtIsBefore(User user, LocalDateTime createdAt, Pageable pageable);
}

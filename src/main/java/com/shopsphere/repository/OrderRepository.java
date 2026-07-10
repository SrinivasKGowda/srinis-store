package com.shopsphere.repository;

import com.shopsphere.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"customer", "orderItems", "orderItems.product"})
    Optional<Order> findWithDetailsById(Long id);

    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    @Query("select coalesce(sum(o.totalAmount), 0) from Order o where o.orderDate between :start and :end and o.status <> :excludedStatus")
    BigDecimal calculateRevenueBetween(@Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end,
                                        @Param("excludedStatus") Order.OrderStatus excludedStatus);

    List<Order> findByStatusAndOrderDateBefore(Order.OrderStatus status, LocalDateTime cutoff);
}

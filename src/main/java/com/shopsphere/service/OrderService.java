package com.shopsphere.service;

import com.shopsphere.dto.OrderDto;
import com.shopsphere.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface OrderService {

    OrderDto.Response placeOrder(OrderDto.Request request);

    OrderDto.Response getById(Long id);

    Page<OrderDto.Response> getOrderHistory(Long customerId, Pageable pageable);

    OrderDto.Response updateStatus(Long id, Order.OrderStatus status);

    OrderDto.Response cancelOrder(Long id);

    BigDecimal getRevenueBetween(LocalDateTime start, LocalDateTime end);
}

package com.shopsphere.controller;

import com.shopsphere.dto.OrderDto;
import com.shopsphere.entity.Order;
import com.shopsphere.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order placement and management")
@Validated
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Place a new order")
    public ResponseEntity<OrderDto.Response> placeOrder(@Valid @RequestBody OrderDto.Request request) {
        return ResponseEntity.ok(orderService.placeOrder(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order details by id")
    public OrderDto.Response getById(@PathVariable Long id) {
        return orderService.getById(id);
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get paginated order history for a customer")
    public Page<OrderDto.Response> getOrderHistory(@PathVariable Long customerId, Pageable pageable) {
        return orderService.getOrderHistory(customerId, pageable);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order status")
    public OrderDto.Response updateStatus(@PathVariable Long id, @RequestParam Order.OrderStatus status) {
        return orderService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel an order")
    public OrderDto.Response cancelOrder(@PathVariable Long id) {
        return orderService.cancelOrder(id);
    }

    @GetMapping("/revenue")
    @Operation(summary = "Get total revenue between two dates")
    public BigDecimal getRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return orderService.getRevenueBetween(start, end);
    }
}

package com.shopsphere.service;

import com.shopsphere.dto.OrderDto;
import com.shopsphere.entity.Customer;
import com.shopsphere.entity.Order;
import com.shopsphere.entity.OrderItem;
import com.shopsphere.entity.Product;
import com.shopsphere.repository.CustomerRepository;
import com.shopsphere.repository.OrderRepository;
import com.shopsphere.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDto.Response placeOrder(OrderDto.Request request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new NoSuchElementException("Customer not found with id " + request.getCustomerId()));

        Order order = Order.builder()
                .customer(customer)
                .status(Order.OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .totalAmount(BigDecimal.ZERO)
                .orderItems(new ArrayList<>())
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (OrderDto.Request.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new NoSuchElementException("Product not found with id " + itemRequest.getProductId()));

            if (product.getInventory() == null || product.getInventory().getQuantity() < itemRequest.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product " + product.getName());
            }

            product.getInventory().setQuantity(product.getInventory().getQuantity() - itemRequest.getQuantity());

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .price(product.getPrice())
                    .build();

            order.getOrderItems().add(orderItem);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
        }

        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    @Override
    public OrderDto.Response getById(Long id) {
        Order order = orderRepository.findWithDetailsById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found with id " + id));
        return toResponse(order);
    }

    @Override
    public Page<OrderDto.Response> getOrderHistory(Long customerId, Pageable pageable) {
        return orderRepository.findByCustomerId(customerId, pageable).map(this::toResponse);
    }

    @Override
    public OrderDto.Response updateStatus(Long id, Order.OrderStatus status) {
        Order order = findOrderOrThrow(id);
        order.setStatus(status);
        return toResponse(orderRepository.save(order));
    }

    @Override
    public OrderDto.Response cancelOrder(Long id) {
        Order order = findOrderOrThrow(id);
        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order " + id + " is already cancelled");
        }
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            if (product.getInventory() != null) {
                product.getInventory().setQuantity(product.getInventory().getQuantity() + item.getQuantity());
            }
        }
        order.setStatus(Order.OrderStatus.CANCELLED);
        return toResponse(orderRepository.save(order));
    }

    @Override
    public BigDecimal getRevenueBetween(LocalDateTime start, LocalDateTime end) {
        return orderRepository.calculateRevenueBetween(start, end, Order.OrderStatus.CANCELLED);
    }

    private Order findOrderOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found with id " + id));
    }

    private OrderDto.Response toResponse(Order order) {
        List<OrderDto.Response.OrderItemResponse> items = order.getOrderItems().stream()
                .map(item -> OrderDto.Response.OrderItemResponse.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderDto.Response.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .customerName(order.getCustomer().getName())
                .status(order.getStatus().name())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .items(items)
                .build();
    }
}

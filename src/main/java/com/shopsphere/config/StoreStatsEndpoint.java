package com.shopsphere.config;

import com.shopsphere.repository.CustomerRepository;
import com.shopsphere.repository.OrderRepository;
import com.shopsphere.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Endpoint(id = "storestats")
public class StoreStatsEndpoint {

    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    @ReadOperation
    public Map<String, Object> storeStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProducts", productRepository.count());
        stats.put("totalCustomers", customerRepository.count());
        stats.put("totalOrders", orderRepository.count());
        return stats;
    }
}

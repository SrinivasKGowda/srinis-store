package com.shopsphere.scheduler;

import com.shopsphere.entity.Order;
import com.shopsphere.entity.OrderItem;
import com.shopsphere.entity.Product;
import com.shopsphere.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderScheduler {

    private final OrderRepository orderRepository;

    @Scheduled(fixedRate = 300000)
    @Transactional
    public void cancelUnpaidOrders() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(30);
        List<Order> unpaidOrders = orderRepository.findByStatusAndOrderDateBefore(Order.OrderStatus.PENDING, cutoff);

        for (Order order : unpaidOrders) {
            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();
                if (product.getInventory() != null) {
                    product.getInventory().setQuantity(product.getInventory().getQuantity() + item.getQuantity());
                }
            }
            order.setStatus(Order.OrderStatus.CANCELLED);
            orderRepository.save(order);
        }

        if (!unpaidOrders.isEmpty()) {
            log.info("Auto-cancelled {} unpaid orders older than 30 minutes", unpaidOrders.size());
        }
    }
}

package com.project.ecommerce.schedular;

import com.project.ecommerce.orders.services.OrderService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class SchedulerConfig {

    private final OrderService orderService;

    public SchedulerConfig(OrderService orderService) {
        this.orderService = orderService;
    }

    @Scheduled(fixedRate = 300000) // every 5 minutes
    public void processPendingOrders() {
        orderService.updatePendingOrders();
    }
}
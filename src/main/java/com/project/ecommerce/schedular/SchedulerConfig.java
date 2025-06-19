package com.project.ecommerce.schedular;

import com.project.ecommerce.orders.services.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
public class SchedulerConfig {

    private final OrderService orderService;

    public SchedulerConfig(OrderService orderService) {
        this.orderService = orderService;
    }

    @Scheduled(fixedRate = 300000) // runs every 5 minutes
    public void processPendingOrders() {
        log.info("Scheduler triggered to check for pending orders...");

        if (orderService.hasPendingOrders()) {
            log.info("Pending orders found!! Updating Order Status..");
            orderService.updatePendingOrders();
        } else {
            log.info("No pending orders found. Skipping processing.");
        }
    }
}
package com.project.ecommerce.schedular;

import com.project.ecommerce.orders.constants.OrderStatus;
import com.project.ecommerce.orders.dto.OrderResponseDTO;
import com.project.ecommerce.orders.services.OrderService;
import com.project.ecommerce.payments.constants.PaymentType;
import com.project.ecommerce.payments.entity.Payment;
import com.project.ecommerce.payments.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@EnableScheduling
public class SchedulerConfig {

    private final OrderService orderService;
    private final PaymentService paymentService;

    public SchedulerConfig(OrderService orderService, PaymentService paymentService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
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

    @Scheduled(fixedRate = 300000)
    public void processPaymentsForOrders() {
        if (orderService.hasPendingOrders()) {
            List<OrderResponseDTO> pendingOrders = orderService.getAllOrders(OrderStatus.PENDING);
            pendingOrders.forEach(order -> {
                Payment paymentForOrderID = paymentService.getPaymentByOrderId(order.getOrderId());
                PaymentType paymentType = paymentForOrderID.getPaymentType();
                if ("COD".equalsIgnoreCase(String.valueOf(paymentType))) {
                    orderService.updateOrderStatus(order.getOrderId(), OrderStatus.PROCESSING);
                }else if("CARD_PAYMENT".equalsIgnoreCase(String.valueOf(paymentType))) {
                    double paymentAmount  = paymentForOrderID.getAmount();
                    if(paymentAmount >= order.getBillingAmount()){
                        orderService.updateOrderStatus(order.getOrderId(), OrderStatus.PROCESSING);
                }}
            });
        }
    }
}
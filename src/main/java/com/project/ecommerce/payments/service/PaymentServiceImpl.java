package com.project.ecommerce.payments.service;

import com.project.ecommerce.orders.constants.OrderStatus;
import com.project.ecommerce.orders.dto.OrderResponseDTO;
import com.project.ecommerce.orders.entities.Order;
import com.project.ecommerce.orders.mapper.OrderMapper;
import com.project.ecommerce.orders.services.OrderService;
import com.project.ecommerce.payments.constants.PaymentStatus;
import com.project.ecommerce.payments.constants.PaymentType;
import com.project.ecommerce.payments.dto.PaymentRequestDTO;
import com.project.ecommerce.payments.entity.Payment;
import com.project.ecommerce.payments.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class PaymentServiceImpl implements PaymentService{
    private final OrderService orderService;
    private final PaymentRepository paymentRepository;
    private final OrderMapper orderMapper;

    @Override
    public boolean processPayment(PaymentRequestDTO paymentRequestDTO) {

        // Retrieve order by ID or throw if not found
        OrderResponseDTO currentOrder = orderService.getOrderById(paymentRequestDTO.getOrderId());
        String paymentType = paymentRequestDTO.getPaymentType();

        if ("COD".equalsIgnoreCase(paymentType)) {
            // If COD and order is pending, update to processing
            if ("PENDING".equalsIgnoreCase(currentOrder.getStatus())) {
                orderService.updateOrderStatus(currentOrder.getOrderId(), OrderStatus.PROCESSING);
                return true;
            }
        } else {
            // For card or other payment types
            double currentPaymentAmount = paymentRequestDTO.getAmount();
            double successfulPaymentsSum = paymentRepository.getSuccessfulPaymentsSumByOrderId(currentOrder.getOrderId());
            double newTotalPaid = successfulPaymentsSum + currentPaymentAmount;

            // Save payment
            Payment payment = new Payment();
            Order order = orderService.getOrderEntityById(paymentRequestDTO.getOrderId());
            payment.setOrder(order);
            payment.setPaymentType(PaymentType.CARD_PAYMENT);
            payment.setAmount(currentPaymentAmount);
            payment.setPaymentStatus(newTotalPaid >= currentOrder.getBillingAmount() ? PaymentStatus.Success : PaymentStatus.Partial);
            payment.setReferenceId(UUID.randomUUID().toString());
            paymentRepository.save(payment);

            // Update order if fully paid
            if (newTotalPaid >= currentOrder.getBillingAmount()) {
                orderService.updateOrderStatus(currentOrder.getOrderId(), OrderStatus.PROCESSING);
                return true;
            }
        }
        return true;
    }

    @Override
    public Payment getPaymentByOrderId(Long id) {
        return paymentRepository.findByOrder_Id(id);
    }
}

package com.project.ecommerce.payments.service;

import com.project.ecommerce.payments.dto.PaymentRequestDTO;
import com.project.ecommerce.payments.entity.Payment;

public interface PaymentService {
    boolean processPayment(PaymentRequestDTO paymentRequestDTO);
    Payment getPaymentByOrderId(Long id);
}

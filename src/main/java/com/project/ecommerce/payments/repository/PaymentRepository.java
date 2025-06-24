package com.project.ecommerce.payments.repository;

import com.project.ecommerce.payments.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByOrder_Id(Long orderId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.order.id = :orderId AND p.paymentStatus = 'PARTIAL'")
    double getSuccessfulPaymentsSumByOrderId(Long orderId);

}
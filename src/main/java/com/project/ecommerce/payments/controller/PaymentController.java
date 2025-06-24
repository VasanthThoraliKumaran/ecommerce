package com.project.ecommerce.payments.controller;

import com.project.ecommerce.payments.dto.PaymentRequestDTO;
import com.project.ecommerce.payments.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Process a new payment for the order.
     *
     * @param dto the order request payload containing customer and item details
     * @return the created order response with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<Boolean> processPayment(@Valid @RequestBody PaymentRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(paymentService.processPayment(dto));
    }
}
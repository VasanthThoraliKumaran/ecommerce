package com.project.ecommerce.payments.dto;

import com.project.ecommerce.orders.dto.OrderItemRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PaymentRequestDTO {
    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Amount must be included")
    @Valid
    private Double amount;

    @NotEmpty(message = "PaymentType must have at least one type selected")
    private String paymentType;
}

package com.project.ecommerce.orders.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDTO {
    private Long orderId;
    private String customerName;
    private List<OrderItemResponseDTO> orderItems;
    private String status;
    private LocalDateTime createdAt;
    private double billingAmount;
}
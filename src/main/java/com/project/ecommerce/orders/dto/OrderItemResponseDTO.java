package com.project.ecommerce.orders.dto;

import lombok.Data;

@Data
public class OrderItemResponseDTO {
    private String productName;
    private int quantity;
    private double price;
    private double netPrice;
}
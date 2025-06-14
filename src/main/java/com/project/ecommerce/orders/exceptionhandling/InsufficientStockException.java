package com.project.ecommerce.orders.exceptionhandling;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
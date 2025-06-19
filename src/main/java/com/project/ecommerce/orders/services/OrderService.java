package com.project.ecommerce.orders.services;

import com.project.ecommerce.orders.constants.OrderStatus;
import com.project.ecommerce.orders.dto.OrderRequestDTO;
import com.project.ecommerce.orders.dto.OrderResponseDTO;

import java.util.List;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO);

    OrderResponseDTO getOrderById(Long id);

    List<OrderResponseDTO> getAllOrders(OrderStatus status);

    OrderResponseDTO updateOrderStatus(Long id, OrderStatus status);

    void cancelOrder(Long id);

    void updatePendingOrders();

    boolean hasPendingOrders();
}


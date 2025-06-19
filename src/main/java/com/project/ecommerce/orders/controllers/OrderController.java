package com.project.ecommerce.orders.controllers;

import com.project.ecommerce.orders.constants.OrderStatus;
import com.project.ecommerce.orders.dto.OrderRequestDTO;
import com.project.ecommerce.orders.dto.OrderResponseDTO;
import com.project.ecommerce.orders.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Orders in the system.
 * Handles CRUD operations for orders and delegates business logic to OrderService.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * Creates a new order.
     *
     * @param dto the order request payload containing customer and item details
     * @return the created order response with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(dto));
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param id the ID of the order to retrieve
     * @return the order details if found, HTTP 200
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    /**
     * Updates the status of an existing order.
     *
     * @param id the ID of the order to update
     * @param status the new status to set (e.g., SHIPPED, DELIVERED)
     * @return the updated order details with HTTP 200
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        OrderResponseDTO updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * Retrieves a list of all orders, optionally filtered by status.
     *
     * @param status (optional) filter orders by their status
     * @return list of matching orders with HTTP 200
     */
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAll(@RequestParam(required = false) OrderStatus status) {
        List<OrderResponseDTO> orders = orderService.getAllOrders(status);
        return ResponseEntity.ok(orders);
    }

    /**
     * Cancels (soft deletes) an order by its ID.
     *
     * @param id the ID of the order to update order status as CANCELLED
     * @return HTTP 204 (No Content) if cancellation was successful
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }
}

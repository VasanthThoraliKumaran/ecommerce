package com.project.ecommerce.services;

import com.project.ecommerce.orders.dto.OrderResponseDTO;
import com.project.ecommerce.orders.entities.Order;
import com.project.ecommerce.orders.constants.OrderStatus;
import com.project.ecommerce.orders.exceptionhandling.InvalidOrderStatusException;
import com.project.ecommerce.orders.mapper.OrderMapper;
import com.project.ecommerce.orders.repository.OrderRepository;
import com.project.ecommerce.orders.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class OrderServiceImplTest {

    @Autowired
    private OrderService orderService;  // Spring injects OrderServiceImpl with mocks

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private OrderMapper orderMapper;

    private Order order;
    private OrderResponseDTO responseDto;

    @BeforeEach
    void setup() {
        order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);

        responseDto = new OrderResponseDTO();
        responseDto.setOrderId(1L);
    }

    @Test
    void testGetOrderById_found() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toDto(order)).thenReturn(responseDto);

        OrderResponseDTO result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
    }

    @Test
    void testGetOrderById_notFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class, () -> orderService.getOrderById(1L));
        assertTrue(ex.getMessage().contains("Order not found"));
    }

    @Test
    void testUpdateOrderStatus() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(responseDto);

        OrderResponseDTO result = orderService.updateOrderStatus(1L, OrderStatus.PROCESSING);

        assertEquals(1L, result.getOrderId());
        assertEquals(OrderStatus.PROCESSING, order.getStatus());
    }

    @Test
    void testGetAllOrders_noStatus() {
        when(orderRepository.findAll()).thenReturn(Collections.singletonList(order));
        when(orderMapper.toDto(order)).thenReturn(responseDto);

        List<OrderResponseDTO> result = orderService.getAllOrders(null);

        assertEquals(1, result.size());
    }

    @Test
    void testCancelOrder_valid() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.cancelOrder(1L);

        verify(orderRepository).save(order);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void testCancelOrder_invalidStatus() {
        order.setStatus(OrderStatus.SHIPPED);  // Example of non-cancellable status
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidOrderStatusException.class, () -> orderService.cancelOrder(1L));
    }
}

package com.project.ecommerce.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.ecommerce.orders.controllers.OrderController;
import com.project.ecommerce.orders.dto.OrderItemRequestDTO;
import com.project.ecommerce.orders.dto.OrderRequestDTO;
import com.project.ecommerce.orders.dto.OrderResponseDTO;
import com.project.ecommerce.orders.constants.OrderStatus;
import com.project.ecommerce.orders.exceptionhandling.OrderNotFoundException;
import com.project.ecommerce.orders.services.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateOrder() throws Exception {
        OrderResponseDTO responseDTO = new OrderResponseDTO();
        responseDTO.setOrderId(1L);
        responseDTO.setStatus(String.valueOf(OrderStatus.PENDING));

        when(orderService.createOrder(any(OrderRequestDTO.class))).thenReturn(responseDTO);

        OrderRequestDTO requestDTO = new OrderRequestDTO();
        requestDTO.setCustomerId(1L);
        List<OrderItemRequestDTO> orderItemRequestDTOs = new ArrayList<>();
        OrderItemRequestDTO orderItemRequestDTO = new OrderItemRequestDTO();
        orderItemRequestDTO.setProductId(1L);
        orderItemRequestDTO.setQuantity(1);
        orderItemRequestDTOs.add(orderItemRequestDTO);
        requestDTO.setOrderItems(orderItemRequestDTOs);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldReturnOrderById() throws Exception {
        OrderResponseDTO responseDTO = new OrderResponseDTO();
        responseDTO.setOrderId(1L);
        responseDTO.setStatus(String.valueOf(OrderStatus.PROCESSING));

        when(orderService.getOrderById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.status").value("PROCESSING"));
    }

    @Test
    void shouldReturn404WhenOrderNotFound() throws Exception {
        when(orderService.getOrderById(99L)).thenThrow(new OrderNotFoundException(99L));

        mockMvc.perform(get("/api/orders/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found with id: 99"))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"));
    }

    @Test
    void shouldReturnAllOrders() throws Exception {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(1L);
        dto.setStatus(String.valueOf(OrderStatus.PENDING));

        when(orderService.getAllOrders(null)).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(1L))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

}

package com.project.ecommerce.orders.mapper;

import com.project.ecommerce.orders.dto.OrderItemResponseDTO;
import com.project.ecommerce.orders.dto.OrderRequestDTO;
import com.project.ecommerce.orders.dto.OrderResponseDTO;
import com.project.ecommerce.orders.entities.Order;
import com.project.ecommerce.orders.entities.OrderItem;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    // Request DTO to Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "orderItems", ignore = true) // set manually
    Order toEntity(OrderRequestDTO dto);

    // Entity to Response DTO
    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "id", target = "orderId")
    OrderResponseDTO toDto(Order order);

    List<OrderItemResponseDTO> toOrderItemResponseList(List<OrderItem> items);

    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.price", target = "price")
    @Mapping(source = "net_price", target = "netPrice")
    OrderItemResponseDTO toOrderItemResponse(OrderItem item);
}

package com.project.ecommerce.orders.repository;

import com.project.ecommerce.orders.constants.OrderStatus;
import com.project.ecommerce.orders.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);
    boolean existsByStatus(OrderStatus status);
}

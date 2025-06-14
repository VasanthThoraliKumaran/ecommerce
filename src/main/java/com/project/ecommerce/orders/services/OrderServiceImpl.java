package com.project.ecommerce.orders.services;

import com.project.ecommerce.customers.entities.Customer;
import com.project.ecommerce.customers.repository.CustomerRepository;
import com.project.ecommerce.orders.constants.OrderStatus;
import com.project.ecommerce.orders.dto.OrderRequestDTO;
import com.project.ecommerce.orders.dto.OrderResponseDTO;
import com.project.ecommerce.orders.entities.Order;
import com.project.ecommerce.orders.entities.OrderItem;
import com.project.ecommerce.orders.exceptionhandling.InsufficientStockException;
import com.project.ecommerce.orders.exceptionhandling.InvalidOrderStatusException;
import com.project.ecommerce.orders.exceptionhandling.ResourceNotFoundException;
import com.project.ecommerce.orders.mapper.OrderMapper;
import com.project.ecommerce.orders.repository.OrderRepository;
import com.project.ecommerce.products.entities.Product;
import com.project.ecommerce.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing orders.
 * Handles creation, retrieval, status updates, cancellation, and batch updates of orders.
 */
@RequiredArgsConstructor
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;


    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO dto) {
        // Retrieve customer or throw if not found
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + dto.getCustomerId()));

        // Map request DTO to Order entity
        Order order = orderMapper.toEntity(dto);
        order.setCustomer(customer);

        // Build list of OrderItems, validate stock, and update product quantities
        List<OrderItem> items = dto.getOrderItems().stream().map(itemDto -> {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + itemDto.getProductId()));

            if (product.getAvailableQuantity() < itemDto.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
            }

            // Reduce available quantity
            product.setAvailableQuantity(product.getAvailableQuantity() - itemDto.getQuantity());

            // Create OrderItem
            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setOrder(order);
            item.setQuantity(itemDto.getQuantity());
            item.setNet_price(product.getPrice() * itemDto.getQuantity());

            return item;
        }).collect(Collectors.toList());

        order.setOrderItems(items);

        // Save order and return DTO
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    public OrderResponseDTO getOrderById(Long id) {
        // Retrieve order by ID or throw if not found
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderResponseDTO> getAllOrders(OrderStatus status) {
        // Retrieve orders, optionally filtered by status
        List<Order> orders = (status != null)
                ? orderRepository.findByStatus(status)
                : orderRepository.findAll();

        // Map entities to DTOs
        return orders.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDTO updateOrderStatus(Long id, OrderStatus status) {
        // Retrieve order and update status
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
        order.setStatus(status);
        Order updated = orderRepository.save(order);
        return orderMapper.toDto(updated);
    }

    @Override
    public void cancelOrder(Long id) {
        // Retrieve order and cancel if it is pending
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException("Only PENDING orders can be cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Override
    public void updatePendingOrders() {
        // Scheduler will update all PENDING orders to PROCESSING after 5 minutes
        List<Order> pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING);
        for (Order order : pendingOrders) {
            order.setStatus(OrderStatus.PROCESSING);
            orderRepository.save(order);
        }
    }

}
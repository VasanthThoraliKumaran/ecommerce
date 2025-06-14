package com.project.ecommerce.uicontroller;

import com.project.ecommerce.customers.entities.Customer;
import com.project.ecommerce.customers.repository.CustomerRepository;
import com.project.ecommerce.orders.entities.Order;
import com.project.ecommerce.orders.repository.OrderRepository;

import com.project.ecommerce.products.entities.Product;
import com.project.ecommerce.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class OrderUIController {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    //UI - Thymeleaf
    @GetMapping("/orders-ui/summary")
    public String viewOrderSummary(Model model) {
        List<Order> orders = orderRepository.findAll();
        model.addAttribute("orders", orders);
        return "order-summary";
    }

    @GetMapping("/orders-ui/dashboard")
    public String dashboard(Model model) {
        List<Customer> customers = customerRepository.findAll();
        List<Product> products = productRepository.findAll();

        model.addAttribute("customers", customers);
        model.addAttribute("products", products);

        return "dashboard";
    }

}


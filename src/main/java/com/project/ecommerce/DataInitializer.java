package com.project.ecommerce;

import com.project.ecommerce.customers.entities.Customer;
import com.project.ecommerce.products.entities.Product;
import com.project.ecommerce.customers.repository.CustomerRepository;
import com.project.ecommerce.products.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public DataInitializer(CustomerRepository customerRepository, ProductRepository productRepository) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (customerRepository.count() == 0) {
            List<Customer> customers = List.of(
                    Customer.builder().name("Alice").build(),
                    Customer.builder().name("Bob").build(),
                    Customer.builder().name("Mike").build(),
                    Customer.builder().name("Jason").build(),
                    Customer.builder().name("Katy").build()
            );
            customerRepository.saveAll(customers);
        }

        if (productRepository.count() == 0) {
            List<Product> products = List.of(
                    Product.builder().name("Phone").price(800.00).availableQuantity(50).build(),
                    Product.builder().name("Tablet").price(400.00).availableQuantity(50).build(),
                    Product.builder().name("Monitor").price(300.00).availableQuantity(50).build(),
                    Product.builder().name("Keyboard").price(50.00).availableQuantity(50).build(),
                    Product.builder().name("Mouse").price(30.00).availableQuantity(50).build(),
                    Product.builder().name("Printer").price(200.00).availableQuantity(50).build(),
                    Product.builder().name("Camera").price(500.00).availableQuantity(50).build(),
                    Product.builder().name("Speaker").price(100.00).availableQuantity(50).build(),
                    Product.builder().name("Router").price(80.00).availableQuantity(10).build(),
                    Product.builder().name("SSD").price(150.00).availableQuantity(10).build(),
                    Product.builder().name("HDD").price(100.00).availableQuantity(10).build(),
                    Product.builder().name("RAM").price(60.00).availableQuantity(15).build(),
                    Product.builder().name("Power Bank").price(40.00).availableQuantity(18).build(),
                    Product.builder().name("Webcam").price(70.00).availableQuantity(11).build()
            );
            productRepository.saveAll(products);
        }
    }
}

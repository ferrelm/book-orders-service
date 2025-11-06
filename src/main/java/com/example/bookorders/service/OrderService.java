package com.example.bookorders.service;

import com.example.bookorders.model.Order;
import com.example.bookorders.repository.OrderRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public List<Order> getAllOrders() {
        return repository.findAll();
    }

    public Order createOrder(Order order) {
        order.setStatus("CREATED");
        return repository.save(order);
    }
}

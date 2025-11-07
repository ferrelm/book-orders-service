package com.example.bookorders.controller;

import com.example.bookorders.model.Order;
import com.example.bookorders.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private OrderService service;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void listOrders_returnsList() throws Exception {
        Order o = Order.builder()
                .id("1")
                .customerName("Alice")
                .bookTitle("1984")
                .quantity(1)
                .status("CREATED")
                .build();

        given(service.getAllOrders()).willReturn(List.of(o));

        mvc.perform(get("/api/orders").accept(MediaType.APPLICATION_JSON).with(jwt()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].customerName").value("Alice"));
    }

    @Test
    void createOrder_returns201AndLocation() throws Exception {
        Order request = Order.builder()
                .customerName("Bob")
                .bookTitle("Brave New World")
                .quantity(2)
                .build();

        Order created = Order.builder()
                .id("42")
                .customerName(request.getCustomerName())
                .bookTitle(request.getBookTitle())
                .quantity(request.getQuantity())
                .status("CREATED")
                .build();

        given(service.createOrder(any(Order.class))).willReturn(created);

        mvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .with(csrf())
                        .with(jwt()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/api/orders/42")))
                .andExpect(jsonPath("$.id").value("42"))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }
}

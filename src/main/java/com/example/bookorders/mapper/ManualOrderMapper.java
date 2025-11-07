package com.example.bookorders.mapper;

import com.example.bookorders.model.Order;
import com.example.bookorders.model.OrderDto;
import org.springframework.stereotype.Component;

/**
 * Manual mapper implementation used for tests and examples. This avoids MapStruct/Lombok
 * annotation processing ordering issues in some environments while still demonstrating
 * DTO <-> entity mapping.
 */
@Component
public class ManualOrderMapper {

    public OrderDto toDto(Order order) {
        if (order == null) return null;
        return OrderDto.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .bookTitle(order.getBookTitle())
                .quantity(order.getQuantity())
                .status(order.getStatus())
                .build();
    }

    public Order toEntity(OrderDto dto) {
        if (dto == null) return null;
        return Order.builder()
                .id(dto.getId())
                .customerName(dto.getCustomerName())
                .bookTitle(dto.getBookTitle())
                .quantity(dto.getQuantity())
                .status(dto.getStatus())
                .build();
    }
}

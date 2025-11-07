package com.example.bookorders.mapper;

import com.example.bookorders.model.Order;
import com.example.bookorders.model.OrderDto;
import org.mapstruct.Mapper;
 

@Mapper(componentModel = "spring")
public interface OrderMapper {

    // Provide an explicit implementation for toDto to avoid MapStruct/Lombok
    // annotation processing ordering issues in some environments.
    default OrderDto toDto(Order order) {
        if (order == null) return null;
        return OrderDto.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .bookTitle(order.getBookTitle())
                .quantity(order.getQuantity())
                .status(order.getStatus())
                .build();
    }

    // Let MapStruct generate the toEntity implementation.
    Order toEntity(OrderDto dto);
}

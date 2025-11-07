package com.example.bookorders.mapper;

import com.example.bookorders.model.Order;
import com.example.bookorders.model.OrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderDto toDto(Order order);

    @Mapping(target = "id", ignore = true)
    Order toEntity(OrderDto dto);
}

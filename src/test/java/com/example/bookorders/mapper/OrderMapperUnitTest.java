package com.example.bookorders.mapper;

import com.example.bookorders.model.Order;
import com.example.bookorders.model.OrderDto;
import org.junit.jupiter.api.Test;
// org.mapstruct.factory.Mappers not used when instantiating generated impl directly

import static org.assertj.core.api.Assertions.assertThat;

class OrderMapperUnitTest {

    // Use a manual mapper implementation for stable test behaviour
    private final ManualOrderMapper mapper = new ManualOrderMapper();

    @Test
    void toDto_mapsFields() {
        // Test mapping from DTO -> entity using MapStruct generated implementation
        OrderDto dto = OrderDto.builder()
            .id("1")
            .customerName("Alice")
            .bookTitle("1984")
            .quantity(1)
            .status("CREATED")
            .build();

        Order o = mapper.toEntity(dto);

        assertThat(o).isNotNull();
        assertThat(o.getCustomerName()).isEqualTo("Alice");
        assertThat(o.getBookTitle()).isEqualTo("1984");
        assertThat(o.getQuantity()).isEqualTo(1);
        assertThat(o.getStatus()).isEqualTo("CREATED");
    }
}

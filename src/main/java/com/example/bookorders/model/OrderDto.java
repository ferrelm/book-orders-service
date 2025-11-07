package com.example.bookorders.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private String id;
    private String customerName;
    private String bookTitle;
    private int quantity;
    private String status;
}

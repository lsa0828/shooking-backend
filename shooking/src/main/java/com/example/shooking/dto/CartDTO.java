package com.example.shooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartDTO {
    private Long productId;
    private String brand;
    private String description;
    private Integer price;
    private Integer quantity;
}

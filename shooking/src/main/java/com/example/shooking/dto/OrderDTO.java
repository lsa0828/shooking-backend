package com.example.shooking.dto;

import com.example.shooking.entity.OrderSheet;
import com.example.shooking.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private Long productId;
    private String brand;
    private String description;
    private Integer price;
    private Integer quantity;
    private LocalDate orderedAt;

    public OrderDTO(OrderSheet orderSheet) {
        Product product = orderSheet.getProduct();
        this.id = orderSheet.getId();
        this.productId = product.getId();
        this.brand = product.getBrand();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.quantity = orderSheet.getQuantity();
        this.orderedAt = orderSheet.getOrderedAt();
    }
}

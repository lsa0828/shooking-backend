package com.example.shooking.dto;

import com.example.shooking.entity.Cart;
import com.example.shooking.entity.Product;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartDTO {
    private Long id;
    private String brand;
    private String description;
    private Integer price;
    private Integer quantity;

    public CartDTO(Cart cart) {
        Product product = cart.getProduct();
        this.id = product.getId();
        this.brand = product.getBrand().getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.quantity = cart.getQuantity();
    }
}

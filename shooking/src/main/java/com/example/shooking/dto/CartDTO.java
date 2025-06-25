package com.example.shooking.dto;

import com.example.shooking.entity.Cart;
import com.example.shooking.entity.Product;
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

    public CartDTO(Cart cart) {
        Product product = cart.getProduct();
        this.productId = product.getId();
        this.brand = product.getBrand();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.quantity = cart.getQuantity();
    }
}

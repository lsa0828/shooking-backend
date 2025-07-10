package com.example.shooking.dto;

import com.example.shooking.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String brand;
    private String description;
    private Integer price;

    public ProductDTO(Product product) {
        this.id = product.getId();
        this.brand = product.getBrand().getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
    }
}

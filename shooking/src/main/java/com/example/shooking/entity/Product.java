package com.example.shooking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq_gen")
    @SequenceGenerator(
            name = "product_seq_gen",
            sequenceName = "product_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "image_path", nullable = false)
    private String imagePath;

    public Product(Brand brand, String description, Integer price, String imagePath) {
        this.brand = brand;
        this.description = description;
        this.price = price;
        this.imagePath = imagePath;
    }
}

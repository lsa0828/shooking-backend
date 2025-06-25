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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq_gen")
    @SequenceGenerator(
            name = "member_seq_gen",
            sequenceName = "member_seq",
            allocationSize = 1
    )
    private Long id;
    @Column(nullable = false)
    private String brand;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private Integer price;
    @Column(name = "image_path", nullable = false)
    private String imagePath;

    public Product(String brand, String description, Integer price, String imagePath) {
        this.brand = brand;
        this.description = description;
        this.price = price;
        this.imagePath = imagePath;
    }
}

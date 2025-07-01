package com.example.shooking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "brand_seq_gen")
    @SequenceGenerator(
            name = "brand_seq_gen",
            sequenceName = "brand_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false)
    private String name;

    public Brand(String name) {
        this.name = name;
    }
}

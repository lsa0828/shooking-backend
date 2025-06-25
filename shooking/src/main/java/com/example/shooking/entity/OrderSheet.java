package com.example.shooking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_sheet")
public class OrderSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq_gen")
    @SequenceGenerator(
            name = "order_seq_gen",
            sequenceName = "order_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "ordered_at", nullable = false)
    private LocalDate orderedAt;

    public OrderSheet(Member member, Product product, Integer quantity) {
        this.member = member;
        this.product = product;
        this.quantity = quantity;
        this.orderedAt = LocalDate.now();
    }
}

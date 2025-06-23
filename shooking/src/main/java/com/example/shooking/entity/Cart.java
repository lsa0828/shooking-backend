package com.example.shooking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @EmbeddedId
    private CartId id;

    @MapsId("memberId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @MapsId("productId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;

    public Cart(Member member, Product product, Integer quantity) {
        this.id = new CartId(member.getId(), product.getId());
        this.member = member;
        this.product = product;
        this.quantity = quantity;
    }
}

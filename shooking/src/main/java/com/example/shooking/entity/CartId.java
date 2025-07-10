package com.example.shooking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class CartId implements Serializable {
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartId cartId)) return false;
        return Objects.equals(memberId, cartId.memberId) &&
                Objects.equals(productId, cartId.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, productId);
    }
}

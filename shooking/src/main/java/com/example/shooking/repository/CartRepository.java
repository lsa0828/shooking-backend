package com.example.shooking.repository;

import com.example.shooking.entity.Cart;
import com.example.shooking.entity.CartId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, CartId> {
    List<Cart> findByMemberId(Long memberId);

    Cart findByMemberIdAndProductId(Long memberId, Long productId);

    void deleteByMemberIdAndProductId(Long memberId, Long productId);

    void deleteByMemberId(Long memberId);
}

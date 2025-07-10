package com.example.shooking.controller;

import com.example.shooking.dto.ApiResponse;
import com.example.shooking.dto.CartDTO;
import com.example.shooking.entity.Member;
import com.example.shooking.security.CurrentMember;
import com.example.shooking.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<?> showProductsInCart(@CurrentMember Member member) {
        Long memberId = member.getId();
        List<CartDTO> cartList = cartService.getProductsInCart(memberId);

        if (cartList.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success("장바구니가 비어 있습니다.", cartList));
        }
        return ResponseEntity.ok(ApiResponse.success("장바구니 조회", cartList));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> showQuantityOfProductInCart(@CurrentMember Member member, @PathVariable Long productId) {
        Long memberId = member.getId();
        CartDTO cartDTO = cartService.getQuantityOfProductInCart(memberId, productId);
        return ResponseEntity.ok(ApiResponse.success("장바구니 상품 수량 조회", cartDTO));
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<?> toggleCartItem(@CurrentMember Member member, @PathVariable Long productId) {
        Long memberId = member.getId();
        CartDTO cartDTO = cartService.toggleCartItem(memberId, productId);
        return ResponseEntity.ok(ApiResponse.success("장바구니 상품 담김 여부 변경", cartDTO));
    }

    @PatchMapping("/{productId}/{quantity}")
    public ResponseEntity<?> upsertCartItem(@CurrentMember Member member, @PathVariable Long productId, @PathVariable Integer quantity) {
        Long memberId = member.getId();
        CartDTO cartDTO = cartService.upsertCartItem(memberId, productId, quantity);
        return ResponseEntity.ok(ApiResponse.success("장바구니 상품 수량 변경 또는 저장", cartDTO));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteProductsInCart(@CurrentMember Member member) {
        Long memberId = member.getId();
        cartService.deleteProductsInCart(memberId);
        return ResponseEntity.ok(ApiResponse.success("장바구니 삭제"));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteCartItem(@CurrentMember Member member, @PathVariable Long productId) {
        Long memberId = member.getId();
        cartService.deleteCartItem(memberId, productId);
        return ResponseEntity.ok(ApiResponse.success("장바구니 상품 삭제"));
    }
}

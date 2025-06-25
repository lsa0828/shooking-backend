package com.example.shooking.controller;

import com.example.shooking.dto.ApiResponse;
import com.example.shooking.dto.OrderDTO;
import com.example.shooking.entity.Member;
import com.example.shooking.security.CurrentMember;
import com.example.shooking.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<?> showOrderList(@CurrentMember Member member) {
        Long memberId = member.getId();
        List<OrderDTO> orderList = orderService.getOrderList(memberId);
        return ResponseEntity.ok(ApiResponse.success("주문 내역 조회", orderList));
    }

    @PostMapping("/{productId}/{quantity}")
    public ResponseEntity<?> orderProduct(@CurrentMember Member member, @PathVariable Long productId, @PathVariable Integer quantity) {
        Long memberId = member.getId();
        OrderDTO order = orderService.orderProduct(memberId, productId, quantity);
        return ResponseEntity.ok(ApiResponse.success("상품 주문", order));
    }

    @PostMapping("/cart")
    public ResponseEntity<?> orderProductsInCart(@CurrentMember Member member) {
        Long memberId = member.getId();
        List<OrderDTO> orderList = orderService.orderProductsInCart(memberId);
        return ResponseEntity.ok(ApiResponse.success("장바구니 상품 목록 주문", orderList));
    }
}

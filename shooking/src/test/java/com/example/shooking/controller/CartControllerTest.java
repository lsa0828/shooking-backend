package com.example.shooking.controller;

import com.example.shooking.dto.CartDTO;
import com.example.shooking.entity.Member;
import com.example.shooking.security.CurrentMemberArgumentResolver;
import com.example.shooking.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
public class CartControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private CurrentMemberArgumentResolver currentMemberArgumentResolver;

    private Member member;

    @BeforeEach
    void setup() throws Exception {
        member = new Member(1L, "test", "1234", "테스트유저", LocalDate.of(2000, 1, 3), LocalDate.of(2025, 6, 24), "USER");
        given(currentMemberArgumentResolver.supportsParameter(any())).willReturn(true);
        given(currentMemberArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(member);
    }

    @Test
    @DisplayName("정상적인 장바구니 목록 조회 API 요청")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void showProductInCart_ShouldReturnCartList() throws Exception {
        Long memberId = member.getId();
        List<CartDTO> cartList = List.of(
                new CartDTO(1L, "브랜드1", "편한 신발", 16000, 1),
                new CartDTO(2L, "브랜드2", "멋진 신발", 15000, 2)
        );
        given(cartService.getProductsInCart(memberId)).willReturn(cartList);

        mockMvc.perform(get("/api/product/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("장바구니 조회"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].quantity").value(1))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].length()").value(5));
    }

    @Test
    @DisplayName("정상적인 비어있는 장바구니 목록 조회 API 요청")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void showProductInCart_ShouldReturnEmptyCartList() throws Exception {
        Long memberId = member.getId();
        List<CartDTO> cartList = new ArrayList<>();
        given(cartService.getProductsInCart(memberId)).willReturn(cartList);

        mockMvc.perform(get("/api/product/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("장바구니가 비어 있습니다."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("정상적인 장바구니 상품 수량 조회 API 요청")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void showQuantityOfProductInCart_ShouldReturnQuantity() throws Exception {
        Long memberId = member.getId();
        Long productId = 1L;
        Integer quantity = 2;
        given(cartService.getQuantityOfProductInCart(memberId, productId)).willReturn(quantity);

        mockMvc.perform(get("/api/product/cart/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("장바구니 상품 수량 조회"))
                .andExpect(jsonPath("$.data").value(2));
    }

    @Test
    @DisplayName("정상적인 장바구니 상품 담기 API 요청")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void toggleCartItem_ShouldReturnQuantity() throws Exception {
        Long productId = 1L;
        Integer quantity = 1;
        given(cartService.toggleCartItem(member, productId)).willReturn(quantity);

        mockMvc.perform(patch("/api/product/cart/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("장바구니 상품 담김 여부 변경"))
                .andExpect(jsonPath("$.data").value(1));
    }

    @Test
    @DisplayName("정상적인 장바구니 상품 수량 변경 또는 저장 API 요청")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void upsertCartItem_ShouldReturnQuantity() throws Exception {
        Long productId = 1L;
        Integer quantity = 3;
        given(cartService.upsertCartItem(member, productId, quantity)).willReturn(quantity);

        mockMvc.perform(patch("/api/product/cart/1/3")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("장바구니 상품 수량 변경 또는 저장"))
                .andExpect(jsonPath("$.data").value(3));
    }

    @Test
    @DisplayName("정상적인 장바구니 목록 삭제 API 요청")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteProductsInCart_ShouldSucceed() throws Exception {
        Long memberId = member.getId();
        willDoNothing().given(cartService).deleteProductsInCart(memberId);

        mockMvc.perform(delete("/api/product/cart")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("장바구니 삭제"));
    }

    @Test
    @DisplayName("정상적인 장바구니 상품 삭제 API 요청")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteCartItem_ShouldSucceed() throws Exception {
        Long memberId = member.getId();
        Long productId = 1L;
        willDoNothing().given(cartService).deleteCartItem(memberId, productId);

        mockMvc.perform(delete("/api/product/cart/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("장바구니 상품 삭제"));
    }
}

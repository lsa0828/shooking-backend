package com.example.shooking.controller;

import com.example.shooking.dto.OrderDTO;
import com.example.shooking.entity.Member;
import com.example.shooking.security.CurrentMemberArgumentResolver;
import com.example.shooking.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

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
    @DisplayName("정상적인 주문 내역 조회 API 요청")
    @WithMockUser(username = "test", roles = "USER")
    void showOrderList_ShouldReturnOrderList() throws Exception {
        Long memberId = member.getId();
        List<OrderDTO> orderList = List.of(
                new OrderDTO(1L, 2L, "브랜드1", "편한 신발", 16000, 1, LocalDate.of(2000, 6, 25)),
                new OrderDTO(2L, 3L, "브랜드2", "멋진 신발", 15000, 3, LocalDate.of(2000, 6, 25))
        );
        given(orderService.getOrderList(memberId)).willReturn(orderList);

        mockMvc.perform(get("/api/order"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("주문 내역 조회"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    @DisplayName("정상적인 상품 주문 API 요청")
    @WithMockUser(username = "test", roles = "USER")
    void orderProduct_ShouldReturnOrder() throws Exception {
        Long memberId = member.getId();
        Long productId = 2L;
        Integer quantity = 2;
        OrderDTO order = new OrderDTO(1L, productId, "브랜드1", "편한 신발", 16000, quantity, LocalDate.of(2000, 6, 25));
        given(orderService.orderProduct(memberId, productId, quantity)).willReturn(order);

        mockMvc.perform(post("/api/order/2/2")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("상품 주문"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.brand").value("브랜드1"));
    }

    @Test
    @DisplayName("정상적인 장바구니 상품 목록 주문 API 요청")
    @WithMockUser(username = "test", roles = "USER")
    void orderProductsInCart_ShouldReturnOrderList() throws Exception {
        Long memberId = member.getId();
        List<OrderDTO> orderList = List.of(
                new OrderDTO(1L, 2L, "브랜드1", "편한 신발", 16000, 1, LocalDate.of(2000, 6, 25)),
                new OrderDTO(2L, 3L, "브랜드2", "멋진 신발", 15000, 3, LocalDate.of(2000, 6, 25))
        );
        given(orderService.orderProductsInCart(memberId)).willReturn(orderList);

        mockMvc.perform(post("/api/order/cart")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("장바구니 상품 목록 주문"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].productId").value(2))
                .andExpect(jsonPath("$.data[0].brand").value("브랜드1"));
    }
}

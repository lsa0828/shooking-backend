package com.example.shooking.integration;

import com.example.shooking.entity.*;
import com.example.shooking.repository.CartRepository;
import com.example.shooking.repository.MemberRepository;
import com.example.shooking.repository.OrderRepository;
import com.example.shooking.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Long memberId;
    private Long productId;

    @BeforeEach
    void setup() throws Exception {
        Member member = memberRepository.save(new Member("test", "1234", "테스트유저", LocalDate.of(2000, 1, 3), LocalDate.of(2025, 6, 24), "USER"));
        Product product = productRepository.save(new Product("브랜드", "멋진 신발", 15000, "img.jpg"));
        memberId = member.getId();
        productId = product.getId();
        CartId cartId = new CartId(member.getId(), product.getId());
        cartRepository.save(new Cart(cartId, member, product, 2));
        orderRepository.save(new OrderSheet(member, product, 5));

        Authentication auth = new UsernamePasswordAuthenticationToken(member, null, List.of(new SimpleGrantedAuthority("USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("정상적인 주문 내역 조회")
    @WithMockUser(username = "test", roles = "USER")
    void testGetOrderList() throws Exception {
        String orderedAt = LocalDate.now().toString();
        mockMvc.perform(get("/api/order"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("주문 내역 조회"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].brand").value("브랜드"))
                .andExpect(jsonPath("$.data[0].orderedAt").value(orderedAt));
    }

    @Test
    @DisplayName("정상적인 상품 주문")
    @WithMockUser(username = "test", roles = "USER")
    void testOrderProduct() throws Exception {
        String orderedAt = LocalDate.now().toString();
        mockMvc.perform(post("/api/order/"+ productId +"/10")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("상품 주문"))
                .andExpect(jsonPath("$.data.brand").value("브랜드"))
                .andExpect(jsonPath("$.data.quantity").value(10))
                .andExpect(jsonPath("$.data.orderedAt").value(orderedAt));
    }

    @Test
    @DisplayName("존재하지 않는 상품 주문")
    @WithMockUser(username = "test", roles = "USER")
    void testOrderProduct_EmptyOrder() throws Exception {
        mockMvc.perform(post("/api/order/9999/1")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("상품이 존재하지 않습니다")));
    }

    @Test
    @DisplayName("정상적인 장바구니 상품 목록 주문")
    @WithMockUser(username = "test", roles = "USER")
    void testOrderProductsInCart() throws Exception {
        mockMvc.perform(post("/api/order/cart")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("장바구니 상품 목록 주문"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].quantity").value(2));

        List<OrderSheet> orderList = orderRepository.findByMemberId(memberId);
        assertEquals(2, orderList.size());
    }
}

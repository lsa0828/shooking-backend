package com.example.shooking.integration;

import com.example.shooking.entity.Cart;
import com.example.shooking.entity.CartId;
import com.example.shooking.entity.Member;
import com.example.shooking.entity.Product;
import com.example.shooking.repository.CartRepository;
import com.example.shooking.repository.MemberRepository;
import com.example.shooking.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CartIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member member;
    private Product product;

    @BeforeEach
    void setup() throws Exception {
        member = memberRepository.save(new Member("test", "1234", "테스트유저", LocalDate.of(2000, 1, 3), LocalDate.of(2025, 6, 24), "USER"));
        product = productRepository.save(new Product("브랜드2", "멋진 신발", 15000, "img2.jpg"));
        CartId cartId = new CartId(member.getId(), product.getId());
        Cart cart = new Cart(cartId, member, product, 2);
        cartRepository.save(cart);

        Authentication auth = new UsernamePasswordAuthenticationToken(member, null, List.of(new SimpleGrantedAuthority("USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("정상적인 장바구니 목록 조회")
    @WithMockUser(username = "test", roles = "USER")
    void testGetProductsInCart() throws Exception {
        mockMvc.perform(get("/api/product/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("장바구니 조회"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].quantity").value(2));
    }

    @Test
    @DisplayName("정상적인 비어있는 장바구니 목록 조회")
    @WithMockUser(username = "test", roles = "USER")
    void testGetProductsInCart_EmptyCartList() throws Exception {
        cartRepository.deleteAll();
        mockMvc.perform(get("/api/product/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("장바구니가 비어 있습니다."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("정상적인 장바구니 상품 수량 조회")
    @WithMockUser(username = "test", roles = "USER")
    void testGetQuantityOfProductInCart() throws Exception {
        mockMvc.perform(get("/api/product/cart/" + product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("장바구니 상품 수량 조회"))
                .andExpect(jsonPath("$.data").value(2));
    }

    @Test
    @DisplayName("정상적인 장바구니에 상품 담기 요청")
    @WithMockUser(username = "test", roles = "USER")
    void testToggleCartItem_set() throws Exception {
        Product testProduct = productRepository.save(new Product("브랜드3", "예쁜 신발", 14000, "img3.jpg"));
        mockMvc.perform(patch("/api/product/cart/" + testProduct.getId())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("장바구니 상품 담김 여부 변경"))
                .andExpect(jsonPath("$.data").value(1));
    }

    @Test
    @DisplayName("정상적인 장바구니에 상품 안 담기 요청")
    @WithMockUser(username = "test", roles = "USER")
    void testToggleCartItem_delete() throws Exception {
        mockMvc.perform(patch("/api/product/cart/" + product.getId())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("장바구니 상품 담김 여부 변경"))
                .andExpect(jsonPath("$.data").value(0));
    }

    @Test
    @DisplayName("정상적인 장바구니에 담긴 상품 수량 변경")
    @WithMockUser(username = "test", roles = "USER")
    void testUpsertCartItem_update() throws Exception {
        mockMvc.perform(patch("/api/product/cart/" + product.getId() + "/3")
                    .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("장바구니 상품 수량 변경 또는 저장"))
                .andExpect(jsonPath("$.data").value(3));
    }

    @Test
    @DisplayName("정상적인 장바구니에 담긴 상품 수량 저장")
    @WithMockUser(username = "test", roles = "USER")
    void testUpsertCartItem_set() throws Exception {
        Product testProduct = productRepository.save(new Product("브랜드3", "예쁜 신발", 14000, "img3.jpg"));
        mockMvc.perform(patch("/api/product/cart/" + testProduct.getId() + "/3")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("장바구니 상품 수량 변경 또는 저장"))
                .andExpect(jsonPath("$.data").value(3));
    }

    @Test
    @DisplayName("정상적인 장바구니 목록 삭제")
    @WithMockUser(username = "test", roles = "USER")
    void testDeleteProductsInCart() throws Exception {
        mockMvc.perform(delete("/api/product/cart")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("장바구니 삭제"));

        boolean existing = cartRepository.existsByMemberId(member.getId());
        assertFalse(existing);
    }

    @Test
    @DisplayName("정상적인 장바구니 상품 삭제")
    @WithMockUser(username = "test", roles = "USER")
    void testDeleteCartItem() throws Exception {
        mockMvc.perform(delete("/api/product/cart/" + product.getId())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("장바구니 상품 삭제"));

        CartId cartId = new CartId(member.getId(), product.getId());
        Cart deletedCart = cartRepository.findById(cartId).orElse(null);
        assertNull(deletedCart);
    }

    @Test
    @DisplayName("장바구니에 없는 상품을 장바구니에서 삭제")
    @WithMockUser(username = "test", roles = "USER")
    void testDeleteCartItem_fail() throws Exception {
        mockMvc.perform(delete("/api/product/cart/9999")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("장바구니 상품 삭제"));
    }
}

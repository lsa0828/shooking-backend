package com.example.shooking.service;

import com.example.shooking.dto.CartDTO;
import com.example.shooking.entity.*;
import com.example.shooking.repository.CartRepository;
import com.example.shooking.repository.MemberRepository;
import com.example.shooking.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {
    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private CartService cartService;

    private List<Cart> mockList;
    private Member member;
    private Product product;

    @BeforeEach
    void setup() throws Exception {
        Brand brand1 = new Brand(1L, "브랜드2");
        member = new Member(1L, "test", "1234", "테스트유저", LocalDate.of(2000, 1, 3), LocalDate.of(2025, 6, 24), "USER");
        product = new Product(2L, brand1, "멋진 신발", 15000, "img2.jpg");
        CartId id = new CartId(member.getId(), product.getId());
        mockList = List.of(
                new Cart(id, member, product, 2)
        );
    }

    @Test
    @DisplayName("정상적인 장바구니 목록 조회")
    void getProductsInCart_ShouldReturnCartList() throws Exception {
        Long memberId = member.getId();
        given(cartRepository.findByMemberId(1L)).willReturn(mockList);

        List<CartDTO> result = cartService.getProductsInCart(memberId);
        CartDTO cart = result.get(0);
        assertEquals(2L, cart.getId());
        assertEquals("브랜드2", cart.getBrand());
        assertEquals("멋진 신발", cart.getDescription());
        assertEquals(15000, cart.getPrice());
        assertEquals(2, cart.getQuantity());
    }

    @Test
    @DisplayName("정상적인 비어있는 장바구니 목록 조회")
    void getProductsInCart_ShouldReturnEmptyCartList() throws Exception {
        Long memberId = member.getId();
        List<Cart> emptyList = new ArrayList<>();
        given(cartRepository.findByMemberId(1L)).willReturn(emptyList);

        List<CartDTO> result = cartService.getProductsInCart(memberId);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("정상적인 장바구니에 담겨있는 상품 수량 조회")
    void getQuantityOfProductInCart_ShouldReturnQuantity() throws Exception {
        Long memberId = member.getId();
        Long productId = product.getId();
        given(cartRepository.findByMemberIdAndProductId(memberId, productId)).willReturn(mockList.get(0));

        CartDTO cartDTO = cartService.getQuantityOfProductInCart(memberId, productId);
        assertEquals(2L, cartDTO.getId());
        assertEquals("브랜드2", cartDTO.getBrand());
    }

    @Test
    @DisplayName("정상적인 장바구니에 담겨있지 않는 상품 수량 조회")
    void getQuantityOfProductInCart_ShouldReturn0() throws Exception {
        Long memberId = member.getId();
        Long productId = 3L;
        given(cartRepository.findByMemberIdAndProductId(memberId, productId)).willReturn(null);

        CartDTO cartDTO = cartService.getQuantityOfProductInCart(memberId, productId);
        assertNull(cartDTO);
    }

    @Test
    @DisplayName("정상적인 장바구니에서 상품 담김으로 변경")
    void toggleCartItem_ShouldReturnQuantity() throws Exception {
        Long memberId = member.getId();
        Brand brand1 = new Brand(1L, "브랜드1");
        Product testProduct = new Product(3L, brand1, "예쁜 신발", 14000, "img3.jpg");
        CartId id = new CartId(member.getId(), testProduct.getId());
        Cart cart = new Cart(id, member, testProduct, 1);
        Long productId = testProduct.getId();
        given(cartRepository.findByMemberIdAndProductId(memberId, productId)).willReturn(null);
        given(productRepository.findById(productId)).willReturn(Optional.of(testProduct));
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(cartRepository.save(cart)).willReturn(cart);

        CartDTO cartDTO = cartService.toggleCartItem(memberId, productId);
        assertEquals(1, cartDTO.getQuantity());
    }

    @Test
    @DisplayName("정상적인 장바구니에서 상품 안 담김으로 변경")
    void toggleCartItem_ShouldReturn0() throws Exception {
        Long memberId = member.getId();
        Long productId = product.getId();
        given(cartRepository.findByMemberIdAndProductId(memberId, productId)).willReturn(mockList.get(0));
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        CartDTO cartDTO = cartService.toggleCartItem(memberId, productId);
        assertNull(cartDTO);
    }

    @Test
    @DisplayName("존재하지 않는 상품 담김 여부 변경 시도")
    void toggleCartItem_WhenProductNotFound() throws Exception {
        Long memberId = member.getId();
        Long productId = 1L;
        given(cartRepository.findByMemberIdAndProductId(memberId, productId)).willReturn(null);
        given(productRepository.findById(productId)).willReturn(Optional.empty());
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> {
            cartService.toggleCartItem(memberId, productId);
        });
        assertThat(e.getMessage()).isEqualTo("상품이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("정상적인 장바구니에 담긴 상품 수량 저장")
    void upsertCartItem_ShouldReturnSavedQuantity() throws Exception {
        Long memberId = member.getId();
        Brand brand1 = new Brand(1L, "브랜드1");
        Product testProduct = new Product(3L, brand1, "예쁜 신발", 14000, "img3.jpg");
        CartId id = new CartId(member.getId(), testProduct.getId());
        Cart cart = new Cart(id, member, testProduct, 5);
        Long productId = testProduct.getId();
        CartId cartId = new CartId(memberId, productId);
        given(cartRepository.findById(cartId)).willReturn(Optional.empty());
        given(productRepository.findById(productId)).willReturn(Optional.of(testProduct));
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(cartRepository.save(cart)).willReturn(cart);

        CartDTO cartDTO = cartService.upsertCartItem(memberId, productId, 5);
        assertEquals(5, cartDTO.getQuantity());
    }

    @Test
    @DisplayName("정상적인 장바구니에 담긴 상품 수량 변경")
    void upsertCartItem_ShouldReturnChangedQuantity() throws Exception {
        Long memberId = member.getId();
        Long productId = product.getId();
        CartId cartId = new CartId(memberId, productId);
        Cart cart = new Cart(cartId, member, product, 5);
        given(cartRepository.findById(cartId)).willReturn(Optional.of(mockList.get(0)));
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(cartRepository.save(cart)).willReturn(cart);

        CartDTO cartDTO = cartService.upsertCartItem(memberId, productId, 5);
        assertEquals(5, cartDTO.getQuantity());
    }

    @Test
    @DisplayName("장바구니에 담긴 상품 수량 음수로 변경 시도")
    void upsertCartItem_ShouldReturn0() throws Exception {
        Long memberId = member.getId();
        Long productId = product.getId();
        CartId cartId = new CartId(memberId, productId);
        given(cartRepository.findById(cartId)).willReturn(Optional.of(mockList.get(0)));
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        
        CartDTO cartDTO = cartService.upsertCartItem(memberId, productId, -5);
        assertNull(cartDTO);
    }

    @Test
    @DisplayName("정상적인 장바구니 목록 삭제")
    void deleteProductsInCart_ShouldSucceed() throws Exception {
        Long memberId = member.getId();
        assertDoesNotThrow(() -> cartService.deleteProductsInCart(memberId));
        verify(cartRepository).deleteByMemberId(memberId);
    }

    @Test
    @DisplayName("정상적인 장바구니에서 상품 삭제")
    void deleteCartItem_ShouldSucceed() throws Exception {
        Long memberId = member.getId();
        Long productId = product.getId();
        assertDoesNotThrow(() -> cartService.deleteCartItem(memberId, productId));
        verify(cartRepository).deleteByMemberIdAndProductId(memberId, productId);
    }
}

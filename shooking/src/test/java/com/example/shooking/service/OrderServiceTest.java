package com.example.shooking.service;

import com.example.shooking.dto.OrderDTO;
import com.example.shooking.entity.*;
import com.example.shooking.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private OrderService orderService;

    private Member member;
    private Card card;
    private Long memberId;
    private Long cardId;

    @BeforeEach
    void setup() throws Exception {
        member = new Member(1L, "test", "1234", "테스트유저", LocalDate.of(2000, 1, 3), LocalDate.of(2025, 6, 24), "USER");
        card = new Card(2L, member, "0123456789012345", "0426", "tester", "012", "01");
        memberId = member.getId();
        cardId = card.getId();
    }

    @Test
    @DisplayName("정상적인 주문 내역 조회")
    void getOrderList_ShouldReturnOrderList() throws Exception {
        Product product = new Product(2L, "브랜드2", "멋진 신발", 15000, "img2.jpg");
        List<OrderSheet> orderList = List.of(
                new OrderSheet(3L, member, product, card, 2, LocalDate.now())
        );
        given(orderRepository.findByMemberId(memberId)).willReturn(orderList);

        List<OrderDTO> result = orderService.getOrderList(memberId);
        OrderDTO dto = result.get(0);
        assertEquals(2L, dto.getProductId());
        assertEquals("브랜드2", dto.getBrand());
        assertEquals("멋진 신발", dto.getDescription());
        assertEquals(15000, dto.getPrice());
        assertEquals(2, dto.getQuantity());
        assertEquals(LocalDate.now(), dto.getOrderedAt());
    }

    @Test
    @DisplayName("정상적인 상품 주문")
    void orderProduct_ShouldReturnOrderDTO() throws Exception {
        Product product = new Product(2L, "브랜드2", "멋진 신발", 15000, "img2.jpg");
        Long productId = product.getId();
        OrderSheet orderSheet = new OrderSheet(member, product, card, 2);
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(cardRepository.findById(cardId)).willReturn(Optional.of(card));
        given(orderRepository.save(orderSheet)).willReturn(orderSheet);

        OrderDTO result = orderService.orderProduct(memberId, productId, cardId,2);
        assertEquals(2L, result.getProductId());
        assertEquals("브랜드2", result.getBrand());
        assertEquals(2, result.getQuantity());
        assertEquals(LocalDate.now(), result.getOrderedAt());
    }

    @Test
    @DisplayName("존재하지 않는 상품 주문 시도")
    void orderProduct_WhenProductNotFound() throws Exception {
        Long productId = 5L;
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(productRepository.findById(productId)).willReturn(Optional.empty());

        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> {
            orderService.orderProduct(memberId, productId, cardId, 2);
        });
        assertThat(e.getMessage()).isEqualTo("상품이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("정상적인 장바구니 상품 목록 주문")
    void orderProductsInCart_ShouldReturnOrderList() throws Exception {
        Product product = new Product(2L, "브랜드2", "멋진 신발", 15000, "img2.jpg");
        Long productId = product.getId();
        Integer quantity = 2;
        CartId cartId = new CartId(memberId, productId);
        List<Cart> cartList = List.of(
                new Cart(cartId, member, product, quantity)
        );
        OrderSheet orderSheet = new OrderSheet(member, product, card, quantity);
        OrderSheet savedOrderSheet = new OrderSheet(3L, member, product, card, quantity, LocalDate.now());
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(cartRepository.findByMemberId(memberId)).willReturn(cartList);
        given(cardRepository.findById(cardId)).willReturn(Optional.of(card));
        given(orderRepository.save(orderSheet)).willReturn(savedOrderSheet);

        List<OrderDTO> result = orderService.orderProductsInCart(memberId, cardId);
        OrderDTO dto = result.get(0);
        assertEquals(2L, dto.getProductId());
        assertEquals("브랜드2", dto.getBrand());
        assertEquals(2, dto.getQuantity());
        assertEquals(LocalDate.now(), dto.getOrderedAt());
    }

    @Test
    @DisplayName("정상적인 비어있는 장바구니 상품 목록 주문")
    void orderProductsInCart_ReturnEmptyList() throws Exception {
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(cartRepository.findByMemberId(memberId)).willReturn(null);
        given(cardRepository.findById(cardId)).willReturn(Optional.of(card));

        List<OrderDTO> result = orderService.orderProductsInCart(memberId, cardId);
        assertThat(result).isEmpty();
    }
}

package com.example.shooking.service;

import com.example.shooking.dto.OrderDTO;
import com.example.shooking.entity.*;
import com.example.shooking.repository.*;
import com.example.shooking.util.AESUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final MemberRepository memberRepository;
    private final CardRepository cardRepository;
    private final AESUtil aesUtil;

    public List<OrderDTO> getOrderList(Long memberId) {
        List<OrderSheet> orderSheets = orderRepository.findByMemberId(memberId);
        return orderSheets.stream()
                .map(orderSheet -> {
                    OrderDTO dto = new OrderDTO(orderSheet);
                    String decryptedCardNumber = aesUtil.decrypt(dto.getCardNumber());
                    dto.setCardNumber(decryptedCardNumber.substring(0, 4));
                    return dto;
                })
                .sorted(Comparator.comparing(OrderDTO::getOrderedAt).reversed())
                .collect(Collectors.toList());
    }

    public OrderDTO orderProduct(Long memberId, Long productId, Long cardId, Integer quantity) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("카드가 존재하지 않습니다."));
        if (!card.getMember().equals(member)) {
            throw new IllegalArgumentException("해당 카드가 회원의 카드가 아닙니다.");
        }
        OrderSheet order = orderRepository.save(new OrderSheet(member, product, card, quantity));
        return new OrderDTO(order);
    }

    public List<OrderDTO> orderProductsInCart(Long memberId, Long cardId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("카드가 존재하지 않습니다."));
        if (!card.getMember().equals(member)) {
            throw new IllegalArgumentException("해당 카드가 회원의 카드가 아닙니다.");
        }
        List<Cart> cartList = cartRepository.findByMemberId(memberId);
        if (cartList == null || cartList.isEmpty()) {
            return new ArrayList<>();
        }

        List<OrderSheet> orderList = new ArrayList<>();
        for (Cart cart : cartList) {
            Product product = productRepository.findById(cart.getProduct().getId())
                    .orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));
            orderList.add(orderRepository.save(new OrderSheet(member, product, card, cart.getQuantity())));
            cartRepository.deleteById(cart.getId());
        }
        return orderList.stream().map(OrderDTO::new).collect(Collectors.toList());
    }
}

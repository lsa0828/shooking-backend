package com.example.shooking.service;

import com.example.shooking.dto.OrderDTO;
import com.example.shooking.entity.Cart;
import com.example.shooking.entity.Member;
import com.example.shooking.entity.OrderSheet;
import com.example.shooking.entity.Product;
import com.example.shooking.repository.CartRepository;
import com.example.shooking.repository.MemberRepository;
import com.example.shooking.repository.OrderRepository;
import com.example.shooking.repository.ProductRepository;
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

    public List<OrderDTO> getOrderList(Long memberId) {
        return orderRepository.findByMemberId(memberId).stream()
                .sorted(Comparator.comparing(OrderDTO::getOrderedAt).reversed())
                .collect(Collectors.toList());
    }

    public OrderDTO orderProduct(Long memberId, Long productId, Integer quantity) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));
        OrderSheet order = orderRepository.save(new OrderSheet(member, product, quantity));
        return new OrderDTO(order);
    }

    public List<OrderDTO> orderProductsInCart(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
        List<Cart> cartList = cartRepository.findByMemberId(memberId);
        if (cartList == null || cartList.isEmpty()) {
            return new ArrayList<>();
        }

        List<OrderSheet> orderList = new ArrayList<>();
        for (Cart cart : cartList) {
            Product product = productRepository.findById(cart.getProduct().getId())
                    .orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));
            orderList.add(orderRepository.save(new OrderSheet(member, product, cart.getQuantity())));
            cartRepository.deleteById(cart.getId());
        }
        return orderList.stream().map(OrderDTO::new).collect(Collectors.toList());
    }
}

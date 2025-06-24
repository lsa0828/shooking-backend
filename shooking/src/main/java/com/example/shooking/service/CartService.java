package com.example.shooking.service;

import com.example.shooking.dto.CartDTO;
import com.example.shooking.entity.Cart;
import com.example.shooking.entity.CartId;
import com.example.shooking.entity.Member;
import com.example.shooking.entity.Product;
import com.example.shooking.repository.CartRepository;
import com.example.shooking.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public List<CartDTO> getProductsInCart(Long memberId) {
        List<Cart> cartList = cartRepository.findByMemberId(memberId);
        return cartList.stream()
                .map(cart -> new CartDTO(
                        cart.getProduct().getId(),
                        cart.getProduct().getBrand(),
                        cart.getProduct().getDescription(),
                        cart.getProduct().getPrice(),
                        cart.getQuantity()
                ))
                .collect(Collectors.toList());
    }

    public Integer getQuantityOfProductInCart(Long memberId, Long productId) {
        Cart cart = cartRepository.findByMemberIdAndProductId(memberId, productId);
        return (cart != null) ? cart.getQuantity() : 0;
    }

    @Transactional
    public Integer toggleCartItem(Member member, Long productId) {
        Long memberId = member.getId();
        Cart cart = cartRepository.findByMemberIdAndProductId(memberId, productId);
        if (cart != null) {
            cartRepository.deleteByMemberIdAndProductId(memberId, productId);
            return 0;
        } else {
            Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));
            CartId cartId = new CartId(memberId, productId);
            Cart newCart = new Cart(member, product, 1);
            cartRepository.save(newCart);
            return 1;
        }
    }

    @Transactional
    public Integer upsertCartItem(Member member, Long productId, Integer quantity) {
        Long memberId = member.getId();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));
        CartId cartId = new CartId(memberId, productId);
        Optional<Cart> optionalCart = cartRepository.findById(cartId);

        if (quantity <= 0) {
            optionalCart.ifPresent(cartRepository::delete);
            return 0;
        }

        Cart cart;
        if (optionalCart.isPresent()) {
            cart = optionalCart.get();
            cart.setQuantity(quantity);
        } else {
            cart = new Cart(member, product, quantity);
        }
        cartRepository.save(cart);
        return quantity;
    }

    @Transactional
    public void deleteProductsInCart(Long memberId) {
        cartRepository.deleteByMemberId(memberId);
    }

    @Transactional
    public void deleteCartItem(Long memberId, Long productId) {
        cartRepository.deleteByMemberIdAndProductId(memberId, productId);
    }
}

package com.example.shooking.service;

import com.example.shooking.dto.CartDTO;
import com.example.shooking.entity.Cart;
import com.example.shooking.entity.CartId;
import com.example.shooking.entity.Member;
import com.example.shooking.entity.Product;
import com.example.shooking.repository.CartRepository;
import com.example.shooking.repository.MemberRepository;
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
    private final MemberRepository memberRepository;

    public List<CartDTO> getProductsInCart(Long memberId) {
        List<Cart> cartList = cartRepository.findByMemberId(memberId);
        return cartList.stream()
                .map(CartDTO::new)
                .collect(Collectors.toList());
    }

    public CartDTO getQuantityOfProductInCart(Long memberId, Long productId) {
        Cart cart = cartRepository.findByMemberIdAndProductId(memberId, productId);
        return (cart != null) ? new CartDTO(cart) : null;
    }

    @Transactional
    public CartDTO toggleCartItem(Long memberId, Long productId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
        Cart cart = cartRepository.findByMemberIdAndProductId(memberId, productId);
        if (cart != null) {
            cartRepository.deleteByMemberIdAndProductId(memberId, productId);
            return null;
        } else {
            Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));
            Cart savedCart = cartRepository.save(new Cart(member, product, 1));
            return new CartDTO(savedCart);
        }
    }

    @Transactional
    public CartDTO upsertCartItem(Long memberId, Long productId, Integer quantity) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));
        CartId cartId = new CartId(memberId, productId);
        Optional<Cart> optionalCart = cartRepository.findById(cartId);

        if (quantity <= 0) {
            optionalCart.ifPresent(cartRepository::delete);
            return null;
        }

        Cart cart;
        if (optionalCart.isPresent()) {
            cart = optionalCart.get();
            cart.setQuantity(quantity);
        } else {
            cart = new Cart(member, product, quantity);
        }
        Cart savedCart = cartRepository.save(cart);
        return new CartDTO(savedCart);
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

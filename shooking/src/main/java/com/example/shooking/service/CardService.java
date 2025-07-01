package com.example.shooking.service;

import com.example.shooking.dto.CardDTO;
import com.example.shooking.dto.CardResponse;
import com.example.shooking.entity.Card;
import com.example.shooking.entity.Member;
import com.example.shooking.repository.CardRepository;
import com.example.shooking.repository.MemberRepository;
import com.example.shooking.util.AESUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final MemberRepository memberRepository;
    private final AESUtil aesUtil;

    public List<CardResponse> getCardList(Long memberId) {
        List<Card> cardList = cardRepository.findByMemberId(memberId);
        return cardList.stream()
                .map(CardResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public CardResponse addCard(Long memberId, CardDTO cardDTO) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
        Card card = setEncryptedCard(new Card(member, cardDTO));
        Card savedCard = cardRepository.save(card);
        return new CardResponse(savedCard);
    }

    @Transactional
    public void deleteCard(Long memberId, Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("카드가 존재하지 않습니다."));
        if (!card.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("회원이 일치하지 않습니다.");
        }
        cardRepository.delete(card);
    }

    public Card setEncryptedCard(Card card) {
        card.setCardNumber(aesUtil.encrypt(card.getCardNumber()));
        card.setSecurityCode(aesUtil.encrypt(card.getSecurityCode()));
        card.setPassword(aesUtil.encrypt(card.getPassword()));
        return card;
    }
}

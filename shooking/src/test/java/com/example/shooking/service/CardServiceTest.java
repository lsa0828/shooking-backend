package com.example.shooking.service;

import com.example.shooking.dto.CardDTO;
import com.example.shooking.dto.CardResponse;
import com.example.shooking.entity.Card;
import com.example.shooking.entity.Member;
import com.example.shooking.repository.CardRepository;
import com.example.shooking.repository.MemberRepository;
import com.example.shooking.util.AESUtil;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {
    @Mock
    private CardRepository cardRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AESUtil aesUtil;

    @InjectMocks
    private CardService cardService;

    private Member member;
    private Long memberId;

    @BeforeEach
    void setup() throws Exception {
        member = new Member(1L, "test", "1234", "테스트유저", LocalDate.of(2000, 1, 3), LocalDate.of(2025, 6, 24), "USER");
        memberId = member.getId();
    }

    @Test
    @DisplayName("정상적인 카드 목록 조회")
    void getCardList_ShouldReturnCardList() throws Exception {
        List<Card> cardList = List.of(
                new Card(2L, member, "0123456789012345", "0426", "tester", "012", "01")
        );
        given(cardRepository.findByMemberId(memberId)).willReturn(cardList);

        List<CardResponse> result = cardService.getCardList(memberId);
        CardResponse card = result.get(0);
        assertEquals(2L, card.getId());
        assertEquals("0123456789012345", card.getCardNumber());
    }

    @Test
    @DisplayName("정상적인 카드 추가")
    void addCard_ShouldReturnAddedCard() throws Exception {
        CardDTO cardDTO = new CardDTO(2L, "0123456789012345", "0426", "tester", "012", "01");
        Card card = new Card(member, cardDTO);
        Card savedCard = new Card(2L, member, "0123456789012345", "0426", "tester", "012", "01");
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(cardRepository.save(card)).willReturn(savedCard);
        given(aesUtil.encrypt(card.getCardNumber())).willReturn("0123456789012345");
        given(aesUtil.encrypt(card.getSecurityCode())).willReturn("012");
        given(aesUtil.encrypt(card.getPassword())).willReturn("01");

        CardResponse result = cardService.addCard(memberId, cardDTO);
        assertEquals(2L, result.getId());
        assertEquals("0426", result.getExpirationDate());
    }

    @Test
    @DisplayName("정상적인 카드 삭제")
    void deleteCard_ShouldSucceed() throws Exception {
        Card card = new Card(2L, member, "0123456789012345", "0426", "tester", "012", "01");
        Long cardId = card.getId();
        given(cardRepository.findById(cardId)).willReturn(Optional.of(card));

        assertDoesNotThrow(() -> cardService.deleteCard(memberId, cardId));
        verify(cardRepository).delete(card);
    }

    @Test
    @DisplayName("존재하지 않는 카드 삭제")
    void deleteCard_WhenNotFoundCard() throws Exception {
        Long cardId = 10L;
        given(cardRepository.findById(cardId)).willReturn(Optional.empty());
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> {
            cardService.deleteCard(memberId, cardId);
        });
        assertThat(e.getMessage()).isEqualTo("카드가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("현재 회원이 보유하지 않은 카드 삭제")
    void deleteCard_WhenNotEqualMember() throws Exception {
        Member cardMember = new Member(2L, "test", "1234", "카드보유유저", LocalDate.of(2000, 1, 10), LocalDate.of(2025, 5, 24), "USER");
        Card card = new Card(3L, cardMember, "0123456789012345", "0426", "tester", "012", "01");
        Long cardId = card.getId();
        given(cardRepository.findById(cardId)).willReturn(Optional.of(card));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            cardService.deleteCard(memberId, cardId);
        });
        assertThat(e.getMessage()).isEqualTo("회원이 일치하지 않습니다.");
    }
}

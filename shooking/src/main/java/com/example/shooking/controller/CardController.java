package com.example.shooking.controller;

import com.example.shooking.dto.ApiResponse;
import com.example.shooking.dto.CardDTO;
import com.example.shooking.dto.CardResponse;
import com.example.shooking.entity.Member;
import com.example.shooking.security.CurrentMember;
import com.example.shooking.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/card")
public class CardController {
    private final CardService cardService;

    @GetMapping
    public ResponseEntity<?> showCardList(@CurrentMember Member member) {
        Long memberId = member.getId();
        List<CardResponse> cardList = cardService.getCardList(memberId);
        return ResponseEntity.ok(ApiResponse.success("카드 목록 조회", cardList));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCard(@CurrentMember Member member, @RequestBody @Valid CardDTO cardDTO) {
        Long memberId = member.getId();
        CardResponse card = cardService.addCard(memberId, cardDTO);
        return ResponseEntity.ok(ApiResponse.success("카드 추가", card));
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<?> deleteCard(@CurrentMember Member member, @PathVariable Long cardId) {
        Long memberId = member.getId();
        cardService.deleteCard(memberId, cardId);
        return ResponseEntity.ok(ApiResponse.success("카드 삭제"));
    }
}

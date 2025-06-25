package com.example.shooking.controller;

import com.example.shooking.dto.CardDTO;
import com.example.shooking.dto.CardResponse;
import com.example.shooking.entity.Member;
import com.example.shooking.security.CurrentMemberArgumentResolver;
import com.example.shooking.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardController.class)
public class CardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardService cardService;

    @MockitoBean
    private CurrentMemberArgumentResolver currentMemberArgumentResolver;

    private Member member;

    @BeforeEach
    void setup() throws Exception {
        member = new Member(1L, "test", "1234", "테스트유저", LocalDate.of(2000, 1, 3), LocalDate.of(2025, 6, 24), "USER");
        given(currentMemberArgumentResolver.supportsParameter(any())).willReturn(true);
        given(currentMemberArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(member);
    }

    @Test
    @DisplayName("정상적인 카드 목록 조회 API 요청")
    @WithMockUser(username = "test", roles = "USER")
    void showCardList_ShouldReturnCardList() throws Exception {
        Long memberId = member.getId();
        List<CardResponse> cardList = List.of(
                new CardResponse(1L, "0123456789012345", "0526", "tester")
        );
        given(cardService.getCardList(memberId)).willReturn(cardList);

        mockMvc.perform(get("/api/card"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("카드 목록 조회"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].cardNumber").value("0123456789012345"));
    }

    @Test
    @DisplayName("정상적인 카드 추가 API 요청")
    @WithMockUser(username = "test", roles = "USER")
    void addCard_ShouldReturnCard() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Long memberId = member.getId();
        CardDTO cardDTO = new CardDTO("0123456789012345", "0526", "tester", "012", "01");
        CardResponse card = new CardResponse(1L, "0123456789012345", "0526", "tester");
        given(cardService.addCard(memberId, cardDTO)).willReturn(card);

        mockMvc.perform(post("/api/card/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("카드 추가"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.cardholder").value("tester"));
    }

    @Test
    @DisplayName("정상적인 카드 삭제 API 요청")
    @WithMockUser(username = "test", roles = "USER")
    void deleteCard_ShouldSucceed() throws Exception {
        Long cardId = 1L;
        mockMvc.perform(delete("/api/card/" + cardId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("카드 삭제"));
    }
}

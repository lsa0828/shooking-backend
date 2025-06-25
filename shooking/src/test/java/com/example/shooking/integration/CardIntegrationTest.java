package com.example.shooking.integration;

import com.example.shooking.dto.CardDTO;
import com.example.shooking.entity.*;
import com.example.shooking.repository.CardRepository;
import com.example.shooking.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CardIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private MemberRepository memberRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Long memberId;
    private Long cardId;

    @BeforeEach
    void setup() throws Exception {
        Member member = memberRepository.save(new Member("test", "1234", "테스트유저", LocalDate.of(2000, 1, 3), LocalDate.of(2025, 6, 24), "USER"));
        Card card = cardRepository.save(new Card(member, "0123456789012345", "0426", "tester", "012", "01"));
        memberId = member.getId();
        cardId = card.getId();

        Authentication auth = new UsernamePasswordAuthenticationToken(member, null, List.of(new SimpleGrantedAuthority("USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("정상적인 카드 목록 조회")
    @WithMockUser(username = "test", roles = "USER")
    void testGetCardList() throws Exception {
        mockMvc.perform(get("/api/card"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("카드 목록 조회"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].expirationDate").value("0426"));
    }

    @Test
    @DisplayName("정상적인 비어있는 카드 목록 조회")
    @WithMockUser(username = "test", roles = "USER")
    void testGetCardList_EmptyCardList() throws Exception {
        cardRepository.deleteByMemberId(memberId);
        mockMvc.perform(get("/api/card"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("카드 목록 조회"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("정상적인 카드 추가")
    @WithMockUser(username = "test", roles = "USER")
    void testAddCard() throws Exception {
        CardDTO cardDTO = new CardDTO("0123456789012345", "0526", "tester", "012", "01");
        mockMvc.perform(post("/api/card/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("카드 추가"))
                .andExpect(jsonPath("$.data.cardNumber").value("0123456789012345"))
                .andExpect(jsonPath("$.data.cardholder").value("tester"));
    }

    @Test
    @DisplayName("카드번호가 비어있는 카드 추가")
    @WithMockUser(username = "test", roles = "USER")
    void testAddCard_WhenEmptyCardNumber() throws Exception {
        CardDTO cardDTO = new CardDTO();
        cardDTO.setExpirationDate("0526");
        cardDTO.setCardholder("tester");
        cardDTO.setSecurityCode("012");
        cardDTO.setPassword("01");
        mockMvc.perform(post("/api/card/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("필수 값이 누락되었습니다."));
    }

    @Test
    @DisplayName("정상적인 카드 삭제")
    @WithMockUser(username = "test", roles = "USER")
    void testDeleteCard() throws Exception {
        mockMvc.perform(delete("/api/card/" + cardId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("카드 삭제"));

        boolean existing = cardRepository.existsById(cardId);
        assertFalse(existing);
    }

    @Test
    @DisplayName("존재하지 않는 카드 삭제")
    @WithMockUser(username = "test", roles = "USER")
    void testDeleteCard_WhenNotFoundCard() throws Exception {
        mockMvc.perform(delete("/api/card/99999")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("카드가 존재하지 않습니다."));
    }
}

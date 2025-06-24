package com.example.shooking.integration;

import com.example.shooking.entity.Cart;
import com.example.shooking.entity.CartId;
import com.example.shooking.entity.Member;
import com.example.shooking.entity.Product;
import com.example.shooking.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MyIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setup() throws Exception {
        Member member = memberRepository.save(new Member("test", "1234", "테스트유저", LocalDate.of(2000, 1, 3), LocalDate.of(2025, 6, 24), "USER"));
        Authentication auth = new UsernamePasswordAuthenticationToken(member, null, List.of(new SimpleGrantedAuthority("USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("정상적인 내 정보 조회")
    @WithMockUser(username = "test", roles = "USER")
    void testGetMyInfo() throws Exception {
        mockMvc.perform(get("/api/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("내 정보 조회"))
                .andExpect(jsonPath("$.data.username").value("test"))
                .andExpect(jsonPath("$.data.nickname").value("테스트유저"));
    }

    @Test
    @DisplayName("로그아웃")
    @WithMockUser(username = "test", roles = "USER")
    void testLogin() throws Exception {
        mockMvc.perform(post("/api/me/logout")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그아웃"));
    }
}

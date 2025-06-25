package com.example.shooking.integration;

import com.example.shooking.dto.LoginRequest;
import com.example.shooking.dto.RegisterRequest;
import com.example.shooking.entity.Member;
import com.example.shooking.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MemberIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        memberRepository.deleteByUsername("test");
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    @DisplayName("정상적인 회원가입 요청")
    void register_ShouldSucceed_WhenValidRequest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("test");
        request.setPassword("1234");
        request.setNickname("테스트유저");
        request.setBirthDate(LocalDate.of(2000, 1, 2));

        mockMvc.perform(post("/api/member/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."))
                .andExpect(jsonPath("$.data.username").value("test"))
                .andExpect(jsonPath("$.data.nickname").value("테스트유저"))
                .andExpect(jsonPath("$.data.role").value("USER"));
    }

    @Test
    @DisplayName("중복인 username 회원가입 요청")
    void register_ShouldFail_WhenEmailAlreadyExists() throws Exception {
        Member member = new Member();
        member.setUsername("test");
        member.setPassword(passwordEncoder.encode("1234"));
        member.setNickname("기존유저");
        member.setBirthDate(LocalDate.of(1995, 5, 5));
        member.setRole("USER");
        memberRepository.save(member);

        RegisterRequest request = new RegisterRequest();
        request.setUsername("test");
        request.setPassword("5678");
        request.setNickname("중복유저");
        request.setBirthDate(LocalDate.of(2000, 1, 2));

        mockMvc.perform(post("/api/member/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 존재하는 이메일입니다."));
    }

    @Test
    @DisplayName("정상적인 로그인 요청")
    void login_ShouldSucceed_WithCorrectCredentials() throws Exception {
        Member member = new Member();
        member.setUsername("test");
        member.setPassword(passwordEncoder.encode("1234"));
        member.setNickname("로그인유저");
        member.setBirthDate(LocalDate.of(2000, 1, 2));
        member.setRole("USER");
        memberRepository.save(member);

        LoginRequest request = new LoginRequest();
        request.setUsername("test");
        request.setPassword("1234");

        mockMvc.perform(post("/api/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그인 성공"))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.username").value("test"))
                .andExpect(jsonPath("$.data.nickname").value("로그인유저"))
                .andExpect(jsonPath("$.data.role").value("USER"));
    }

    @Test
    @DisplayName("존재하지 않은 정보로 로그인 요청")
    void login_ShouldFail_WithInvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("test");
        request.setPassword("1234");

        mockMvc.perform(post("/api/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("이메일 또는 비밀번호가 잘못되었습니다."));
    }
}

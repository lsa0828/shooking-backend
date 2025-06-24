package com.example.shooking.controller;

import com.example.shooking.dto.MemberDTO;
import com.example.shooking.entity.Member;
import com.example.shooking.security.CurrentMemberArgumentResolver;
import com.example.shooking.service.MyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MyController.class)
public class MyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MyService myService;

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
    @DisplayName("정상적인 내 정보 조회 API 요청")
    @WithMockUser(username = "test", roles = "USER")
    void showMyInfo_ShouldReturnMemberDTO() throws Exception {
        MemberDTO dto = new MemberDTO(1L, "test", "테스트유저", LocalDate.of(2000, 1, 3), LocalDate.of(2025, 6, 24), "USER");
        given(myService.getMyInfo(member)).willReturn(dto);

        mockMvc.perform(get("/api/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("내 정보 조회"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.username").value("test"));
    }

    @Test
    @DisplayName("정상적인 로그아웃 API 요청")
    @WithMockUser(username = "test", roles = "USER")
    void logout_ShouldSucceed() throws Exception {
        mockMvc.perform(post("/api/me/logout")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그아웃"));
    }
}

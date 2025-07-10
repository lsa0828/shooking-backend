package com.example.shooking.service;

import com.example.shooking.dto.MemberDTO;
import com.example.shooking.entity.Member;
import com.example.shooking.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MyServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MyService myService;

    @Test
    @DisplayName("정상적인 내 정보 조회")
    void getMyInfo_ShouldReturnMemberDTO() throws Exception {
        Member member = new Member(1L, "test", "1234", "테스트유저", LocalDate.of(2000, 1, 3), LocalDate.of(2025, 6, 24), "USER");
        Long memberId = member.getId();
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        MemberDTO dto = myService.getMyInfo(memberId);
        Field[] fields = dto.getClass().getDeclaredFields();
        assertEquals(1L, member.getId());
        assertEquals("test", member.getUsername());
        assertEquals(6, fields.length);
    }
}

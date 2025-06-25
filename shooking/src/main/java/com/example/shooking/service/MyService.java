package com.example.shooking.service;

import com.example.shooking.dto.MemberDTO;
import com.example.shooking.entity.Member;
import com.example.shooking.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyService {
    private final MemberRepository memberRepository;
    public MemberDTO getMyInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
        return new MemberDTO(member);
    }
}

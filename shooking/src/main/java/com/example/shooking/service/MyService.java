package com.example.shooking.service;

import com.example.shooking.dto.MemberDTO;
import com.example.shooking.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyService {
    public MemberDTO getMyInfo(Member member) {
        return new MemberDTO(
                member.getId(),
                member.getUsername(),
                member.getNickname(),
                member.getBirthDate(),
                member.getJoinDate(),
                member.getRole()
        );
    }
}

package com.example.shooking.service;

import com.example.shooking.dto.MemberDTO;
import com.example.shooking.dto.RegisterRequest;
import com.example.shooking.entity.Member;
import com.example.shooking.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Member loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findOptionalByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public boolean existsByEmail(String username) {
        return memberRepository.existsByUsername(username);
    }

    public MemberDTO saveMember(RegisterRequest registerRequest) {
        Member member = new Member();
        member.setUsername(registerRequest.getUsername());
        member.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        member.setNickname(registerRequest.getNickname());
        member.setBirthDate(registerRequest.getBirthDate());
        member.setJoinDate(LocalDate.now());
        member.setRole("USER");
        Member registeredMember = memberRepository.save(member);
        return new MemberDTO(registeredMember);
    }
}

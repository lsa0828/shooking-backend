package com.example.shooking.service;

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

    public Member saveMember(Member member) {
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        member.setJoinDate(LocalDate.now());
        return memberRepository.save(member);
    }
}

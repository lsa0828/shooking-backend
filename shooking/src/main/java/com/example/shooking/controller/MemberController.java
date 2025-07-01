package com.example.shooking.controller;

import com.example.shooking.dto.*;
import com.example.shooking.entity.Member;
import com.example.shooking.security.JwtTokenProvider;
import com.example.shooking.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest registerRequest) {
        if (memberService.existsByEmail(registerRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("이미 존재하는 이메일입니다."));
        }
        MemberDTO dto = memberService.saveMember(registerRequest);
        return ResponseEntity.ok().body(ApiResponse.success("회원가입이 완료되었습니다.", dto));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            Member member = (Member) authentication.getPrincipal();
            String jwt = tokenProvider.generateToken(member);

            JwtResponse jwtResponse = new JwtResponse(jwt, member.getUsername(), member.getNickname(), member.getRole());

            return ResponseEntity.ok().body(ApiResponse.success("로그인 성공", jwtResponse));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("이메일 또는 비밀번호가 잘못되었습니다."));
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            if (tokenProvider.validateToken(token)) {
                String email = tokenProvider.getUsernameFromToken(token);
                Member member = (Member) memberService.loadUserByUsername(email);

                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("email", member.getUsername());
                userInfo.put("nickname", member.getNickname());
                userInfo.put("role", member.getRole());

                return ResponseEntity.ok().body(ApiResponse.success("유효한 토큰입니다.", userInfo));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("유효하지 않은 토큰입니다."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("토큰 검증 중 오류가 발생했습니다."));
        }
    }
}

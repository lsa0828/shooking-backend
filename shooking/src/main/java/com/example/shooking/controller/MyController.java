package com.example.shooking.controller;

import com.example.shooking.dto.ApiResponse;
import com.example.shooking.dto.MemberDTO;
import com.example.shooking.entity.Member;
import com.example.shooking.security.CurrentMember;
import com.example.shooking.service.MyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me")
public class MyController {
    private final MyService myService;

    @GetMapping
    public ResponseEntity<?> showMyInfo(@CurrentMember Member member) {
        MemberDTO dto = myService.getMyInfo(member);
        return ResponseEntity.ok(ApiResponse.success("내 정보 조회", dto));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().body(ApiResponse.success("로그아웃"));
    }
}

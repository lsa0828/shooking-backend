package com.example.shooking.dto;

import com.example.shooking.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private Long id;
    private String username;
    private String nickname;
    private LocalDate birthDate;
    private LocalDate joinDate;
    private String role;

    public MemberDTO(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        this.nickname = member.getNickname();
        this.birthDate = member.getBirthDate();
        this.joinDate = member.getJoinDate();
        this.role = member.getRole();
    }
}

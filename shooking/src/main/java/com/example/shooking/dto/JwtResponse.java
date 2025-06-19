package com.example.shooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String email;
    private String nickname;
    private String role;

    public JwtResponse(String token, String email, String nickname, String role) {
        this.token = token;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
    }
}

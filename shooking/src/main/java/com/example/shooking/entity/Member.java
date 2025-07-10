package com.example.shooking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Member implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq_gen")
    @SequenceGenerator(
            name = "member_seq_gen",
            sequenceName = "member_seq",
            allocationSize = 1
    )
    private Long id;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String nickname;
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;
    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate;
    @Column(nullable = false)
    private String role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(() -> role);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public Member(String username, String password, String nickname, LocalDate birthDate, LocalDate joinDate, String role) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.birthDate = birthDate;
        this.joinDate = joinDate;
        this.role = role;
    }
}

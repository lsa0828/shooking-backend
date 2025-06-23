package com.example.shooking.security;

import com.example.shooking.entity.Member;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final Key JWT_KEY;
    private final int JWT_EXPIRATION;

    public JwtTokenProvider(@Value("${jwt.key}") String jwtKey, @Value("${jwt.expiration}") int jwtExpiration) {
        this.JWT_KEY = Keys.hmacShaKeyFor(jwtKey.getBytes(StandardCharsets.UTF_8));
        this.JWT_EXPIRATION = jwtExpiration;
    }

    public String generateToken(Member member) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

        return Jwts.builder()
                .setSubject(member.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(JWT_KEY, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(JWT_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(JWT_KEY).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            System.err.println("Invalid JWT signature or token");
        } catch (ExpiredJwtException e) {
            System.err.println("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            System.err.println("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            System.err.println("JWT claims string is empty");
        }
        return false;
    }
}

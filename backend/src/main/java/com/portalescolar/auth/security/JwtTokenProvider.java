package com.portalescolar.auth.security;

import com.portalescolar.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-hours}")
    private int expirationHours;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        Date now = new Date();
         Date expirationDate = new Date(now.getTime() + expirationHours * 3600000L);
         return Jwts.builder()
                 .subject(user.getEmail())
                 .claim("role", user.getRole().name())
                 .claim("userId", user.getId().toString())
                 .issuedAt(now)
                 .expiration(expirationDate)
                 .signWith(getSigningKey())
                 .compact();
    }

    public String extractEmail(String token){
        return extractClaims(token).getSubject();
    }
    public String extractRole(String token){
        return extractClaims(token).get("role", String.class);
    }
    public LocalDateTime extractExpiration(String token){
        Date expiration = extractClaims(token).getExpiration();
        return expiration.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
    }
    public boolean isTokenValid(String token, UserDetails userDetails){
        String email = extractEmail(token);
        return email.equals(userDetails.getUsername()) && !isTokenExpired(token);    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).isBefore(LocalDateTime.now());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}

package com.github.birulazena.AuthenticationService.service;

import com.github.birulazena.AuthenticationService.entity.Role;
import com.github.birulazena.AuthenticationService.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${security.jwt.access-expiration}")
    private long jwtAccessExpiration;

    @Value("${security.jwt.refresh-expiration}")
    private long jwtRefreshExpiration;

    private SecretKey secretKey;

    @PostConstruct
    private void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String username, Long userId, Role role) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtAccessExpiration))
                .claim("type", "access")
                .claim("userId", userId)
                .claim("role", role)
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(String username, Long userId) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtRefreshExpiration))
                .claim("type", "refresh")
                .claim("userId", userId)
                .signWith(secretKey)
                .compact();
    }

    public String getUserFromToken(String token) {
        return parseToken(token)
                .getSubject();
    }

    public Long getUserIdFromToken(String token) {
        return parseToken(token)
                .get("userId", Long.class);
    }

    public String getRole(String token) {
        return parseToken(token)
                .get("role", String.class);
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        return parseToken(token)
                .get("type", String.class)
                .equals("refresh");
    }

    public String extractToken(String token) {
        if(token != null && token.startsWith("Bearer "))
            return token.substring(7);
        throw new InvalidTokenException("Token is invalid");
    }

    private Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception ex) {
            throw new InvalidTokenException(ex.getMessage())    ;
        }
    }
}

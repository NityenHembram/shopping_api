package com.ndroid.shopping.shopping_api.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class AuthUtils {

    @Value("${jwt.securitykey}")
    private String SECRET_KEY;

    @Value("${jwt.accesstoken.expirationtime}")
    private long Access_Exp;

    @Value("${jwt.refreshtoken.expirationtime}")
    private long Refresh_Exp;

    private SecretKey getSigningKey() {
        // Ensure the key is properly generated from the configured secret
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccesssToken(int userId) {
        return getToken(userId, "access");
    }

    public String generateRefreshToken(int userId) {
        return getToken(userId, "refresh");
    }

    public String getToken(int userId, String tokenType) {
        Date issuedAt = new Date();
        long expirationTimeMs;

        // Set expiration time based on token type
        if ("refresh".equals(tokenType)) {
            expirationTimeMs = Refresh_Exp;// Convert to milliseconds
        } else {
            expirationTimeMs = Access_Exp; // Convert to milliseconds
        }

        Date expiration = new Date(issuedAt.getTime() + expirationTimeMs);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("token_type", tokenType)
                .claim("user_id", userId)
                .claim("jti", UUID.randomUUID().toString().replace("-", ""))
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUserName(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public Integer extractUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("user_id", Integer.class);
    }

    public String extractTokenType(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("token_type", String.class);
    }

    public String extractJti(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("jti", String.class);
    }

    public Date extractExpiration(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration();
    }

    public Date extractIssuedAt(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getIssuedAt();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

}

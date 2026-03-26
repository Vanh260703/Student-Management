package com.example.quan_ly_sinh_vien_v2.Service.Auth;

import com.example.quan_ly_sinh_vien_v2.Exception.NotFoundException;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.User;
import com.example.quan_ly_sinh_vien_v2.Repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {
    public static final String SECRET_KEY = "GOxirTr5Fv9zsqlPgwAIRC6VBqji3OBuQJKySUJuDb3t8PMjjLKgyIDxdgJKI9o6";
    private static final long ACCESS_TOKEN_EXP = 1000 * 60 * 60 * 24; // 1 day
    private static final long REFRESH_TOKEN_EXP = 1000 * 60 * 60 * 24 * 7; // 7 days

    private final UserRepository userRepository;

    public JwtService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Create access token
    public String generateAccessToken(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("type", "access");

        return createToken(claims, email, ACCESS_TOKEN_EXP);
    }

    // Create refresh token
    public String generateRefreshToken(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("type", "refresh");

        return createToken(claims, email, REFRESH_TOKEN_EXP);
    }

    // Function create token
    public String createToken(Map<String, Object> claims, String email, long exp) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + exp))
                .signWith(getSignKey())
                .compact();
    }

    // Create sign key
    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    // Get email by access token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Get expires by access token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}


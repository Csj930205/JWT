package com.example.jwttokentest2.security.jwt;

import com.example.jwttokentest2.entity.Token;
import com.example.jwttokentest2.entity.User;
import com.example.jwttokentest2.service.UserService;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.time.Duration;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    private String secretKey = "secretKey-test-authorization-jwt-manage-token";
    private long accessTokenTime = Duration.ofMinutes(30).toMillis();
    private long refreshTokenTime = Duration.ofDays(1).toMillis();
    private final UserService userService;

    /**
     * 객체 초기화 및 secretKey Base64 인코딩
     */
    @PostConstruct
    public void init() {
        secretKey = Base64Utils.encodeToString(secretKey.getBytes());
    }

    /**
     * JWT Token 생성
     * @param user
     * @param tokenValidTime
     * @return
     */
    public Token createToken(User user, long tokenValidTime) {
        Claims claims = Jwts.claims().setSubject(user.getUserId());
        claims.put("role", user.getUserRole());
        Date now = new Date();

        String token =
                Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(now)
                    .setExpiration(new Date(now.getTime() + tokenValidTime))
                    .signWith(SignatureAlgorithm.HS256, secretKey)
                    .compact();

        return Token.builder()
                .key(user.getUserId())
                .value(token)
                .expiredTime(tokenValidTime)
                .build();
    }

    /**
     * access-Token 생성
     * @param user
     * @return
     */
    public Token accessTokenCreate(User user) {
        return createToken(user, accessTokenTime);
    }

    /**
     * Refresh-Token 생성
     * @param user
     * @return
     */
    public Token refreshTokenCreate(User user) {
        return createToken(user, refreshTokenTime);
    }

    /**
     * 토큰에서 인증 정보 조회
     * @param token
     * @return
     */
    public Authentication getAuthentication(String token) {
        User user = (User) userService.loadUserByUsername(this.getUserId(token));
        return new UsernamePasswordAuthenticationToken(user.getUserId(), "", user.getAuthorities());

    }

    /**
     * 토큰에서 회원 정보 추출
     * @param token
     * @return
     */
    public String getUserId(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * request의 헤더에서 토큰 추출
     * @param request
     * @return
     */
    public String resolveAccessToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    /**
     * 토큰 유효성 검사
     * @param token
     * @return
     */
    public boolean validationToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            e.printStackTrace();
            return false;
        } catch (UnsupportedJwtException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 응답 헤더에 토큰 전송
     * @param response
     * @param accessToken
     */
    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader("Authorization", accessToken);
    }

    /**
     * access-Token 유효시간 계산
     * @param token
     * @return
     */
    public Long getExpiration(String token) {
        Date expiration = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getExpiration();
        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }
}

package com.example.jwttokentest2.security.jwt;

import com.example.jwttokentest2.entity.Token;
import com.example.jwttokentest2.entity.User;
import com.example.jwttokentest2.repository.RedisRepository;
import com.example.jwttokentest2.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    private String secretKey = "secretKey-test-authorization-jwt-manage-token";
    private long accessTokenTime = 60 * 30 * 1000L; // 30분
    private long refreshTokenTime = 60 * 60 * 24 * 7 * 1000L; // 7일
    private final UserService userService;
    private final RedisRepository redisRepository;

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
        HashMap<String, String> payload = getPayloadByToken(token);
        User user = (User) userService.loadUserByUsername(payload.get("sub"));
        return new UsernamePasswordAuthenticationToken(user.getUserId(), "", user.getAuthorities());

    }

    /**
     * 토큰에서 회원 정보 조회
     * @param token
     * @return
     * @throws JsonProcessingException
     */
    public HashMap<String, String> getPayloadByToken(String token) {
        try {
            String[] splitJwt = token.split("\\.");
            String payload = new String(Base64Utils.decode(splitJwt[1].getBytes()));
            return new ObjectMapper().readValue(payload, HashMap.class);
        } catch (JsonProcessingException e) {
            return null;
        } catch (IllegalArgumentException e) {
            throw new JwtException("토큰이 정보가 유효하지 않습니다.");
        }
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
     * 토큰 유효성 검사(Access-Token)
     * @param token
     * @return
     */
    public boolean accessTokenValidationToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            throw new JwtException("Access-Token 기한이 만료되었습니다. 재발급 신청을 해주세요.");
        } catch (IllegalArgumentException e) {
            throw new JwtException("유효하지 않은 토큰입니다. 다시 확인해 주세요.");
        } catch (SignatureException e) {
            throw new JwtException("토큰값이 잘못되었습니다. 다시 확인해주세요.");
        }
    }

    /**
     * 토큰 유효성 검사(Refresh-Token)
     * @param token
     * @return
     */
    public boolean refreshTokenValidation(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            throw new JwtException("Refresh-Token 기한이 만료되었습니다. 재발급 신청을 해주세요");
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
     * refresh-Token 조회
     * @param userId
     * @return
     */
    public Token getRefreshToken(String userId) {
        Token refreshToken = redisRepository.findByKey(userId);
        return refreshToken;
    }

    /**
     * refresh-Tokne 만료시 레디스에서 삭제
     * @param userId
     */
    @Transactional
    public void deleteRefreshToken(String userId) {
        redisRepository.deleteById(userId);
    }
}

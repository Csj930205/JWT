package com.example.jwttokentest2.security.jwt;

import com.example.jwttokentest2.entity.Token;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtProvider.resolveAccessToken(request);
        String path = request.getServletPath();

        if (token != null) {
            String userId = jwtProvider.getPayloadByToken(token).get("sub");
            Token refreshToken = jwtProvider.getRefreshToken(userId);
            if (refreshToken != null && path.startsWith("/api/reissue/refresh")) {
                filterChain.doFilter(request, response);
            } else {
                if ( jwtProvider.refreshTokenValidation(refreshToken.getValue()) ) {
                    if (path.startsWith("/api/reissue/access")) {
                        filterChain.doFilter(request, response);
                    } else if ( token != null && jwtProvider.accessTokenValidationToken(token)) {
                        setAuthentication(token);
                        filterChain.doFilter(request, response);
                    }
                }
            }
        } else {
            filterChain.doFilter(request, response);
        }
}

    /**
     * SecurityContext 에 Authentication 저장
     * @param token
     */
    private void setAuthentication(String token) throws JsonProcessingException {
        Authentication authentication = jwtProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

package com.example.jwttokentest2.security.cofig;

import com.example.jwttokentest2.security.jwt.JwtAuthenticationFilter;
import com.example.jwttokentest2.security.jwt.JwtExceptionFilter;
import com.example.jwttokentest2.security.jwt.JwtProvider;
import com.example.jwttokentest2.service.UserService;
import com.example.jwttokentest2.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final JwtExceptionFilter jwtExceptionFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic().disable();

        http.csrf().disable();

        http.formLogin().disable();

        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/test/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/**").permitAll()
                );

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        http.addFilterBefore(jwtExceptionFilter,
                        JwtAuthenticationFilter.class);

        return http.build();
    }
}

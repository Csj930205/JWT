package com.example.jwttokentest2.service;

import com.example.jwttokentest2.entity.User;
import com.example.jwttokentest2.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userId = userRepository.findByUserId(username);

        if (userId != null) {
            return userId;
        } else {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
    }
}

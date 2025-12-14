package com.example.service.impl;

import com.example.repository.UserRepository;
import com.example.security.JwtUtil;
import com.example.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public Optional<String> login(String username, String password) {
        log.info("Login attempt username={}", username);

        return userRepository.findByUsername(username)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                .map(u -> {
                    log.info("Login success username={}", username);
                    return jwtUtil.generateToken(u.getUsername(), u.getRoles());
                });
    }
}

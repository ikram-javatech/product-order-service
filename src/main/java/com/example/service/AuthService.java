package com.example.service;

import java.util.Optional;

public interface AuthService {

    Optional<String> login(String username, String password);
}


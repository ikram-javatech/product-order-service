package com.example.controller;

import com.example.entity.AppUser;
import com.example.entity.Product;
import com.example.repository.ProductRepository;
import com.example.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class IntegrationTestSupport {

    @Autowired protected MockMvc mockMvc;
    @Autowired protected ObjectMapper objectMapper;
    @Autowired protected UserRepository userRepository;
    @Autowired protected ProductRepository productRepository;
    @Autowired protected PasswordEncoder passwordEncoder;

    protected Long productId;

    @BeforeEach
    void setUp() {
        ensureUser("user1", "password123", Set.of("USER"));
        ensureUser("premium1", "password123", Set.of("PREMIUM_USER"));
        ensureUser("admin1", "password123", Set.of("ADMIN"));

        Product p = new Product();
        p.setName("TEST_PRODUCT");
        p.setDescription("TEST_DESC");
        p.setPrice(new BigDecimal("100.00"));
        p.setQuantity(10);
        p.setAvailable(true);

        productId = productRepository.save(p).getId();
    }

    protected void ensureUser(String username, String password, Set<String> roles) {
        userRepository.findByUsername(username).orElseGet(() -> {
            AppUser u = new AppUser();
            u.setUsername(username);
            u.setPassword(passwordEncoder.encode(password));
            u.setRoles(roles);
            return userRepository.save(u);
        });
    }

    protected String loginAndGetToken(String username) throws Exception {
        String loginJson = """
                {
                  "username": "%s",
                  "password": "password123"
                }
                """.formatted(username);

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(response);
        return node.has("token") ? node.get("token").asText() : node.get("accessToken").asText();
    }

}

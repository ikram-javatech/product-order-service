package com.example.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityInvalidJwtIT extends IntegrationTestSupport {

    @Test
    void invalidJwt_randomString_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer this-is-not-a-jwt"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidJwt_malformed_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer abc.def"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidJwt_tamperedSignature_shouldReturn401() throws Exception {
        //  JWT but invalid signature
        String fakeJwt =
                "eyJhbGciOiJIUzI1NiJ9." +
                        "eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTYwMDAwMDAwMH0." +
                        "invalid-signature";

        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + fakeJwt))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void expiredJwt_shouldReturn401() throws Exception {
        // expired token signed with correct secret but old exp
        String expiredJwt =
                "eyJhbGciOiJIUzI1NiJ9." +
                        "eyJzdWIiOiJ1c2VyMSIsImV4cCI6MTU5MDAwMDAwMH0." +
                        "X7m6t7Qq7G6yJ0y9kGvRkZy1Qq5sR8gQ9Z3s9ZQ0JzU";

        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + expiredJwt))
                .andExpect(status().isUnauthorized());
    }
}

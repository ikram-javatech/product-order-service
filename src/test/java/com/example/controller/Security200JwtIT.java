package com.example.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class Security200JwtIT extends IntegrationTestSupport {

    @Test
    void getProducts_USER_200() throws Exception {
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + loginAndGetToken("user1")))
                .andExpect(status().isOk());
    }

    @Test
    void getProducts_PREMIUM_200() throws Exception {
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + loginAndGetToken("premium1")))
                .andExpect(status().isOk());
    }

    @Test
    void getProducts_ADMIN_200() throws Exception {
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + loginAndGetToken("admin1")))
                .andExpect(status().isOk());
    }

    @Test
    void getProductById_USER_200() throws Exception {
        mockMvc.perform(get("/api/products/{id}", productId)
                        .header("Authorization", "Bearer " + loginAndGetToken("user1")))
                .andExpect(status().isOk());
    }

    @Test
    void getProductById_PREMIUM_200() throws Exception {
        mockMvc.perform(get("/api/products/{id}", productId)
                        .header("Authorization", "Bearer " + loginAndGetToken("premium1")))
                .andExpect(status().isOk());
    }

    @Test
    void getProductById_ADMIN_200() throws Exception {
        mockMvc.perform(get("/api/products/{id}", productId)
                        .header("Authorization", "Bearer " + loginAndGetToken("admin1")))
                .andExpect(status().isOk());
    }

    @Test
    void createProduct_ADMIN_200() throws Exception {
        String body = """
                {
                  "name": "NEW",
                  "description": "DESC",
                  "price": 50,
                  "quantity": 5,
                  "available": true
                }
                """;

        mockMvc.perform(post("/api/products/create")
                        .header("Authorization", "Bearer " + loginAndGetToken("admin1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void createProduct_USER_403() throws Exception {
        mockMvc.perform(post("/api/products/create")
                        .header("Authorization", "Bearer " + loginAndGetToken("user1")))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateProduct_ADMIN_200() throws Exception {
        String body = """
                {
                  "name": "UPDATED",
                  "description": "UPDATED_DESC",
                  "price": 120,
                  "quantity": 20,
                  "available": true
                }
                """;

        mockMvc.perform(put("/api/products/{id}", productId)
                        .header("Authorization", "Bearer " + loginAndGetToken("admin1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void deleteProduct_ADMIN_200() throws Exception {
        mockMvc.perform(delete("/api/products/{id}", productId)
                        .header("Authorization", "Bearer " + loginAndGetToken("admin1")))
                .andExpect(status().isOk());
    }

    @Test
    void placeOrder_USER_200() throws Exception {
        String body = """
                {
                  "items": [{ "productId": %d, "quantity": 1 }]
                }
                """.formatted(productId);

        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + loginAndGetToken("user1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void placeOrder_ADMIN_403() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + loginAndGetToken("admin1")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getOrders_ADMIN_200() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .header("Authorization", "Bearer " + loginAndGetToken("admin1")))
                .andExpect(status().isOk());
    }

    @Test
    void getOrders_USER_403() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .header("Authorization", "Bearer " + loginAndGetToken("user1")))
                .andExpect(status().isForbidden());
    }
}

package com.example.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class Security403IT extends IntegrationTestSupport {

    @Test
    void products_list_noToken_403() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isForbidden());
    }

    @Test
    void products_getById_noToken_403() throws Exception {
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void products_create_noToken_403() throws Exception {
        mockMvc.perform(post("/api/products/create"))
                .andExpect(status().isForbidden());
    }

    @Test
    void products_update_noToken_403() throws Exception {
        mockMvc.perform(put("/api/products/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void products_delete_noToken_403() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void orders_place_noToken_403() throws Exception {
        mockMvc.perform(post("/api/orders"))
                .andExpect(status().isForbidden());
    }

    @Test
    void orders_getById_noToken_403() throws Exception {
        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void orders_getAll_noToken_403() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isForbidden());
    }
}

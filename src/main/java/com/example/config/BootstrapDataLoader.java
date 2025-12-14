package com.example.config;

import com.example.entity.AppUser;
import com.example.entity.Product;
import com.example.repository.ProductRepository;
import com.example.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Profile("!test")
@RequiredArgsConstructor
public class BootstrapDataLoader {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        loadUsers();
        loadProducts();
    }

    private void loadUsers() {
        try (InputStream is = new ClassPathResource("users.json").getInputStream()) {
            List<UserSeed> users = objectMapper.readValue(is, new TypeReference<>() {
            });

            for (UserSeed seed : users) {
                userRepository.findByUsername(seed.username())
                        .orElseGet(() -> {
                            AppUser u = new AppUser();
                            u.setUsername(seed.username());
                            // PASSWORD IS ALREADY BCRYPT-HASHED
                            u.setPassword(seed.password());
                            u.setRoles(Set.copyOf(seed.roles()));
                            log.info("Bootstrapped user={}", u.getUsername());
                            return userRepository.save(u);
                        });
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load users.json", e);
        }
    }

    private void loadProducts() {
        if (productRepository.count() > 0)
            return;

        try (InputStream is = new ClassPathResource("products.json").getInputStream()) {
            List<ProductSeed> products = objectMapper.readValue(is, new TypeReference<>() {
            });

            for (ProductSeed s : products) {
                Product p = new Product();
                p.setName(s.name());
                p.setDescription(s.description());
                p.setPrice(new BigDecimal(s.price()));
                p.setQuantity(s.quantity());
                p.setAvailable(s.available());
                productRepository.save(p);
            }
            log.info("Bootstrapped {} products", products.size());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load products.json", e);
        }
    }

    private record UserSeed(String username, String password, List<String> roles) {

    }

    private record ProductSeed(String name, String description, String price, int quantity, boolean available) {

    }
}

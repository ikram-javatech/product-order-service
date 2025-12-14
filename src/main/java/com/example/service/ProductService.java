package com.example.service;

import com.example.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;

public interface ProductService {

    Product create(Product p);

    Optional<Product> get(Long id);

    Product update(Long id, Product p);

    void softDelete(Long id);

    Page<Product> search(String name, BigDecimal minPrice, BigDecimal maxPrice, Boolean available, Pageable pageable);
}


package com.example.service.impl;

import com.example.entity.Product;
import com.example.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
public class ProductServiceImpl implements com.example.service.ProductService {

    private final ProductRepository repo;

    public ProductServiceImpl(ProductRepository repo) {this.repo = repo;}

    public Product create(Product p) {
        log.info("Creating product name={}", p == null ? null : p.getName());
        return repo.save(p);
    }

    public Optional<Product> get(Long id) {return repo.findById(id).filter(q -> !q.isDeleted());}

    public Page<Product> search(String name, BigDecimal min, BigDecimal max, Boolean available, Pageable pageable) {
        return repo.search(name, min, max, available, pageable);
    }

    public Product update(Long id, Product p) {
        Product ex = repo.findById(id).orElseThrow(() -> new RuntimeException("product.notfound"));
        ex.setName(p.getName());
        ex.setDescription(p.getDescription());
        ex.setPrice(p.getPrice());
        ex.setQuantity(p.getQuantity());
        return repo.save(ex);
    }

    public void softDelete(Long id) {
        repo.findById(id).ifPresent(pr -> {
            pr.setDeleted(true);
            repo.save(pr);
        });
    }
}

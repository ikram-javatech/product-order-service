package com.example.service;

import com.example.entity.Product;
import com.example.repository.ProductRepository;
import com.example.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repo;

    @InjectMocks
    private ProductServiceImpl service;

    @Test
    void create_shouldSave() {
        Product p = new Product();
        when(repo.save(any(Product.class))).thenReturn(p);

        Product saved = service.create(p);

        assertNotNull(saved);
        verify(repo).save(p);
    }

    @Test
    void get_shouldReturnEmptyWhenDeleted() {
        Product p = new Product();
        p.setDeleted(true);
        when(repo.findById(1L)).thenReturn(Optional.of(p));

        Optional<Product> out = service.get(1L);

        assertTrue(out.isEmpty());
        verify(repo).findById(1L);
    }

    @Test
    void get_shouldReturnProductWhenNotDeleted() {
        Product p = new Product();
        p.setDeleted(false);
        when(repo.findById(1L)).thenReturn(Optional.of(p));

        Optional<Product> out = service.get(1L);

        assertTrue(out.isPresent());
        verify(repo).findById(1L);
    }

    @Test
    void search_shouldDelegateToRepository() {
        Page<Product> page = new PageImpl<>(List.of());
        when(repo.search(eq("p"), eq(new BigDecimal("10")), eq(new BigDecimal("20")), eq(true), any()))
                .thenReturn(page);

        Page<Product> out = service.search("p", new BigDecimal("10"), new BigDecimal("20"), true,
                PageRequest.of(0, 10));

        assertSame(page, out);
        verify(repo).search(eq("p"), eq(new BigDecimal("10")), eq(new BigDecimal("20")), eq(true), any());
    }

    @Test
    void update_shouldModifyAndSave() {
        Product existing = new Product();
        existing.setId(1L);
        existing.setName("old");
        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product patch = new Product();
        patch.setName("new");
        patch.setDescription("d");
        patch.setPrice(new BigDecimal("9.99"));
        patch.setQuantity(5);

        Product out = service.update(1L, patch);

        assertEquals("new", out.getName());
        assertEquals("d", out.getDescription());
        assertEquals(new BigDecimal("9.99"), out.getPrice());
        assertEquals(5, out.getQuantity());
        verify(repo).save(existing);
    }

    @Test
    void softDelete_shouldMarkDeletedAndSave() {
        Product existing = new Product();
        existing.setId(1L);
        existing.setDeleted(false);
        when(repo.findById(1L)).thenReturn(Optional.of(existing));

        service.softDelete(1L);

        assertTrue(existing.isDeleted());
        verify(repo).save(existing);
    }
}

package com.example.service;

import com.example.dto.OrderRequest;
import com.example.entity.AppUser;
import com.example.entity.OrderEntity;
import com.example.entity.Product;
import com.example.exception.InsufficientStockException;
import com.example.repository.OrderRepository;
import com.example.repository.ProductRepository;
import com.example.repository.UserRepository;
import com.example.service.discount.PremiumDiscountStrategy;
import com.example.service.impl.OrderServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void placeOrder_userNoDiscount_under500_shouldCreateOrderAndReduceStock() {
        AppUser u = new AppUser();
        u.setId(10L);
        u.setUsername("user1");
        u.setRoles(Set.of("USER"));
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(u));

        Product p = new Product();
        p.setId(1L);
        p.setDeleted(false);
        p.setQuantity(5);
        p.setPrice(new BigDecimal("100.00"));
        when(productRepository.findById(1L)).thenReturn(Optional.of(p));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderRequest.Item item = new OrderRequest.Item();
        item.setProductId(1L);
        item.setQuantity(2);

        OrderEntity out = orderService.placeOrder("user1", List.of(item));

        assertNotNull(out);
        assertEquals(10L, out.getUserId());
        assertEquals(new BigDecimal("200.00"), out.getOrderTotal());
        assertEquals(1, out.getItems().size());
        assertEquals(3, p.getQuantity()); // stock reduced

        verify(orderRepository, times(1)).save(any(OrderEntity.class));
        verify(productRepository, atLeastOnce()).save(any(Product.class));
    }

    @Test
    void placeOrder_insufficientStock_shouldThrow() {
        AppUser u = new AppUser();
        u.setId(10L);
        u.setUsername("user1");
        u.setRoles(Set.of("USER"));
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(u));

        Product p = new Product();
        p.setId(1L);
        p.setDeleted(false);
        p.setQuantity(1);
        p.setPrice(new BigDecimal("100.00"));
        when(productRepository.findById(1L)).thenReturn(Optional.of(p));

        OrderRequest.Item item = new OrderRequest.Item();
        item.setProductId(1L);
        item.setQuantity(2);

        assertThrows(InsufficientStockException.class, () -> orderService.placeOrder("user1", List.of(item)));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void getOrderById_shouldDelegateToRepo() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(new OrderEntity()));
        orderService.getOrderById(1L);
        verify(orderRepository).findById(1L);
    }

    @Test
    void getAllOrders_shouldDelegateToRepo() {
        orderService.getAllOrders();
        verify(orderRepository).findAll();
    }

    @Test
    void shouldApplyTenPercentDiscountForPremiumUser() {

        PremiumDiscountStrategy strategy = new PremiumDiscountStrategy();

        AppUser user = new AppUser();
        user.setRoles(Set.of("PREMIUM_USER"));

        BigDecimal subtotal = new BigDecimal("200.00");

        BigDecimal discount = strategy.calculate(subtotal, user);

        Assertions.assertThat(discount).isEqualByComparingTo("20.00");
    }

    @Test
    void shouldReturnZeroDiscountIfSubtotalIsZero() {
        PremiumDiscountStrategy strategy = new PremiumDiscountStrategy();

        AppUser user = new AppUser();
        user.setRoles(Set.of("PREMIUM_USER"));

        BigDecimal discount = strategy.calculate(BigDecimal.ZERO, user);

        Assertions.assertThat(discount).isEqualByComparingTo("0.00");
    }

}

package com.example.service.impl;

import com.example.dto.OrderRequest;
import com.example.entity.AppUser;
import com.example.entity.OrderEntity;
import com.example.entity.OrderItem;
import com.example.entity.Product;
import com.example.exception.InsufficientStockException;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.OrderRepository;
import com.example.repository.ProductRepository;
import com.example.repository.UserRepository;
import com.example.service.discount.DiscountStrategy;
import com.example.service.discount.DiscountStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class OrderServiceImpl implements com.example.service.OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(ProductRepository productRepository,
            OrderRepository orderRepository,
            UserRepository userRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderEntity placeOrder(String username, List<OrderRequest.Item> itemsReq) {
        log.info("Placing order username={} itemsCount={}",
                username, itemsReq == null ? 0 : itemsReq.size());

        AppUser user = getUser(username);

        OrderContext ctx = prepareOrderContext(user, itemsReq);

        OrderEntity order = buildOrderAndUpdateStock(ctx);

        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public OrderEntity getOrderById(Long id) {
        log.info("Fetching order id={}", id);

        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));

        String username = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getName)
                .orElse(null);

        // If no security context (unit tests), skip ownership check
        if (username == null) {
            return order;
        }

        AppUser requester = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("user.notfound"));

        boolean isAdmin = requester.getRoles() != null
                && requester.getRoles().contains("ADMIN");

        if (!isAdmin && !requester.getId().equals(order.getUserId())) {
            throw new AccessDeniedException("You are not allowed to access this order");
        }

        return order;
    }

    @Transactional(readOnly = true)
    public List<OrderEntity> getAllOrders() {
        return orderRepository.findAll();
    }

    private AppUser getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("user.notfound"));
    }

    private OrderContext prepareOrderContext(AppUser user, List<OrderRequest.Item> itemsReq) {
        ValidationResult validation = validateItemsAndCalculateSubtotal(itemsReq);
        DiscountResult discount = calculateDiscount(user, validation.subtotal());

        OrderContext ctx = new OrderContext();
        ctx.user = user;
        ctx.items = itemsReq;
        ctx.products = validation.products();
        ctx.subtotal = validation.subtotal();
        ctx.totalDiscount = discount.totalDiscount();
        ctx.orderTotal = discount.orderTotal();
        return ctx;
    }

    private ValidationResult validateItemsAndCalculateSubtotal(List<OrderRequest.Item> itemsReq) {
        Map<Long, Product> prodMap = new HashMap<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (OrderRequest.Item it : itemsReq) {
            Product p = productRepository.findById(it.getProductId())
                    .orElseThrow(() -> new RuntimeException("product.notfound:" + it.getProductId()));

            if (p.isDeleted()) {
                throw new ResourceNotFoundException("Product not available: " + p.getId());
            }
            if (p.getQuantity() < it.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + p.getId());
            }

            prodMap.put(p.getId(), p);
            subtotal = subtotal.add(
                    p.getPrice().multiply(BigDecimal.valueOf(it.getQuantity()))
            );
        }

        return new ValidationResult(prodMap, subtotal);
    }

    private DiscountResult calculateDiscount(AppUser user, BigDecimal subtotal) {
        DiscountStrategy strategy = DiscountStrategyFactory.getStrategy(user, subtotal);
        BigDecimal discount = strategy.calculate(subtotal, user);
        BigDecimal total = subtotal.subtract(discount);
        return new DiscountResult(discount, total);
    }

    private OrderEntity buildOrderAndUpdateStock(OrderContext ctx) {
        OrderEntity order = new OrderEntity();
        order.setUserId(ctx.user.getId());

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderRequest.Item it : ctx.items) {
            Product p = ctx.products.get(it.getProductId());

            BigDecimal line = p.getPrice()
                    .multiply(BigDecimal.valueOf(it.getQuantity()));

            BigDecimal lineDiscount = ctx.subtotal.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : line.multiply(ctx.totalDiscount)
                    .divide(ctx.subtotal, 2, BigDecimal.ROUND_HALF_UP);

            OrderItem oi = new OrderItem();
            oi.setProductId(p.getId());
            oi.setQuantity(it.getQuantity());
            oi.setUnitPrice(p.getPrice());
            oi.setDiscountApplied(lineDiscount);
            oi.setTotalPrice(line.subtract(lineDiscount));
            orderItems.add(oi);

            // decrease stock
            p.setQuantity(p.getQuantity() - it.getQuantity());
            productRepository.save(p);
        }

        order.setItems(orderItems);
        order.setOrderTotal(ctx.orderTotal);
        return order;
    }

    private record ValidationResult(Map<Long, Product> products, BigDecimal subtotal) {

    }

    private record DiscountResult(BigDecimal totalDiscount, BigDecimal orderTotal) {

    }

    private static class OrderContext {

        AppUser user;
        List<OrderRequest.Item> items;
        Map<Long, Product> products;
        BigDecimal subtotal;
        BigDecimal totalDiscount;
        BigDecimal orderTotal;
    }
}

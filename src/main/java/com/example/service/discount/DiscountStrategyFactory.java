package com.example.service.discount;

import com.example.entity.AppUser;

import java.math.BigDecimal;

public class DiscountStrategyFactory {

    public static DiscountStrategy getStrategy(AppUser user, BigDecimal subtotal) {
        DiscountStrategy base = user.getRoles()
                .contains("PREMIUM_USER") ? new PremiumDiscountStrategy() : new NoDiscountStrategy();
        BigDecimal extra5 = BigDecimal.ZERO;
        if (subtotal.compareTo(new BigDecimal("500")) > 0)
            extra5 = subtotal.multiply(new BigDecimal("0.05"));
        final BigDecimal extra = extra5;
        final DiscountStrategy baseFinal = base;
        return (s, u) -> baseFinal.calculate(s, u).add(extra);
    }
}

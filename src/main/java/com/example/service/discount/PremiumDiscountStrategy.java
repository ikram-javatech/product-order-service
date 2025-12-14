package com.example.service.discount;

import com.example.entity.AppUser;

import java.math.BigDecimal;

public class PremiumDiscountStrategy implements DiscountStrategy {

    @Override
    public BigDecimal calculate(BigDecimal subtotal, AppUser user) {return subtotal.multiply(new BigDecimal("0.10"));}
}

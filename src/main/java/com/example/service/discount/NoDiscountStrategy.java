package com.example.service.discount;

import com.example.entity.AppUser;

import java.math.BigDecimal;

public class NoDiscountStrategy implements DiscountStrategy {

    @Override
    public BigDecimal calculate(BigDecimal subtotal, AppUser user) {return BigDecimal.ZERO;}
}

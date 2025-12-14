package com.example.service.discount;

import com.example.entity.AppUser;

import java.math.BigDecimal;

public interface DiscountStrategy {

    BigDecimal calculate(BigDecimal subtotal, AppUser user);
}

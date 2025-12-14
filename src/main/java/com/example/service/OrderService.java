package com.example.service;

import com.example.dto.OrderRequest;
import com.example.entity.OrderEntity;

import java.util.List;

public interface OrderService {

    OrderEntity placeOrder(String username, List<OrderRequest.Item> itemsReq);

    OrderEntity getOrderById(Long id);

    List<OrderEntity> getAllOrders();
}


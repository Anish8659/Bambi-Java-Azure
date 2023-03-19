package com.example.bambi.service;

import com.example.bambi.entity.Order;

import java.sql.Timestamp;
import java.util.List;

public interface OrderService {
    List<Order> getAllOrders();

    void updateOrderStatus(Long orderId, String orderStatus);

    List<Order> getPreviousOrdersByTimestamp(Timestamp startOfTime, Timestamp endOfTime);
}

package dev.abhiroopsantra.orderservice.service;

import dev.abhiroopsantra.orderservice.dto.OrderRequest;

public interface OrderService {
    void createOrder(OrderRequest orderRequest);
}

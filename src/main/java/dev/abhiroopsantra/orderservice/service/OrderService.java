package dev.abhiroopsantra.orderservice.service;

import dev.abhiroopsantra.orderservice.dto.ApiResponse;
import dev.abhiroopsantra.orderservice.dto.OrderRequest;

public interface OrderService {
    ApiResponse createOrder(OrderRequest orderRequest);
}

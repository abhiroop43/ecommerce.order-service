package dev.abhiroopsantra.orderservice.controller;

import dev.abhiroopsantra.orderservice.dto.ApiResponse;
import dev.abhiroopsantra.orderservice.dto.OrderRequest;
import dev.abhiroopsantra.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse> createOrder(@RequestBody OrderRequest orderRequest) {
        orderService.createOrder(orderRequest);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.errCode = "201";
        apiResponse.errMessage = "Order created successfully";
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }
}

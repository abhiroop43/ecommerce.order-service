package dev.abhiroopsantra.orderservice.controller;

import dev.abhiroopsantra.orderservice.dto.ApiResponse;
import dev.abhiroopsantra.orderservice.dto.OrderRequest;
import dev.abhiroopsantra.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackCreateOrder")
    @TimeLimiter(name = "inventory")
    @Retry(name = "inventory")
    public CompletableFuture<ApiResponse> createOrder(@RequestBody OrderRequest orderRequest) {
//        orderService.createOrder(orderRequest);
//        ApiResponse apiResponse = new ApiResponse();
//        apiResponse.errCode = "201";
//        apiResponse.errMessage = "Order created successfully";
//        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        return CompletableFuture.supplyAsync(() -> orderService.createOrder(orderRequest));
    }

    public CompletableFuture<ApiResponse> fallbackCreateOrder(OrderRequest orderRequest, RuntimeException runtimeException) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.errCode = "500";
        apiResponse.errMessage = "Can't check the stocks, please try again later.";
//        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        return CompletableFuture.supplyAsync(() -> apiResponse);
    }
}

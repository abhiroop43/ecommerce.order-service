package dev.abhiroopsantra.orderservice.service;

import dev.abhiroopsantra.orderservice.dto.*;
import dev.abhiroopsantra.orderservice.event.OrderPlacedEvent;
import dev.abhiroopsantra.orderservice.exception.BadRequestException;
import dev.abhiroopsantra.orderservice.model.Order;
import dev.abhiroopsantra.orderservice.model.OrderLineItem;
import dev.abhiroopsantra.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

@Service @RequiredArgsConstructor @Slf4j public class OrderServiceImpl implements OrderService {
    public final  KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
    private final ModelMapper                             modelMapper;
    private final OrderRepository                         orderRepository;
    private final WebClient.Builder                       webClientBuilder;
    private final ObjectMapper                            objectMapper;

    @Override public ApiResponse createOrder(OrderRequest orderRequest) {
        Order order = modelMapper.map(orderRequest, Order.class);

        List<OrderLineItem> orderLineItems = orderRequest.getOrderLineItems().stream()
                                                         .map(orderLineItemRequest -> mapToOrderLineItem(
                                                                 orderLineItemRequest, order)).toList();

        order.setOrderLineItems(orderLineItems);

        // call the inventory service to check if the items are available
        CheckOrderAvailabilityRequestDto checkOrderAvailabilityRequestDto = new CheckOrderAvailabilityRequestDto();
        checkOrderAvailabilityRequestDto.setItems(
                orderRequest.getOrderLineItems().stream().map(this::mapToAvailabilityItemsRequest).toList());

        ApiResponse response = webClientBuilder.build().post()
                                               .uri("http://inventory-service/api/v1/inventory/checkItemsAvailability")
                                               .bodyValue(checkOrderAvailabilityRequestDto).retrieve()
                                               .bodyToMono(ApiResponse.class).block();

        if (response == null) {
            throw new BadRequestException("Product is not in stock or Inventory Service is not reachable.");
        }

        Object data = response.data.get("isAvailable");

        if (!(data instanceof List<?> dataList)) {
            throw new ClassCastException("The object is not a List");
        }

        List<InventoryResponse> inventoryResponseList = dataList.stream().filter(item -> item instanceof LinkedHashMap)
                                                                .map(item -> objectMapper.convertValue(item,
                                                                                                       InventoryResponse.class
                                                                                                      )).toList();
        boolean allProductsInStock = inventoryResponseList.stream().allMatch(InventoryResponse::isInStock);

        if (!allProductsInStock) {
            throw new BadRequestException("Product is not in stock");
        }
        orderRepository.save(order);
        kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getId().toString()));
        log.info("Order created: {}", order.getId());

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.errCode    = "201";
        apiResponse.errMessage = "Order created successfully";

        return apiResponse;
    }

    private ItemsRequest mapToAvailabilityItemsRequest(OrderLineItemRequest orderLineItemRequest) {
        return modelMapper.map(orderLineItemRequest, ItemsRequest.class);
    }

    private OrderLineItem mapToOrderLineItem(OrderLineItemRequest orderLineItemRequest, Order order) {
        OrderLineItem orderLineItem = modelMapper.map(orderLineItemRequest, OrderLineItem.class);
        orderLineItem.setOrder(order);
        return orderLineItem;
    }
}

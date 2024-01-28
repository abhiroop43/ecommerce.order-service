package dev.abhiroopsantra.orderservice.service;

import dev.abhiroopsantra.orderservice.dto.*;
import dev.abhiroopsantra.orderservice.exception.BadRequestException;
import dev.abhiroopsantra.orderservice.model.Order;
import dev.abhiroopsantra.orderservice.model.OrderLineItem;
import dev.abhiroopsantra.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService{
    private final ModelMapper     modelMapper;
    private final OrderRepository orderRepository;
    private final WebClient      webClient;

    @Override public void createOrder(OrderRequest orderRequest) {
        Order order = modelMapper.map(orderRequest, Order.class);

        List<OrderLineItem> orderLineItems = orderRequest.getOrderLineItems().stream().map(
                orderLineItemRequest -> mapToOrderLineItem(orderLineItemRequest, order)).toList();

        order.setOrderLineItems(orderLineItems);

        // call the inventory service to check if the items are available
        CheckOrderAvailabilityRequestDto checkOrderAvailabilityRequestDto = new CheckOrderAvailabilityRequestDto();
        checkOrderAvailabilityRequestDto.setItems(orderRequest.getOrderLineItems().stream().map(this::mapToAvailabilityItemsRequest).toList());


        ApiResponse response = webClient.post()
                .uri("http://localhost:8082/api/v1/inventory/checkItemsAvailability")
                .bodyValue(checkOrderAvailabilityRequestDto)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .block();

        if((boolean) (response != null ? response.data.get("isAvailable") : false)) {
            orderRepository.save(order);
            log.info("Order created: {}", order.getId());
        } else {
            throw new BadRequestException("Product is not in stock");
        }
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

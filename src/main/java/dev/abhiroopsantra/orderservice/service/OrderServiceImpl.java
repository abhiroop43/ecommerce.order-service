package dev.abhiroopsantra.orderservice.service;

import dev.abhiroopsantra.orderservice.dto.OrderLineItemRequest;
import dev.abhiroopsantra.orderservice.dto.OrderRequest;
import dev.abhiroopsantra.orderservice.model.Order;
import dev.abhiroopsantra.orderservice.model.OrderLineItem;
import dev.abhiroopsantra.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService{
    private final ModelMapper     modelMapper;
    private final OrderRepository orderRepository;

    @Override public void createOrder(OrderRequest orderRequest) {
        Order order = modelMapper.map(orderRequest, Order.class);

        List<OrderLineItem> orderLineItems = orderRequest.getOrderLineItems().stream().map(
                orderLineItemRequest -> mapToOrderLineItem(orderLineItemRequest, order)).toList();

        order.setOrderLineItems(orderLineItems);

        orderRepository.save(order);
        log.info("Order created: {}", order.getId());
    }

    private OrderLineItem mapToOrderLineItem(OrderLineItemRequest orderLineItemRequest, Order order) {
        OrderLineItem orderLineItem = modelMapper.map(orderLineItemRequest, OrderLineItem.class);
        orderLineItem.setOrder(order);
        return orderLineItem;
    }
}

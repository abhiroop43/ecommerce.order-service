package dev.abhiroopsantra.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineItemRequest {
    private UUID id;

    private String     skuCode;
    private BigDecimal price;
    private Integer    quantity;
}

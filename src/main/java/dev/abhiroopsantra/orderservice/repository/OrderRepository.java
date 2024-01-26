package dev.abhiroopsantra.orderservice.repository;

import dev.abhiroopsantra.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}

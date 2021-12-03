package io.good.food.repository;

import io.good.food.dto.type.OrderStatusType;
import io.good.food.entity.Order;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface OrderRepository extends ReactiveMongoRepository<Order, String> {

    Flux<Order> findByCustomerId(final String customerId);

    Flux<Order> findByStatus(final OrderStatusType status);
}

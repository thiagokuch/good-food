package io.good.food.dto.type;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum OrderStatusType {

    CREATED,
    WAITING_PAYMENT_CONFIRMATION,
    PAID,
    WAITING_RESTAURANT_CONFIRMATION,
    IN_PREPARATION,
    WAITING_TO_DELIVER,
    DELIVERED,
    ERROR;

    private static final Map<String, OrderStatusType> CACHE = Stream.of(values())
            .collect(Collectors.toMap(OrderStatusType::toString, Function.identity()));

    @JsonCreator
    public static OrderStatusType safeValueOf(final String value) {
        return CACHE.getOrDefault(value, ERROR);
    }

}
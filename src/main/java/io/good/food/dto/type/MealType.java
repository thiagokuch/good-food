package io.good.food.dto.type;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum MealType {

    ASIAN,
    COLD_BEVERAGE,
    BRAZILIAN,
    DRINK,
    ITALIAN,
    DESSERT,
    ERROR;

    private static final Map<String, MealType> CACHE = Stream.of(values())
            .collect(Collectors.toMap(MealType::toString, Function.identity()));

    @JsonCreator
    public static MealType safeValueOf(final String value) {
        return CACHE.getOrDefault(value, ERROR);
    }

}
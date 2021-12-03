package io.good.food.dto.type;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ActionType {

    CREATE,
    UPDATE,
    DELETE;

    private static final Map<String, ActionType> CACHE = Stream.of(values())
            .collect(Collectors.toMap(ActionType::toString, Function.identity()));

    @JsonCreator
    public static ActionType safeValueOf(final String value) {
        return CACHE.get(value);
    }

}
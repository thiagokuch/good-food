package io.good.food.dto.request;

import io.good.food.dto.domain.MealDTO;
import io.good.food.dto.type.OrderStatusType;

import java.util.List;

public class OrderUpdateRequestDTO {

    private String id;

    private List<MealDTO> meals;

    private OrderStatusType status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<MealDTO> getMeals() {
        return meals;
    }

    public void setMeals(List<MealDTO> meals) {
        this.meals = meals;
    }

    public OrderStatusType getStatus() {
        return status;
    }

    public void setStatus(OrderStatusType status) {
        this.status = status;
    }
}

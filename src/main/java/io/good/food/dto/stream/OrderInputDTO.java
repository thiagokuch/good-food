package io.good.food.dto.stream;

import io.good.food.dto.domain.MealDTO;
import io.good.food.dto.type.ActionType;
import io.good.food.dto.type.OrderStatusType;

import java.time.LocalDateTime;
import java.util.List;

public class OrderInputDTO {

    private String id;

    private ActionType action;

    private LocalDateTime creationDate;

    private String customerId;

    private List<MealDTO> meals;

    private OrderStatusType status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
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
package io.good.food.dto.response;

import io.good.food.dto.domain.MealDTO;
import io.good.food.dto.type.OrderStatusType;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponseDTO {

    private String id;

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

package io.good.food.dto.request;

import io.good.food.dto.type.MealType;

public class MealUpdateRequestDTO {

    private String id;

    private String description;

    private String note;

    private MealType type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public MealType getType() {
        return type;
    }

    public void setType(MealType type) {
        this.type = type;
    }
}

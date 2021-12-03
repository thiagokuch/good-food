package io.good.food.controller;

import io.good.food.dto.request.MealInsertRequestDTO;
import io.good.food.dto.request.MealUpdateRequestDTO;
import io.good.food.dto.response.MealResponseDTO;
import io.good.food.service.MealService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("meals")
public class MealController {

    private final MealService mealService;

    public MealController(final MealService mealService) {
        this.mealService = mealService;
    }

    @GetMapping
    @Operation(summary = "Get all meals")
    public Flux<MealResponseDTO> findAll() {
        return this.mealService.findAll();
    }

    @GetMapping("{id}")
    @Operation(summary = "Get meal by id")
    public Mono<MealResponseDTO> findById(@PathVariable("id") final String id) {
        return this.mealService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new meal")
    public Mono<MealResponseDTO> create(@RequestBody final MealInsertRequestDTO dto) {
        return this.mealService.create(dto);
    }

    @PatchMapping
    @Operation(summary = "Update a new meal")
    public Mono<MealResponseDTO> update(@RequestBody final MealUpdateRequestDTO dto) {
        return this.mealService.update(dto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a meal")
    public Mono<Void> delete(@PathVariable("id") final String id) {
        return this.mealService.delete(id);
    }

}
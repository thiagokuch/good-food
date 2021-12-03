package io.good.food.controller;

import io.good.food.dto.request.OrderInsertRequestDTO;
import io.good.food.dto.request.OrderUpdateRequestDTO;
import io.good.food.dto.response.OrderResponseDTO;
import io.good.food.dto.type.OrderStatusType;
import io.good.food.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(final OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @Operation(summary = "Get all orders")
    public Flux<OrderResponseDTO> findAll() {
        return this.orderService.findAll();
    }

    @GetMapping("customers/{customerId}")
    @Operation(summary = "Get orders by customer id")
    public Flux<OrderResponseDTO> findByCustomerId(@PathVariable("customerId") final String customerId) {
        return this.orderService.findByCustomerId(customerId);
    }

    @GetMapping("status/{status}")
    @Operation(summary = "Get orders by status")
    public Flux<OrderResponseDTO> findByStatus(@PathVariable("status") final OrderStatusType status) {
        return this.orderService.findByStatus(status);
    }

    @GetMapping("{id}")
    @Operation(summary = "Get meal by id")
    public Mono<OrderResponseDTO> findById(@PathVariable("id") final String id) {
        return this.orderService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new order")
    public Mono<OrderResponseDTO> create(@RequestBody final OrderInsertRequestDTO dto) {
        return this.orderService.create(dto);
    }

    @PatchMapping
    @Operation(summary = "Update a new order")
    public Mono<OrderResponseDTO> update(@RequestBody final OrderUpdateRequestDTO dto) {
        return this.orderService.update(dto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an order")
    public Mono<Void> delete(@PathVariable("id") final String id) {
        return this.orderService.delete(id);
    }

}
package io.good.food.controller;

import io.good.food.dto.request.CustomerInsertRequestDTO;
import io.good.food.dto.request.CustomerUpdateRequestDTO;
import io.good.food.dto.response.CustomerResponseDTO;
import io.good.food.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(final CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("{suid}")
    @Operation(summary = "Get customer by suid")
    public Mono<CustomerResponseDTO> findBySuid(@PathVariable("suid") final String suid) {
        return this.customerService.findBySuid(suid);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new customer")
    public Mono<CustomerResponseDTO> create(@RequestBody final CustomerInsertRequestDTO dto) {
        return this.customerService.create(dto);
    }

    @PatchMapping
    @Operation(summary = "Update a new customer")
    public Mono<CustomerResponseDTO> update(@RequestBody final CustomerUpdateRequestDTO dto) {
        return this.customerService.update(dto);
    }

    @DeleteMapping("{suid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a customer")
    public Mono<Void> delete(@PathVariable("suid") final String suid) {
        return this.customerService.deleteBySuid(suid);
    }

}
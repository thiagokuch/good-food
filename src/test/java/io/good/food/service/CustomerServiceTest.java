package io.good.food.service;

import io.github.benas.randombeans.api.EnhancedRandom;
import io.good.food.Application;
import io.good.food.configuration.RandomBeanConfiguration;
import io.good.food.dto.request.CustomerInsertRequestDTO;
import io.good.food.dto.request.CustomerUpdateRequestDTO;
import io.good.food.entity.Customer;
import io.good.food.exception.BusinessException;
import io.good.food.repository.CustomerRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes={Application.class, RandomBeanConfiguration.class})
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CustomerServiceTest {

    private final CustomerService customerService;

    private final CustomerRepository customerRepository;

    private final EnhancedRandom enhancedRandom;

    @Autowired
    public CustomerServiceTest(final CustomerService customerService,
                               final CustomerRepository customerRepository,
                               final EnhancedRandom enhancedRandom) {
        this.customerService = customerService;
        this.customerRepository = customerRepository;
        this.enhancedRandom = enhancedRandom;
    }

    @BeforeEach
    protected void init() {
        this.customerRepository.deleteAll().block();
    }

    @Test
    void findById() {
        final var sample = this.createSample();

        final var mono = this.customerService.findById(sample.getId());

        StepVerifier.create(mono, StepVerifierOptions.create())
                .expectNextMatches(value -> {
                    assertNotNull(value);
                    assertEquals(sample.getCreationDate(), value.getCreationDate());
                    assertEquals(sample.getId(), value.getId());
                    assertEquals(sample.getName(), value.getName());
                    assertEquals(sample.getSuid(), value.getSuid());
                    assertEquals(sample.getSurname(), value.getSurname());

                    return true;
                })
                .verifyComplete();
    }

    @Test
    void findByIdEmptyRequest() {
        final var exception = assertThrows(BusinessException.class, () -> this.customerService.findById("").block());
        assertEquals("Id is required", exception.getMessage());
    }

    @Test
    void findByIdNotFound() {
        final var mono = this.customerService.findById("NOTFOUND");

        StepVerifier.create(mono)
                .expectErrorMatches(throwable -> {
                    assertNotNull(throwable);
                    assertEquals("Customer not found", throwable.getMessage());

                    return true;
                })
                .verify();
    }

    @Test
    void findBySuid() {
        final var sample = this.createSample();

        final var mono = this.customerService.findBySuid(sample.getSuid());

        StepVerifier.create(mono, StepVerifierOptions.create())
                .expectNextMatches(value -> {
                    assertNotNull(value);
                    assertEquals(sample.getCreationDate(), value.getCreationDate());
                    assertEquals(sample.getId(), value.getId());
                    assertEquals(sample.getName(), value.getName());
                    assertEquals(sample.getSuid(), value.getSuid());
                    assertEquals(sample.getSurname(), value.getSurname());

                    return true;
                })
                .verifyComplete();
    }

    @Test
    void findBySuidEmptyRequest() {
        final var exception = assertThrows(BusinessException.class, () -> this.customerService.findBySuid("").block());
        assertEquals("Suid is required", exception.getMessage());
    }

    @Test
    void findBySuidNotFound() {
        final var mono = this.customerService.findBySuid("NOTFOUND");

        StepVerifier.create(mono)
                .expectErrorMatches(throwable -> {
                    assertNotNull(throwable);
                    assertEquals("Customer not found", throwable.getMessage());

                    return true;
                })
                .verify();
    }

    @Test
    void create() {
        final var request = this.enhancedRandom.nextObject(CustomerInsertRequestDTO.class);

        final var mono = this.customerService.create(request);

        StepVerifier.create(mono, StepVerifierOptions.create())
                .expectNextMatches(value -> {
                    assertNotNull(value);
                    assertNotNull(value.getCreationDate());
                    assertNotNull(value.getId());
                    assertEquals(request.getName(), value.getName());
                    assertEquals(request.getSuid(), value.getSuid());
                    assertEquals(request.getSurname(), value.getSurname());

                    return true;
                })
                .verifyComplete();
    }

    @Test
    void createWithoutName() {
        final var request = this.enhancedRandom.nextObject(CustomerInsertRequestDTO.class, "name");

        final var exception = assertThrows(BusinessException.class, () -> this.customerService.create(request).block());
        assertEquals("Name is required", exception.getMessage());
    }

    @Test
    void createWithoutSuid() {
        final var request = this.enhancedRandom.nextObject(CustomerInsertRequestDTO.class, "suid");

        final var exception = assertThrows(BusinessException.class, () -> this.customerService.create(request).block());
        assertEquals("Suid is required", exception.getMessage());
    }

    @Test
    void createWithoutSurname() {
        final var request = this.enhancedRandom.nextObject(CustomerInsertRequestDTO.class, "surname");

        final var exception = assertThrows(BusinessException.class, () -> this.customerService.create(request).block());
        assertEquals("Surname is required", exception.getMessage());
    }

    @Test
    void update() {
        final var sample = this.createSample();

        final var request = this.enhancedRandom.nextObject(CustomerUpdateRequestDTO.class);
        request.setId(sample.getId());

        final var mono = this.customerService.update(request);

        StepVerifier.create(mono, StepVerifierOptions.create())
                .expectNextMatches(value -> {
                    assertNotNull(value);
                    assertNotNull(value.getCreationDate());
                    assertEquals(request.getId(), value.getId());
                    assertEquals(request.getName(), value.getName());
                    assertEquals(request.getSuid(), value.getSuid());
                    assertEquals(request.getSurname(), value.getSurname());

                    return true;
                })
                .verifyComplete();
    }

    @Test
    void updateNotFound() {
        final var request = this.enhancedRandom.nextObject(CustomerUpdateRequestDTO.class);
        request.setId("NOT_FOUND");

        final var exception = assertThrows(BusinessException.class, () -> this.customerService.update(request).block());
        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    void updateWithoutId() {
        final var request = this.enhancedRandom.nextObject(CustomerUpdateRequestDTO.class, "id");

        final var exception = assertThrows(BusinessException.class, () -> this.customerService.update(request).block());
        assertEquals("Id is required", exception.getMessage());
    }

    @Test
    void updateWithoutName() {
        final var request = this.enhancedRandom.nextObject(CustomerUpdateRequestDTO.class, "name");

        final var exception = assertThrows(BusinessException.class, () -> this.customerService.update(request).block());
        assertEquals("Name is required", exception.getMessage());
    }

    @Test
    void updateWithoutSuid() {
        final var request = this.enhancedRandom.nextObject(CustomerUpdateRequestDTO.class, "suid");

        final var exception = assertThrows(BusinessException.class, () -> this.customerService.update(request).block());
        assertEquals("Suid is required", exception.getMessage());
    }

    @Test
    void updateWithoutSurname() {
        final var request = this.enhancedRandom.nextObject(CustomerUpdateRequestDTO.class, "surname");

        final var exception = assertThrows(BusinessException.class, () -> this.customerService.update(request).block());
        assertEquals("Surname is required", exception.getMessage());
    }

    @Test
    void deleteBySuid() {
        final var sample = this.createSample();

        final var mono = this.customerService.deleteBySuid(sample.getSuid());

        StepVerifier.create(mono)
                .expectComplete()
                .verify();
    }

    @Test
    void deleteBySuidEmptyRequest() {
        final var exception = assertThrows(BusinessException.class, () -> this.customerService.deleteBySuid("").block());
        assertEquals("Suid is required", exception.getMessage());
    }

    @Test
    void deleteBySuidNotFound() {
        final var exception = assertThrows(BusinessException.class, () -> this.customerService.deleteBySuid("NOT_FOUND").block());
        assertEquals("Customer not found", exception.getMessage());
    }

    private Customer createSample() {
        final var customer = this.enhancedRandom.nextObject(Customer.class);
        customer.setId(null);

        return this.customerRepository.insert(customer).block();
    }
}

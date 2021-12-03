package io.good.food.repository;

import io.github.benas.randombeans.api.EnhancedRandom;
import io.good.food.Application;
import io.good.food.configuration.RandomBeanConfiguration;
import io.good.food.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes={Application.class, RandomBeanConfiguration.class})
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CustomerRepositoryTest {

    private final CustomerRepository customerRepository;

    private final EnhancedRandom enhancedRandom;

    @Autowired
    public CustomerRepositoryTest(final CustomerRepository customerRepository,
                                  final EnhancedRandom enhancedRandom) {
        this.customerRepository = customerRepository;
        this.enhancedRandom = enhancedRandom;
    }

    @BeforeEach
    public void init() {
        this.customerRepository.deleteAll().block();
    }

    @Test
    public void findAll() {
        final var sample = this.createSample();

        final var flux = this.customerRepository.findAll();

        StepVerifier.create(flux)
                .recordWith(ArrayList::new)
                .consumeRecordedWith(results -> {
                    assertNotNull(results);
                    assertFalse(results.isEmpty());

                    final var value = results.stream().findFirst().get();
                    assertEquals(sample.getCreationDate(), value.getCreationDate());
                    assertEquals(sample.getId(), value.getId());
                    assertEquals(sample.getName(), value.getName());
                    assertEquals(sample.getSuid(), value.getSuid());
                    assertEquals(sample.getSurname(), value.getSurname());
                })
                .expectComplete()
                .verify();
    }

    @Test
    public void findById() {
        final var sample = this.createSample();

        final var mono = this.customerRepository.findById(sample.getId());

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
    public void findBySuid() {
        final var sample = this.createSample();

        final var mono = this.customerRepository.findBySuid(sample.getSuid());

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

    private Customer createSample() {
        final var customer = this.enhancedRandom.nextObject(Customer.class);
        customer.setId(null);

        return this.customerRepository.insert(customer).block();
    }
}
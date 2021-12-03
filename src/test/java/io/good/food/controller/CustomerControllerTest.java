package io.good.food.controller;

import io.github.benas.randombeans.api.EnhancedRandom;
import io.good.food.Application;
import io.good.food.configuration.RandomBeanConfiguration;
import io.good.food.dto.request.CustomerInsertRequestDTO;
import io.good.food.dto.request.CustomerUpdateRequestDTO;
import io.good.food.dto.response.CustomerResponseDTO;
import io.good.food.entity.Customer;
import io.good.food.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes={Application.class, RandomBeanConfiguration.class})
@AutoConfigureWebTestClient(timeout = "10000")
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CustomerControllerTest {

    private final CustomerRepository customerRepository;

    private final EnhancedRandom enhancedRandom;

    private final WebTestClient webTestClient;

    @Autowired
    public CustomerControllerTest(final CustomerRepository customerRepository,
                                  final EnhancedRandom enhancedRandom,
                                  final WebTestClient webTestClient) {
        this.enhancedRandom = enhancedRandom;
        this.webTestClient = webTestClient;
        this.customerRepository = customerRepository;
    }

    @BeforeAll
    public void init() {
        this.customerRepository.deleteAll().block();
        this.createSample();
    }

    @Test
    void findBySuid(){
        this.webTestClient.get()
                .uri("/customers/suid")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CustomerResponseDTO.class)
                .value(response -> {
                    final var entity = this.customerRepository.findBySuid("suid").block();
                    assertEquals(entity.getCreationDate(), response.getCreationDate());
                    assertEquals(entity.getId(), response.getId());
                    assertEquals(entity.getName(), response.getName());
                    assertEquals(entity.getSuid(), response.getSuid());
                    assertEquals(entity.getSurname(), response.getSurname());
                });
    }

    @Test
    void create(){
        final var request = this.enhancedRandom.nextObject(CustomerInsertRequestDTO.class);

        this.webTestClient.post()
                .uri("/customers")
                .body(Mono.just(request), CustomerInsertRequestDTO.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(CustomerResponseDTO.class)
                .value(response -> {
                    assertNotNull(response.getCreationDate());
                    assertNotNull(response.getId());
                    assertEquals(request.getName(), response.getName());
                    assertEquals(request.getSuid(), response.getSuid());
                    assertEquals(request.getSurname(), response.getSurname());
                });
    }

    @Test
    void update(){
        final var entity = this.customerRepository.findBySuid("suid").block();

        final var request = this.enhancedRandom.nextObject(CustomerUpdateRequestDTO.class);
        request.setId(entity.getId());
        request.setSuid(entity.getSuid());

        this.webTestClient.patch()
                .uri("/customers")
                .body(Mono.just(request), CustomerUpdateRequestDTO.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CustomerResponseDTO.class)
                .value(response -> {
                    assertNotNull(response.getCreationDate());
                    assertEquals(request.getId(), response.getId());
                    assertEquals(request.getName(), response.getName());
                    assertEquals(request.getSuid(), response.getSuid());
                    assertEquals(request.getSurname(), response.getSurname());
                });
    }

    @Test
    void delete(){
        final var suid = "delete";

        final var customerToDelete = this.enhancedRandom.nextObject(Customer.class, "id");
        customerToDelete.setSuid(suid);

        this.customerRepository.insert(customerToDelete).block();

        this.webTestClient.delete()
                .uri("/customers/" + suid)
                .exchange()
                .expectStatus()
                .isNoContent();

        final var entity = this.customerRepository.findBySuid(suid).block();
        assertNull(entity);
    }

    private void createSample() {
        final var customer = this.enhancedRandom.nextObject(Customer.class, "id");
        customer.setSuid("suid");

        this.customerRepository.insert(customer).block();
    }

}
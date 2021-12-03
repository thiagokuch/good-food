package io.good.food.controller;

import io.github.benas.randombeans.api.EnhancedRandom;
import io.good.food.Application;
import io.good.food.configuration.RandomBeanConfiguration;
import io.good.food.dto.request.OrderInsertRequestDTO;
import io.good.food.dto.request.OrderUpdateRequestDTO;
import io.good.food.dto.response.OrderResponseDTO;
import io.good.food.dto.type.OrderStatusType;
import io.good.food.entity.Order;
import io.good.food.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes={Application.class, RandomBeanConfiguration.class})
@AutoConfigureWebTestClient(timeout = "10000")
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderControllerTest {

    private final OrderRepository orderRepository;

    private final EnhancedRandom enhancedRandom;

    private final WebTestClient webTestClient;

    @Autowired
    public OrderControllerTest(final OrderRepository orderRepository,
                               final EnhancedRandom enhancedRandom,
                               final WebTestClient webTestClient) {
        this.enhancedRandom = enhancedRandom;
        this.webTestClient = webTestClient;
        this.orderRepository = orderRepository;
    }

    @BeforeEach
    public void init() {
        this.orderRepository.deleteAll().block();
    }

    @Test
    void findAll(){
        final var sample = this.createSample();

        this.webTestClient.get()
                .uri("/orders")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(OrderResponseDTO.class)
                .value(list -> {
                    assertFalse(list.isEmpty());

                    final var item = list.get(0);

                    assertEquals(sample.getCreationDate(), item.getCreationDate());
                    assertEquals(sample.getId(), item.getId());
                    assertEquals(sample.getCustomerId(), item.getCustomerId());
                    assertEquals(sample.getStatus(), item.getStatus());

                    IntStream.range(0, sample.getMeals().size()).forEach(i -> {
                        final var mocked = sample.getMeals().get(i);
                        final var meal = item.getMeals().get(i);

                        assertEquals(mocked.getId(), meal.getId());
                        assertEquals(mocked.getCreationDate(), meal.getCreationDate());
                        assertEquals(mocked.getDescription(), meal.getDescription());
                        assertEquals(mocked.getNote(), meal.getNote());
                        assertEquals(mocked.getQuantity(), meal.getQuantity());
                        assertEquals(mocked.getType(), meal.getType());
                    });
                });
    }

    @Test
    void findByCustomerId(){
        final var sample = this.createSample();

        this.webTestClient.get()
                .uri("/orders/customers/" + sample.getCustomerId())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(OrderResponseDTO.class)
                .value(list -> {
                    assertFalse(list.isEmpty());

                    final var item = list.get(0);

                    assertEquals(sample.getCreationDate(), item.getCreationDate());
                    assertEquals(sample.getId(), item.getId());
                    assertEquals(sample.getCustomerId(), item.getCustomerId());
                    assertEquals(sample.getStatus(), item.getStatus());

                    IntStream.range(0, sample.getMeals().size()).forEach(i -> {
                        final var mocked = sample.getMeals().get(i);
                        final var meal = item.getMeals().get(i);

                        assertEquals(mocked.getId(), meal.getId());
                        assertEquals(mocked.getCreationDate(), meal.getCreationDate());
                        assertEquals(mocked.getDescription(), meal.getDescription());
                        assertEquals(mocked.getNote(), meal.getNote());
                        assertEquals(mocked.getQuantity(), meal.getQuantity());
                        assertEquals(mocked.getType(), meal.getType());
                    });
                });
    }

    @Test
    void findByStatus(){
        final var sample = this.createSample();

        this.webTestClient.get()
                .uri("/orders/status/" + sample.getStatus())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(OrderResponseDTO.class)
                .value(list -> {
                    assertFalse(list.isEmpty());

                    final var item = list.get(0);

                    assertEquals(sample.getCreationDate(), item.getCreationDate());
                    assertEquals(sample.getId(), item.getId());
                    assertEquals(sample.getCustomerId(), item.getCustomerId());
                    assertEquals(sample.getStatus(), item.getStatus());

                    IntStream.range(0, sample.getMeals().size()).forEach(i -> {
                        final var mocked = sample.getMeals().get(i);
                        final var meal = item.getMeals().get(i);

                        assertEquals(mocked.getId(), meal.getId());
                        assertEquals(mocked.getCreationDate(), meal.getCreationDate());
                        assertEquals(mocked.getDescription(), meal.getDescription());
                        assertEquals(mocked.getNote(), meal.getNote());
                        assertEquals(mocked.getQuantity(), meal.getQuantity());
                        assertEquals(mocked.getType(), meal.getType());
                    });
                });
    }

    @Test
    void findById(){
        final var sample = this.createSample();

        this.webTestClient.get()
                .uri("/orders/" + sample.getId())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(OrderResponseDTO.class)
                .value(response -> {
                    assertEquals(sample.getCreationDate(), response.getCreationDate());
                    assertEquals(sample.getId(), response.getId());
                    assertEquals(sample.getCustomerId(), response.getCustomerId());
                    assertEquals(sample.getStatus(), response.getStatus());

                    IntStream.range(0, sample.getMeals().size()).forEach(i -> {
                        final var mocked = sample.getMeals().get(i);
                        final var meal = response.getMeals().get(i);

                        assertEquals(mocked.getId(), meal.getId());
                        assertEquals(mocked.getCreationDate(), meal.getCreationDate());
                        assertEquals(mocked.getDescription(), meal.getDescription());
                        assertEquals(mocked.getNote(), meal.getNote());
                        assertEquals(mocked.getQuantity(), meal.getQuantity());
                        assertEquals(mocked.getType(), meal.getType());
                    });
                });
    }

    @Test
    void create(){
        final var request = this.enhancedRandom.nextObject(OrderInsertRequestDTO.class);
        request.setStatus(OrderStatusType.CREATED);

        this.webTestClient.post()
                .uri("/orders")
                .body(Mono.just(request), OrderInsertRequestDTO.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(OrderResponseDTO.class)
                .value(response -> {
                    assertNotNull(response.getCreationDate());
                    assertNotNull(response.getId());
                    assertEquals(request.getCustomerId(), response.getCustomerId());
                    assertEquals(request.getStatus(), response.getStatus());

                    IntStream.range(0, request.getMeals().size()).forEach(i -> {
                        final var mocked = request.getMeals().get(i);
                        final var meal = response.getMeals().get(i);

                        assertEquals(mocked.getId(), meal.getId());
                        assertEquals(mocked.getCreationDate(), meal.getCreationDate());
                        assertEquals(mocked.getDescription(), meal.getDescription());
                        assertEquals(mocked.getNote(), meal.getNote());
                        assertEquals(mocked.getQuantity(), meal.getQuantity());
                        assertEquals(mocked.getType(), meal.getType());
                    });
                });
    }

    @Test
    void update(){
        final var sample = this.createSample();

        final var request = this.enhancedRandom.nextObject(OrderUpdateRequestDTO.class);
        request.setId(sample.getId());
        request.setStatus(OrderStatusType.CREATED);

        this.webTestClient.patch()
                .uri("/orders")
                .body(Mono.just(request), OrderUpdateRequestDTO.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(OrderResponseDTO.class)
                .value(response -> {
                    assertNotNull(response.getCreationDate());
                    assertEquals(request.getId(), response.getId());
                    assertNotNull(response.getCustomerId());
                    assertEquals(request.getStatus(), response.getStatus());

                    IntStream.range(0, request.getMeals().size()).forEach(i -> {
                        final var mocked = request.getMeals().get(i);
                        final var meal = response.getMeals().get(i);

                        assertEquals(mocked.getId(), meal.getId());
                        assertEquals(mocked.getCreationDate(), meal.getCreationDate());
                        assertEquals(mocked.getDescription(), meal.getDescription());
                        assertEquals(mocked.getNote(), meal.getNote());
                        assertEquals(mocked.getQuantity(), meal.getQuantity());
                        assertEquals(mocked.getType(), meal.getType());
                    });
                });
    }

    @Test
    void delete(){
        final var sample = this.createSample();

        this.webTestClient.delete()
                .uri("/orders/" + sample.getId())
                .exchange()
                .expectStatus()
                .isNoContent();

        final var entity = this.orderRepository.findById(sample.getId()).block();
        assertNull(entity);
    }

    private Order createSample() {
        final var order = this.enhancedRandom.nextObject(Order.class, "id");

        return this.orderRepository.insert(order).block();
    }

}
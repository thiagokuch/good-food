package io.good.food.controller;

import io.github.benas.randombeans.api.EnhancedRandom;
import io.good.food.Application;
import io.good.food.configuration.RandomBeanConfiguration;
import io.good.food.dto.request.MealInsertRequestDTO;
import io.good.food.dto.request.MealUpdateRequestDTO;
import io.good.food.dto.response.MealResponseDTO;
import io.good.food.dto.type.MealType;
import io.good.food.entity.Meal;
import io.good.food.repository.MealRepository;
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
public class MealControllerTest {

    private final MealRepository mealRepository;

    private final EnhancedRandom enhancedRandom;

    private final WebTestClient webTestClient;

    @Autowired
    public MealControllerTest(final MealRepository mealRepository,
                              final EnhancedRandom enhancedRandom,
                              final WebTestClient webTestClient) {
        this.enhancedRandom = enhancedRandom;
        this.webTestClient = webTestClient;
        this.mealRepository = mealRepository;
    }

    @BeforeAll
    public void init() {
        this.mealRepository.deleteAll().block();
        this.createSample();
    }

    @Test
    void findAll(){
        final var meal = this.mealRepository.findAll().blockFirst();

        this.webTestClient.get()
                .uri("/meals")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(MealResponseDTO.class)
                .value(list -> {
                    assertFalse(list.isEmpty());

                    final var item = list.get(0);

                    assertEquals(meal.getCreationDate(), item.getCreationDate());
                    assertEquals(meal.getId(), item.getId());
                    assertEquals(meal.getDescription(), item.getDescription());
                    assertEquals(meal.getNote(), item.getNote());
                    assertEquals(meal.getType(), item.getType());
                });
    }

    @Test
    void findById(){
        final var meal = this.mealRepository.findAll().blockFirst();

        this.webTestClient.get()
                .uri("/meals/" + meal.getId())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(MealResponseDTO.class)
                .value(response -> {
                    assertEquals(meal.getCreationDate(), response.getCreationDate());
                    assertEquals(meal.getId(), response.getId());
                    assertEquals(meal.getDescription(), response.getDescription());
                    assertEquals(meal.getNote(), response.getNote());
                    assertEquals(meal.getType(), response.getType());
                });
    }

    @Test
    void create(){
        final var request = this.enhancedRandom.nextObject(MealInsertRequestDTO.class);
        request.setType(MealType.ASIAN);

        this.webTestClient.post()
                .uri("/meals")
                .body(Mono.just(request), MealInsertRequestDTO.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MealResponseDTO.class)
                .value(response -> {
                    assertNotNull(response.getCreationDate());
                    assertNotNull(response.getId());
                    assertEquals(request.getDescription(), response.getDescription());
                    assertEquals(request.getNote(), response.getNote());
                    assertEquals(request.getType(), response.getType());
                });
    }

    @Test
    void update(){
        final var entity = this.mealRepository.findAll().blockFirst();

        final var request = this.enhancedRandom.nextObject(MealUpdateRequestDTO.class);
        request.setId(entity.getId());
        request.setType(MealType.ASIAN);

        this.webTestClient.patch()
                .uri("/meals")
                .body(Mono.just(request), MealUpdateRequestDTO.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(MealResponseDTO.class)
                .value(response -> {
                    assertNotNull(response.getCreationDate());
                    assertEquals(request.getId(), response.getId());
                    assertEquals(request.getDescription(), response.getDescription());
                    assertEquals(request.getNote(), response.getNote());
                    assertEquals(request.getType(), response.getType());
                });
    }

    @Test
    void delete(){
        final var mealToDelete = this.mealRepository.insert(this.enhancedRandom.nextObject(Meal.class, "id")).block();

        this.webTestClient.delete()
                .uri("/meals/" + mealToDelete.getId())
                .exchange()
                .expectStatus()
                .isNoContent();

        final var entity = this.mealRepository.findById(mealToDelete.getId()).block();
        assertNull(entity);
    }

    private void createSample() {
        final var meal = this.enhancedRandom.nextObject(Meal.class, "id");

        this.mealRepository.insert(meal).block();
    }

}
package io.good.food.service;

import io.github.benas.randombeans.api.EnhancedRandom;
import io.good.food.Application;
import io.good.food.configuration.RandomBeanConfiguration;
import io.good.food.dto.request.MealInsertRequestDTO;
import io.good.food.dto.request.MealUpdateRequestDTO;
import io.good.food.dto.type.MealType;
import io.good.food.dto.type.OrderStatusType;
import io.good.food.entity.Meal;
import io.good.food.exception.BusinessException;
import io.good.food.repository.MealRepository;
import org.junit.Assert;
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

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes={Application.class, RandomBeanConfiguration.class})
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MealServiceTest {

    private final MealService mealService;

    private final MealRepository mealRepository;

    private final EnhancedRandom enhancedRandom;

    @Autowired
    public MealServiceTest(final MealService mealService,
                           final MealRepository mealRepository,
                           final EnhancedRandom enhancedRandom) {
        this.mealService = mealService;
        this.mealRepository = mealRepository;
        this.enhancedRandom = enhancedRandom;
    }

    @BeforeEach
    protected void init() {
        this.mealRepository.deleteAll().block();
    }

    @Test
    void findAll() {
        final var sample = this.createSample();

        final var flux = this.mealService.findAll();

        StepVerifier.create(flux)
                .recordWith(ArrayList::new)
                .consumeRecordedWith(results -> {
                    Assert.assertNotNull(results);
                    Assert.assertFalse(results.isEmpty());

                    final var value = results.stream().findFirst().get();
                    assertEquals(sample.getCreationDate(), value.getCreationDate());
                    assertEquals(sample.getId(), value.getId());
                    assertEquals(sample.getDescription(), value.getDescription());
                    assertEquals(sample.getNote(), value.getNote());
                    assertEquals(sample.getType(), value.getType());
                })
                .expectComplete()
                .verify();
    }

    @Test
    void findById() {
        final var sample = this.createSample();

        final var mono = this.mealService.findById(sample.getId());

        StepVerifier.create(mono, StepVerifierOptions.create())
                .expectNextMatches(value -> {
                    assertNotNull(value);
                    assertEquals(sample.getCreationDate(), value.getCreationDate());
                    assertEquals(sample.getId(), value.getId());
                    assertEquals(sample.getDescription(), value.getDescription());
                    assertEquals(sample.getNote(), value.getNote());
                    assertEquals(sample.getType(), value.getType());

                    return true;
                })
                .verifyComplete();
    }

    @Test
    void findByIdEmptyRequest() {
        final var exception = assertThrows(BusinessException.class, () -> this.mealService.findById("").block());
        assertEquals("Id is required", exception.getMessage());
    }

    @Test
    void findByIdNotFound() {
        final var mono = this.mealService.findById("NOTFOUND");

        StepVerifier.create(mono)
                .expectErrorMatches(throwable -> {
                    assertNotNull(throwable);
                    assertEquals("Meal not found", throwable.getMessage());

                    return true;
                })
                .verify();
    }

    @Test
    void create() {
        final var request = this.enhancedRandom.nextObject(MealInsertRequestDTO.class);
        request.setType(MealType.COLD_BEVERAGE);

        final var mono = this.mealService.create(request);

        StepVerifier.create(mono, StepVerifierOptions.create())
                .expectNextMatches(value -> {
                    assertNotNull(value);
                    assertNotNull(value.getCreationDate());
                    assertNotNull(value.getId());
                    assertEquals(request.getDescription(), value.getDescription());
                    assertEquals(request.getNote(), value.getNote());
                    assertEquals(request.getType(), value.getType());

                    return true;
                })
                .verifyComplete();
    }

    @Test
    void createWithoutDescription() {
        final var request = this.enhancedRandom.nextObject(MealInsertRequestDTO.class, "description");

        final var exception = assertThrows(BusinessException.class, () -> this.mealService.create(request).block());
        assertEquals("Description is required", exception.getMessage());
    }

    @Test
    void createWithNoType() {
        final var request = this.enhancedRandom.nextObject(MealInsertRequestDTO.class, "type");

        final var exception = assertThrows(BusinessException.class, () -> this.mealService.create(request).block());
        assertEquals("Invalid meal type", exception.getMessage());
    }

    @Test
    void createWithInvalidType() {
        final var request = this.enhancedRandom.nextObject(MealInsertRequestDTO.class, "type");
        request.setType(MealType.ERROR);

        final var exception = assertThrows(BusinessException.class, () -> this.mealService.create(request).block());
        assertEquals("Invalid meal type", exception.getMessage());
    }

    @Test
    void update() {
        final var sample = this.createSample();

        final var request = this.enhancedRandom.nextObject(MealUpdateRequestDTO.class);
        request.setId(sample.getId());
        request.setType(MealType.DESSERT);

        final var mono = this.mealService.update(request);

        StepVerifier.create(mono, StepVerifierOptions.create())
                .expectNextMatches(value -> {
                    assertNotNull(value);
                    assertNotNull(value.getCreationDate());
                    assertEquals(request.getId(), value.getId());
                    assertEquals(request.getDescription(), value.getDescription());
                    assertEquals(request.getNote(), value.getNote());
                    assertEquals(request.getType(), value.getType());

                    return true;
                })
                .verifyComplete();
    }

    @Test
    void updateNotFound() {
        final var request = this.enhancedRandom.nextObject(MealUpdateRequestDTO.class);
        request.setId("NOT_FOUND");

        final var exception = assertThrows(BusinessException.class, () -> this.mealService.update(request).block());
        assertEquals("Meal not found", exception.getMessage());
    }

    @Test
    void updateWithoutId() {
        final var request = this.enhancedRandom.nextObject(MealUpdateRequestDTO.class, "id");

        final var exception = assertThrows(BusinessException.class, () -> this.mealService.update(request).block());
        assertEquals("Id is required", exception.getMessage());
    }

    @Test
    void updateWithoutDescription() {
        final var request = this.enhancedRandom.nextObject(MealUpdateRequestDTO.class, "description");

        final var exception = assertThrows(BusinessException.class, () -> this.mealService.update(request).block());
        assertEquals("Description is required", exception.getMessage());
    }

    @Test
    void updateWithNoType() {
        final var request = this.enhancedRandom.nextObject(MealUpdateRequestDTO.class, "type");

        final var exception = assertThrows(BusinessException.class, () -> this.mealService.update(request).block());
        assertEquals("Invalid meal type", exception.getMessage());
    }

    @Test
    void updateWithInvalidType() {
        final var request = this.enhancedRandom.nextObject(MealUpdateRequestDTO.class, "type");
        request.setType(MealType.ERROR);

        final var exception = assertThrows(BusinessException.class, () -> this.mealService.update(request).block());
        assertEquals("Invalid meal type", exception.getMessage());
    }

    @Test
    void delete() {
        final var sample = this.createSample();

        final var mono = this.mealService.delete(sample.getId());

        StepVerifier.create(mono)
                .expectComplete()
                .verify();
    }

    @Test
    void deleteEmptyRequest() {
        final var exception = assertThrows(BusinessException.class, () -> this.mealService.delete("").block());
        assertEquals("Id is required", exception.getMessage());
    }

    @Test
    void deleteNotFound() {
        final var exception = assertThrows(BusinessException.class, () -> this.mealService.delete("NOT_FOUND").block());
        assertEquals("Meal not found", exception.getMessage());
    }

    private Meal createSample() {
        final var meal = this.enhancedRandom.nextObject(Meal.class);
        meal.setId(null);
        meal.setType(MealType.DESSERT);

        return this.mealRepository.insert(meal).block();
    }
}

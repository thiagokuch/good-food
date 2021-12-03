package io.good.food.repository;

import io.github.benas.randombeans.api.EnhancedRandom;
import io.good.food.Application;
import io.good.food.configuration.RandomBeanConfiguration;
import io.good.food.entity.Meal;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes={Application.class, RandomBeanConfiguration.class})
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MealRepositoryTest {

	private final MealRepository mealRepository;

	private final EnhancedRandom enhancedRandom;

    @Autowired
    public MealRepositoryTest(final MealRepository mealRepository,
                              final EnhancedRandom enhancedRandom) {
        this.mealRepository = mealRepository;
        this.enhancedRandom = enhancedRandom;
    }

    @BeforeAll
    public void init() {
        this.mealRepository.deleteAll().block();
    }

    @Test
    public void findAll() {
        final var sample = this.createSample();

        final var flux = this.mealRepository.findAll();

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
    public void findById() {
        final var sample = this.createSample();

        final var mono = this.mealRepository.findById(sample.getId());

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

    private Meal createSample() {
        final var meal = this.enhancedRandom.nextObject(Meal.class);
        meal.setId(null);

        return this.mealRepository.insert(meal).block();
    }
}
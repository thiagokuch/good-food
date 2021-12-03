package io.good.food.repository;

import io.good.food.entity.Meal;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface MealRepository extends ReactiveMongoRepository<Meal, String> {

}

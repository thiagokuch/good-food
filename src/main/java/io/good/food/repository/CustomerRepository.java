package io.good.food.repository;

import io.good.food.entity.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {

    Mono<Customer> findBySuid(final String suid);

}

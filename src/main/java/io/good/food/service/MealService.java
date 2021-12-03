package io.good.food.service;

import io.good.food.dto.request.MealInsertRequestDTO;
import io.good.food.dto.request.MealUpdateRequestDTO;
import io.good.food.dto.response.MealResponseDTO;
import io.good.food.dto.type.MealType;
import io.good.food.entity.Meal;
import io.good.food.exception.BusinessException;
import io.good.food.repository.MealRepository;
import io.vavr.control.Option;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class MealService {

    private final MapperFacade mapperFacade;

    private final MealRepository mealRepository;

    @Autowired
    public MealService(final MapperFacade mapperFacade,
                       final MealRepository mealRepository) {
        this.mapperFacade = mapperFacade;
        this.mealRepository = mealRepository;
    }

    public Flux<MealResponseDTO> findAll() {
        return this.mealRepository.findAll()
                .map(t -> this.mapperFacade.map(t, MealResponseDTO.class));
    }

    public Mono<MealResponseDTO> findById(final String id) {
        Option.of(id).filter(StringUtils::isNotBlank).getOrElseThrow(() -> new BusinessException("Id is required"));

        return this.mealRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException("Meal not found"))))
                .map(t -> this.mapperFacade.map(t, MealResponseDTO.class));
    }

    public Mono<MealResponseDTO> create(final MealInsertRequestDTO request) {
        this.validateInsert(request);

        final var entity = this.mapperFacade.map(request, Meal.class);
        entity.setCreationDate(LocalDateTime.now());

        return this.mealRepository.insert(entity)
                .map(t -> this.mapperFacade.map(t, MealResponseDTO.class));
    }

    public Mono<MealResponseDTO> update(final MealUpdateRequestDTO request) {
        this.validateUpdate(request);

        return this.mealRepository.findById(request.getId())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException("Meal not found"))))
                .flatMap(meal -> this.update(meal, request));
    }

    public Mono<Void> delete(final String id) {
        Option.of(id).filter(StringUtils::isNotBlank).getOrElseThrow(() -> new BusinessException("Id is required"));

        return this.mealRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException("Meal not found"))))
                .flatMap(t -> this.mealRepository.deleteById(t.getId()));
    }

    private Mono<MealResponseDTO> update(final Meal meal, final MealUpdateRequestDTO request) {
        meal.setDescription(request.getDescription());
        meal.setNote(request.getNote());
        meal.setType(request.getType());

        return this.mealRepository.save(meal)
                .map(t -> this.mapperFacade.map(t, MealResponseDTO.class));
    }


    private void validateInsert(final MealInsertRequestDTO request) {
        Option.of(request.getDescription()).filter(StringUtils::isNotBlank).getOrElseThrow(() -> new BusinessException("Description is required"));
        Option.when(request.getType() != null && !Objects.equals(MealType.ERROR, request.getType()), request::getType).getOrElseThrow(() -> new BusinessException("Invalid meal type"));
    }

    private void validateUpdate(final MealUpdateRequestDTO request) {
        Option.of(request.getId()).filter(StringUtils::isNotBlank).getOrElseThrow(() -> new BusinessException("Id is required"));
        Option.of(request.getDescription()).filter(StringUtils::isNotBlank).getOrElseThrow(() -> new BusinessException("Description is required"));
        Option.when(request.getType() != null && !Objects.equals(MealType.ERROR, request.getType()), request::getType).getOrElseThrow(() -> new BusinessException("Invalid meal type"));
    }
}

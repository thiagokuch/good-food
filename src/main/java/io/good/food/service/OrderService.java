package io.good.food.service;

import io.good.food.dto.request.OrderInsertRequestDTO;
import io.good.food.dto.request.OrderUpdateRequestDTO;
import io.good.food.dto.response.OrderResponseDTO;
import io.good.food.dto.type.ActionType;
import io.good.food.dto.type.OrderStatusType;
import io.good.food.entity.Order;
import io.good.food.exception.BusinessException;
import io.good.food.repository.OrderRepository;
import io.good.food.streaming.OrderProducer;
import io.vavr.control.Option;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class OrderService {

    private final MapperFacade mapperFacade;

    private final OrderProducer orderProducer;

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(final MapperFacade mapperFacade,
                        final OrderProducer orderProducer,
                        final OrderRepository orderRepository) {
        this.mapperFacade = mapperFacade;
        this.orderProducer = orderProducer;
        this.orderRepository = orderRepository;
    }

    public Flux<OrderResponseDTO> findAll() {
        return this.orderRepository.findAll()
                .map(t -> this.mapperFacade.map(t, OrderResponseDTO.class));
    }

    public Flux<OrderResponseDTO> findByCustomerId(final String customerId) {
        Option.of(customerId).filter(StringUtils::isNotBlank).getOrElseThrow(() -> new BusinessException("Customer id is required"));

        return this.orderRepository.findByCustomerId(customerId)
                .map(t -> this.mapperFacade.map(t, OrderResponseDTO.class));
    }

    public Flux<OrderResponseDTO> findByStatus(final OrderStatusType orderStatusType) {
        Option.when(orderStatusType != null && !Objects.equals(OrderStatusType.ERROR, orderStatusType), orderStatusType).getOrElseThrow(() -> new BusinessException("Invalid order type"));

        return this.orderRepository.findByStatus(orderStatusType)
                .map(t -> this.mapperFacade.map(t, OrderResponseDTO.class));
    }

    public Mono<OrderResponseDTO> findById(final String id) {
        Option.of(id).filter(StringUtils::isNotBlank).getOrElseThrow(() -> new BusinessException("Id is required"));

        return this.orderRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException("Order not found"))))
                .map(t -> this.mapperFacade.map(t, OrderResponseDTO.class));
    }

    public Mono<OrderResponseDTO> create(final OrderInsertRequestDTO request) {
        this.validateInsert(request);

        final var entity = this.mapperFacade.map(request, Order.class);
        entity.setCreationDate(LocalDateTime.now());

        return this.orderRepository.insert(entity)
                .map(t -> this.mapperFacade.map(t, OrderResponseDTO.class))
                .flatMap(t -> this.orderProducer.output(t, ActionType.CREATE));
    }

    public Mono<OrderResponseDTO> update(final OrderUpdateRequestDTO request) {
        this.validateUpdate(request);

        return this.orderRepository.findById(request.getId())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException("Order not found"))))
                .flatMap(order -> this.update(order, request))
                .flatMap(t -> this.orderProducer.output(t, ActionType.UPDATE));
    }

    public Mono<Void> delete(final String id) {
        Option.of(id).filter(StringUtils::isNotBlank).getOrElseThrow(() -> new BusinessException("Id is required"));

        return this.orderRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException("Order not found"))))
                .flatMap(this::delete);
    }

    private Mono<OrderResponseDTO> update(final Order order, final OrderUpdateRequestDTO request) {
        order.setMeals(request.getMeals());
        order.setStatus(request.getStatus());

        return this.orderRepository.save(order)
                .map(t -> this.mapperFacade.map(t, OrderResponseDTO.class));
    }

    private Mono<Void> delete(final Order order) {
        final var response = this.mapperFacade.map(order, OrderResponseDTO.class);

        return this.orderRepository.delete(order)
                .doOnSuccess(t -> this.orderProducer.output(response, ActionType.DELETE));
    }

    private void validateInsert(final OrderInsertRequestDTO request) {
        Option.of(request.getCustomerId()).filter(StringUtils::isNotBlank).getOrElseThrow(() -> new BusinessException("Customer Id is required"));
        Option.of(request.getMeals()).filter(CollectionUtils::isNotEmpty).getOrElseThrow(() -> new BusinessException("Meals are required"));
        Option.when(request.getStatus() != null && !Objects.equals(OrderStatusType.ERROR, request.getStatus()), request::getStatus).getOrElseThrow(() -> new BusinessException("Invalid order status"));
    }

    private void validateUpdate(final OrderUpdateRequestDTO request) {
        Option.of(request.getId()).filter(StringUtils::isNotBlank).getOrElseThrow(() -> new BusinessException("Id is required"));
        Option.when(request.getStatus() != null && !Objects.equals(OrderStatusType.ERROR, request.getStatus()), request::getStatus).getOrElseThrow(() -> new BusinessException("Invalid order status"));
    }
}

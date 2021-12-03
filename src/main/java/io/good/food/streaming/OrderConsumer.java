package io.good.food.streaming;

import io.good.food.dto.request.OrderInsertRequestDTO;
import io.good.food.dto.request.OrderUpdateRequestDTO;
import io.good.food.dto.stream.OrderInputDTO;
import io.good.food.dto.type.ActionType;
import io.good.food.streaming.channel.OrderChannels;
import io.good.food.service.OrderService;
import io.vavr.control.Try;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import static io.vavr.API.*;

@Component
@EnableBinding(OrderChannels.class)
public class OrderConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderConsumer.class);

    private final MapperFacade mapperFacade;

    private final OrderService orderService;

    public OrderConsumer(final MapperFacade mapperFacade,
                         final OrderService orderService) {
        this.mapperFacade = mapperFacade;
        this.orderService = orderService;
    }

    @StreamListener(target = OrderChannels.ORDER_CONSUMER)
    public void listener(final OrderInputDTO input) {
        if(input.getAction() == null) {
            LOGGER.error("Action is required to complete the operation");

        } else {
            Match(input.getAction()).of(
                    Case($(ActionType.CREATE), i -> this.create(input)),
                    Case($(ActionType.UPDATE), i -> this.update(input)),
                    Case($(ActionType.DELETE), i -> this.delete(input))
            );
        }
    }

    private OrderInputDTO create(final OrderInputDTO input) {
        final var request = this.mapperFacade.map(input, OrderInsertRequestDTO.class);

        Try.of(() -> this.orderService.create(request).toProcessor().block())
                .onFailure(throwable -> LOGGER.error("Failure processing an order creation message. Error: {}", this.getMessage(throwable)));

        return input;
    }

    private OrderInputDTO update(final OrderInputDTO input) {
        final var request = this.mapperFacade.map(input, OrderUpdateRequestDTO.class);

        Try.of(() -> this.orderService.update(request).toProcessor().block())
                .onFailure(throwable -> LOGGER.error("Failure processing an order update message. Error: {}", this.getMessage(throwable)));

        return input;
    }

    private OrderInputDTO delete(final OrderInputDTO input) {
        Try.of(() -> this.orderService.delete(input.getId()).toProcessor().block())
                .onFailure(throwable -> LOGGER.error("Failure processing an order delete message. Error: {}", this.getMessage(throwable)));

        return input;
    }

    private String getMessage(final Throwable throwable) {
        if(throwable == null) {
            return StringUtils.EMPTY;
        }

        final var rootCause = org.apache.commons.lang3.exception.ExceptionUtils.getRootCause(throwable);
        return StringUtils.isBlank(rootCause.getMessage()) ? throwable.toString() : rootCause.getMessage();
    }
}

package io.good.food.streaming;

import io.good.food.dto.response.OrderResponseDTO;
import io.good.food.dto.stream.OrderOutputDTO;
import io.good.food.dto.type.ActionType;
import io.good.food.streaming.channel.OrderChannels;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class OrderProducer {

    private final MapperFacade mapperFacade;

    private final OrderChannels orderChannels;

    @Autowired
    public OrderProducer(final MapperFacade mapperFacade,
                         final OrderChannels orderChannels) {
        this.mapperFacade = mapperFacade;
        this.orderChannels = orderChannels;
    }

    public Mono<OrderResponseDTO> output(final OrderResponseDTO response, final ActionType action) {
        final var output = this.mapperFacade.map(response, OrderOutputDTO.class);
        output.setAction(action);

        final var message = MessageBuilder.withPayload(output)
                .build();

        return Mono.just(this.orderChannels.orderProducer().send(message))
                .thenReturn(response);
    }

}

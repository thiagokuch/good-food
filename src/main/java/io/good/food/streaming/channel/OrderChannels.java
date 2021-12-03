package io.good.food.streaming.channel;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.SubscribableChannel;

public interface OrderChannels {

    String ORDER_CONSUMER = "order-consumer";
    String ORDER_PRODUCER = "order-producer";

    @Output(ORDER_CONSUMER)
    SubscribableChannel orderConsumer();

    @Output(ORDER_PRODUCER)
    SubscribableChannel orderProducer();

}

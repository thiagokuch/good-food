package io.good.food.streaming;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.benas.randombeans.api.EnhancedRandom;
import io.good.food.Application;
import io.good.food.configuration.RandomBeanConfiguration;
import io.good.food.dto.stream.OrderInputDTO;
import io.good.food.dto.stream.OrderOutputDTO;
import io.good.food.dto.type.ActionType;
import io.good.food.dto.type.OrderStatusType;
import io.good.food.entity.Order;
import io.good.food.repository.OrderRepository;
import io.good.food.streaming.channel.OrderChannels;
import io.vavr.control.Try;
import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.MimeTypeUtils;

import java.util.stream.IntStream;

import static org.junit.Assert.*;

@SpringBootTest(classes={Application.class, RandomBeanConfiguration.class})
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderConsumerTest {

    private final OrderRepository orderRepository;

    private final OrderChannels orderChannels;

    private final EnhancedRandom enhancedRandom;

    private final MapperFacade mapperFacade;

    private final MessageCollector messageCollector;

    private final ObjectMapper objectMapper;

    @Autowired
    public OrderConsumerTest(final OrderRepository orderRepository,
                             final OrderChannels orderChannels,
                             final EnhancedRandom enhancedRandom,
                             final MapperFacade mapperFacade,
                             final MessageCollector messageCollector,
                             final ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.orderChannels = orderChannels;
        this.enhancedRandom = enhancedRandom;
        this.mapperFacade = mapperFacade;
        this.messageCollector = messageCollector;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    protected void init() {
        this.messageCollector.forChannel(this.orderChannels.orderConsumer()).clear();
        this.messageCollector.forChannel(this.orderChannels.orderProducer()).clear();
    }

    @Test
    void sendCreation() {
        final var request = this.enhancedRandom.nextObject(OrderInputDTO.class, "id");
        request.setAction(ActionType.CREATE);
        request.setStatus(OrderStatusType.CREATED);

        final var messageEvent = MessageBuilder
                .withPayload(request)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build();

        this.orderChannels.orderConsumer().send(messageEvent);

        final var message = this.messageCollector.forChannel(this.orderChannels.orderProducer()).poll();
        assertNotNull(message);

        final var payload = Try.of(() -> this.objectMapper.readValue((String ) message.getPayload(), OrderOutputDTO.class)).getOrNull();
        assertNotNull(payload);
        assertNotNull(payload);
        assertNotNull(payload.getCreationDate());
        assertNotNull(payload.getId());
        assertEquals(request.getCustomerId(), payload.getCustomerId());
        assertEquals(request.getStatus(), payload.getStatus());
        assertEquals(ActionType.CREATE, payload.getAction());

        IntStream.range(0, request.getMeals().size()).forEach(i -> {
            final var mocked = request.getMeals().get(i);
            final var item = payload.getMeals().get(i);

            assertEquals(mocked.getId(), item.getId());
            assertEquals(mocked.getCreationDate(), item.getCreationDate());
            assertEquals(mocked.getDescription(), item.getDescription());
            assertEquals(mocked.getNote(), item.getNote());
            assertEquals(mocked.getQuantity(), item.getQuantity());
            assertEquals(mocked.getType(), item.getType());
        });
    }

    @Test
    void sendCreationWithoutCustomerId() {
        final var request = this.enhancedRandom.nextObject(OrderInputDTO.class, "customerId");
        request.setAction(ActionType.CREATE);
        request.setStatus(OrderStatusType.CREATED);

        final var messageEvent = MessageBuilder
                .withPayload(request)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build();

        this.orderChannels.orderConsumer().send(messageEvent);

        final var consumer = this.messageCollector.forChannel(this.orderChannels.orderConsumer()).poll();
        assertNull(consumer);

        final var producer = this.messageCollector.forChannel(this.orderChannels.orderProducer()).poll();
        assertNull(producer);
    }

    @Test
    void sendUpdate() {
        final var sample = this.createSample();

        final var request = this.enhancedRandom.nextObject(OrderInputDTO.class, "id, customerId");
        request.setAction(ActionType.UPDATE);
        request.setStatus(OrderStatusType.CREATED);
        request.setId(sample.getId());
        request.setCustomerId(sample.getCustomerId());

        final var messageEvent = MessageBuilder
                .withPayload(request)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build();

        this.orderChannels.orderConsumer().send(messageEvent);

        final var message = this.messageCollector.forChannel(this.orderChannels.orderProducer()).poll();
        assertNotNull(message);

        final var payload = Try.of(() -> this.objectMapper.readValue((String ) message.getPayload(), OrderOutputDTO.class)).getOrNull();
        assertNotNull(payload);
        assertNotNull(payload);
        assertNotNull(payload.getCreationDate());
        assertNotNull(payload.getId());
        assertEquals(request.getCustomerId(), payload.getCustomerId());
        assertEquals(request.getStatus(), payload.getStatus());
        assertEquals(ActionType.UPDATE, payload.getAction());

        IntStream.range(0, request.getMeals().size()).forEach(i -> {
            final var mocked = request.getMeals().get(i);
            final var item = payload.getMeals().get(i);

            assertEquals(mocked.getId(), item.getId());
            assertEquals(mocked.getCreationDate(), item.getCreationDate());
            assertEquals(mocked.getDescription(), item.getDescription());
            assertEquals(mocked.getNote(), item.getNote());
            assertEquals(mocked.getQuantity(), item.getQuantity());
            assertEquals(mocked.getType(), item.getType());
        });
    }

    @Test
    void sendUpdateWithoutId() {
        final var request = this.enhancedRandom.nextObject(OrderInputDTO.class, "id");
        request.setAction(ActionType.UPDATE);
        request.setStatus(OrderStatusType.CREATED);

        final var messageEvent = MessageBuilder
                .withPayload(request)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build();

        this.orderChannels.orderConsumer().send(messageEvent);

        final var consumer = this.messageCollector.forChannel(this.orderChannels.orderConsumer()).poll();
        assertNull(consumer);

        final var producer = this.messageCollector.forChannel(this.orderChannels.orderProducer()).poll();
        assertNull(producer);
    }

    @Test
    void sendDelete() {
        final var sample = this.createSample();

        final var request = this.mapperFacade.map(sample, OrderInputDTO.class);
        request.setAction(ActionType.DELETE);

        final var messageEvent = MessageBuilder
                .withPayload(request)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build();

        this.orderChannels.orderConsumer().send(messageEvent);

        final var message = this.messageCollector.forChannel(this.orderChannels.orderProducer()).poll();
        assertNotNull(message);

        final var payload = Try.of(() -> this.objectMapper.readValue((String ) message.getPayload(), OrderOutputDTO.class)).getOrNull();
        assertNotNull(payload);
        assertNotNull(payload);
        assertNotNull(payload.getCreationDate());
        assertNotNull(payload.getId());
        assertEquals(request.getCustomerId(), payload.getCustomerId());
        assertEquals(request.getStatus(), payload.getStatus());
        assertEquals(ActionType.DELETE, payload.getAction());

        IntStream.range(0, request.getMeals().size()).forEach(i -> {
            final var mocked = request.getMeals().get(i);
            final var item = payload.getMeals().get(i);

            assertEquals(mocked.getId(), item.getId());
            assertEquals(mocked.getCreationDate(), item.getCreationDate());
            assertEquals(mocked.getDescription(), item.getDescription());
            assertEquals(mocked.getNote(), item.getNote());
            assertEquals(mocked.getQuantity(), item.getQuantity());
            assertEquals(mocked.getType(), item.getType());
        });
    }

    @Test
    void sendDeleteWithoutId() {
        final var request = this.enhancedRandom.nextObject(OrderInputDTO.class, "id");
        request.setStatus(OrderStatusType.CREATED);
        request.setAction(ActionType.DELETE);

        final var messageEvent = MessageBuilder
                .withPayload(request)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build();

        this.orderChannels.orderConsumer().send(messageEvent);

        final var consumer = this.messageCollector.forChannel(this.orderChannels.orderConsumer()).poll();
        assertNull(consumer);

        final var producer = this.messageCollector.forChannel(this.orderChannels.orderProducer()).poll();
        assertNull(producer);
    }

    @Test
    void sendNoAction() {
        final var request = this.enhancedRandom.nextObject(OrderInputDTO.class, "action");

        final var messageEvent = MessageBuilder
                .withPayload(request)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build();

        this.orderChannels.orderConsumer().send(messageEvent);

        final var message = this.messageCollector.forChannel(this.orderChannels.orderProducer()).poll();
        assertNull(message);
    }
    
    private Order createSample() {
        final var order = this.enhancedRandom.nextObject(Order.class);
        order.setId(null);

        return this.orderRepository.insert(order).block();
    }
}

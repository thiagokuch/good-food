package io.good.food.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.benas.randombeans.api.EnhancedRandom;
import io.good.food.Application;
import io.good.food.configuration.RandomBeanConfiguration;
import io.good.food.dto.request.OrderInsertRequestDTO;
import io.good.food.dto.request.OrderUpdateRequestDTO;
import io.good.food.dto.stream.OrderOutputDTO;
import io.good.food.dto.type.ActionType;
import io.good.food.dto.type.OrderStatusType;
import io.good.food.entity.Order;
import io.good.food.exception.BusinessException;
import io.good.food.repository.OrderRepository;
import io.good.food.streaming.channel.OrderChannels;
import io.vavr.control.Try;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;

import java.util.ArrayList;
import java.util.stream.IntStream;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes={Application.class, RandomBeanConfiguration.class})
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderServiceTest {

    private final OrderService orderService;

    private final OrderRepository orderRepository;

    private final EnhancedRandom enhancedRandom;

    private final OrderChannels orderChannels;

    private final MessageCollector messageCollector;

    private final ObjectMapper objectMapper;

    @Autowired
    public OrderServiceTest(final OrderService orderService,
                            final OrderRepository orderRepository,
                            final EnhancedRandom enhancedRandom,
                            final OrderChannels orderChannels,
                            final MessageCollector messageCollector,
                            final ObjectMapper objectMapper) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.enhancedRandom = enhancedRandom;
        this.orderChannels = orderChannels;
        this.messageCollector = messageCollector;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    protected void init() {
        this.orderRepository.deleteAll().block();
        this.messageCollector.forChannel(this.orderChannels.orderConsumer()).clear();
        this.messageCollector.forChannel(this.orderChannels.orderProducer()).clear();
    }

    @Test
    void findAll() {
        final var sample = this.createSample();

        final var flux = this.orderService.findAll();

        StepVerifier.create(flux)
                .recordWith(ArrayList::new)
                .consumeRecordedWith(results -> {
                    Assert.assertNotNull(results);
                    Assert.assertFalse(results.isEmpty());

                    final var value = results.stream().findFirst().get();
                    assertEquals(sample.getCreationDate(), value.getCreationDate());
                    assertEquals(sample.getId(), value.getId());
                    assertEquals(sample.getCustomerId(), value.getCustomerId());
                    assertEquals(sample.getStatus(), value.getStatus());

                    IntStream.range(0, sample.getMeals().size()).forEach(i -> {
                        final var mocked = sample.getMeals().get(i);
                        final var item = value.getMeals().get(i);

                        assertEquals(mocked.getId(), item.getId());
                        assertEquals(mocked.getCreationDate(), item.getCreationDate());
                        assertEquals(mocked.getDescription(), item.getDescription());
                        assertEquals(mocked.getNote(), item.getNote());
                        assertEquals(mocked.getQuantity(), item.getQuantity());
                        assertEquals(mocked.getType(), item.getType());
                    });
                })
                .expectComplete()
                .verify();
    }

    @Test
    void findByCustomerId() {
        final var sample = this.createSample();

        final var flux = this.orderService.findByCustomerId(sample.getCustomerId());

        StepVerifier.create(flux)
                .recordWith(ArrayList::new)
                .consumeRecordedWith(results -> {
                    Assert.assertNotNull(results);
                    Assert.assertFalse(results.isEmpty());

                    final var value = results.stream().findFirst().get();
                    assertEquals(sample.getCreationDate(), value.getCreationDate());
                    assertEquals(sample.getId(), value.getId());
                    assertEquals(sample.getCustomerId(), value.getCustomerId());
                    assertEquals(sample.getStatus(), value.getStatus());

                    IntStream.range(0, sample.getMeals().size()).forEach(i -> {
                        final var mocked = sample.getMeals().get(i);
                        final var item = value.getMeals().get(i);

                        assertEquals(mocked.getId(), item.getId());
                        assertEquals(mocked.getCreationDate(), item.getCreationDate());
                        assertEquals(mocked.getDescription(), item.getDescription());
                        assertEquals(mocked.getNote(), item.getNote());
                        assertEquals(mocked.getQuantity(), item.getQuantity());
                        assertEquals(mocked.getType(), item.getType());
                    });
                })
                .expectComplete()
                .verify();
    }

    @Test
    void findByCustomerIdEmptyRequest() {
        final var exception = assertThrows(BusinessException.class, () -> this.orderService.findByCustomerId(null).collectList().block());
        assertEquals("Customer id is required", exception.getMessage());
    }

    @Test
    void findByCustomerIdNotFound() {
        final var flux = this.orderService.findByCustomerId("NOT_FOUND");

        StepVerifier.create(flux)
                .expectComplete()
                .verify();
    }

    @Test
    void findByStatus() {
        final var sample = this.createSample();

        final var flux = this.orderService.findByStatus(sample.getStatus());

        StepVerifier.create(flux)
                .recordWith(ArrayList::new)
                .consumeRecordedWith(results -> {
                    Assert.assertNotNull(results);
                    Assert.assertFalse(results.isEmpty());

                    final var value = results.stream().findFirst().get();
                    assertEquals(sample.getCreationDate(), value.getCreationDate());
                    assertEquals(sample.getId(), value.getId());
                    assertEquals(sample.getCustomerId(), value.getCustomerId());
                    assertEquals(sample.getStatus(), value.getStatus());

                    IntStream.range(0, sample.getMeals().size()).forEach(i -> {
                        final var mocked = sample.getMeals().get(i);
                        final var item = value.getMeals().get(i);

                        assertEquals(mocked.getId(), item.getId());
                        assertEquals(mocked.getCreationDate(), item.getCreationDate());
                        assertEquals(mocked.getDescription(), item.getDescription());
                        assertEquals(mocked.getNote(), item.getNote());
                        assertEquals(mocked.getQuantity(), item.getQuantity());
                        assertEquals(mocked.getType(), item.getType());
                    });
                })
                .expectComplete()
                .verify();
    }

    @Test
    void findByStatusEmptyRequest() {
        final var exception = assertThrows(BusinessException.class, () -> this.orderService.findByStatus(null).collectList().block());
        assertEquals("Invalid order type", exception.getMessage());
    }

    @Test
    void findByStatusErrorRequest() {
        final var exception = assertThrows(BusinessException.class, () -> this.orderService.findByStatus(OrderStatusType.ERROR).collectList().block());
        assertEquals("Invalid order type", exception.getMessage());
    }

    @Test
    void findByStatusNotFound() {
        final var flux = this.orderService.findByStatus(OrderStatusType.CREATED);

        StepVerifier.create(flux)
                .expectComplete()
                .verify();
    }

    @Test
    void findById() {
        final var sample = this.createSample();

        final var mono = this.orderService.findById(sample.getId());

        StepVerifier.create(mono, StepVerifierOptions.create())
                .expectNextMatches(value -> {
                    assertNotNull(value);
                    assertEquals(sample.getCreationDate(), value.getCreationDate());
                    assertEquals(sample.getId(), value.getId());
                    assertEquals(sample.getCustomerId(), value.getCustomerId());
                    assertEquals(sample.getStatus(), value.getStatus());

                    IntStream.range(0, sample.getMeals().size()).forEach(i -> {
                        final var mocked = sample.getMeals().get(i);
                        final var item = value.getMeals().get(i);

                        assertEquals(mocked.getId(), item.getId());
                        assertEquals(mocked.getCreationDate(), item.getCreationDate());
                        assertEquals(mocked.getDescription(), item.getDescription());
                        assertEquals(mocked.getNote(), item.getNote());
                        assertEquals(mocked.getQuantity(), item.getQuantity());
                        assertEquals(mocked.getType(), item.getType());
                    });

                    return true;
                })
                .verifyComplete();
    }

    @Test
    void findByIdEmptyRequest() {
        final var exception = assertThrows(BusinessException.class, () -> this.orderService.findById("").block());
        assertEquals("Id is required", exception.getMessage());
    }

    @Test
    void findByIdNotFound() {
        final var mono = this.orderService.findById("NOTFOUND");

        StepVerifier.create(mono)
                .expectErrorMatches(throwable -> {
                    assertNotNull(throwable);
                    assertEquals("Order not found", throwable.getMessage());

                    return true;
                })
                .verify();
    }

    @Test
    void create() {
        final var request = this.enhancedRandom.nextObject(OrderInsertRequestDTO.class);
        request.setStatus(OrderStatusType.CREATED);

        final var mono = this.orderService.create(request);

        StepVerifier.create(mono, StepVerifierOptions.create())
                .expectNextMatches(value -> {
                    assertNotNull(value);
                    assertNotNull(value.getCreationDate());
                    assertNotNull(value.getId());
                    assertEquals(request.getCustomerId(), value.getCustomerId());
                    assertEquals(request.getStatus(), value.getStatus());

                    IntStream.range(0, request.getMeals().size()).forEach(i -> {
                        final var mocked = request.getMeals().get(i);
                        final var item = value.getMeals().get(i);

                        assertEquals(mocked.getId(), item.getId());
                        assertEquals(mocked.getCreationDate(), item.getCreationDate());
                        assertEquals(mocked.getDescription(), item.getDescription());
                        assertEquals(mocked.getNote(), item.getNote());
                        assertEquals(mocked.getQuantity(), item.getQuantity());
                        assertEquals(mocked.getType(), item.getType());
                    });

                    final var message = this.messageCollector.forChannel(this.orderChannels.orderProducer()).poll();
                    Assertions.assertNotNull(message);

                    final var payload = Try.of(() -> this.objectMapper.readValue((String ) message.getPayload(), OrderOutputDTO.class)).getOrNull();
                    Assertions.assertNotNull(payload);
                    assertNotNull(payload);
                    assertEquals(value.getCreationDate(), payload.getCreationDate());
                    assertEquals(value.getId(), payload.getId());
                    assertEquals(value.getCustomerId(), payload.getCustomerId());
                    assertEquals(value.getStatus(), payload.getStatus());
                    assertEquals(ActionType.CREATE, payload.getAction());

                    IntStream.range(0, value.getMeals().size()).forEach(i -> {
                        final var mocked = value.getMeals().get(i);
                        final var item = payload.getMeals().get(i);

                        assertEquals(mocked.getId(), item.getId());
                        assertEquals(mocked.getCreationDate(), item.getCreationDate());
                        assertEquals(mocked.getDescription(), item.getDescription());
                        assertEquals(mocked.getNote(), item.getNote());
                        assertEquals(mocked.getQuantity(), item.getQuantity());
                        assertEquals(mocked.getType(), item.getType());
                    });

                    return true;
                })
                .verifyComplete();
    }

    @Test
    void createWithoutCustomerId() {
        final var request = this.enhancedRandom.nextObject(OrderInsertRequestDTO.class, "customerId");

        final var exception = assertThrows(BusinessException.class, () -> this.orderService.create(request).block());
        assertEquals("Customer Id is required", exception.getMessage());
    }

    @Test
    void createWithoutMeals() {
        final var request = this.enhancedRandom.nextObject(OrderInsertRequestDTO.class, "meals");

        final var exception = assertThrows(BusinessException.class, () -> this.orderService.create(request).block());
        assertEquals("Meals are required", exception.getMessage());
    }

    @Test
    void createWithNoStatus() {
        final var request = this.enhancedRandom.nextObject(OrderInsertRequestDTO.class, "status");

        final var exception = assertThrows(BusinessException.class, () -> this.orderService.create(request).block());
        assertEquals("Invalid order status", exception.getMessage());
    }

    @Test
    void createWithInvalidStatus() {
        final var request = this.enhancedRandom.nextObject(OrderInsertRequestDTO.class, "status");
        request.setStatus(OrderStatusType.ERROR);

        final var exception = assertThrows(BusinessException.class, () -> this.orderService.create(request).block());
        assertEquals("Invalid order status", exception.getMessage());
    }

    @Test
    void update() {
        final var sample = this.createSample();

        final var request = this.enhancedRandom.nextObject(OrderUpdateRequestDTO.class);
        request.setId(sample.getId());
        request.setStatus(OrderStatusType.CREATED);

        final var mono = this.orderService.update(request);

        StepVerifier.create(mono, StepVerifierOptions.create())
                .expectNextMatches(value -> {
                    assertNotNull(value);
                    assertNotNull(value.getCreationDate());
                    assertEquals(request.getId(), value.getId());
                    assertEquals(request.getStatus(), value.getStatus());

                    IntStream.range(0, request.getMeals().size()).forEach(i -> {
                        final var mocked = request.getMeals().get(i);
                        final var item = value.getMeals().get(i);

                        assertEquals(mocked.getId(), item.getId());
                        assertEquals(mocked.getCreationDate(), item.getCreationDate());
                        assertEquals(mocked.getDescription(), item.getDescription());
                        assertEquals(mocked.getNote(), item.getNote());
                        assertEquals(mocked.getQuantity(), item.getQuantity());
                        assertEquals(mocked.getType(), item.getType());
                    });

                    final var message = this.messageCollector.forChannel(this.orderChannels.orderProducer()).poll();
                    Assertions.assertNotNull(message);

                    final var payload = Try.of(() -> this.objectMapper.readValue((String ) message.getPayload(), OrderOutputDTO.class)).getOrNull();
                    Assertions.assertNotNull(payload);
                    assertNotNull(payload);
                    assertEquals(value.getCreationDate(), payload.getCreationDate());
                    assertEquals(value.getId(), payload.getId());
                    assertEquals(value.getCustomerId(), payload.getCustomerId());
                    assertEquals(value.getStatus(), payload.getStatus());
                    assertEquals(ActionType.UPDATE, payload.getAction());

                    IntStream.range(0, value.getMeals().size()).forEach(i -> {
                        final var mocked = value.getMeals().get(i);
                        final var item = payload.getMeals().get(i);

                        assertEquals(mocked.getId(), item.getId());
                        assertEquals(mocked.getCreationDate(), item.getCreationDate());
                        assertEquals(mocked.getDescription(), item.getDescription());
                        assertEquals(mocked.getNote(), item.getNote());
                        assertEquals(mocked.getQuantity(), item.getQuantity());
                        assertEquals(mocked.getType(), item.getType());
                    });

                    return true;
                })
                .verifyComplete();
    }

    @Test
    void updateWithoutId() {
        final var request = this.enhancedRandom.nextObject(OrderUpdateRequestDTO.class, "id");

        final var exception = assertThrows(BusinessException.class, () -> this.orderService.update(request).block());
        assertEquals("Id is required", exception.getMessage());
    }

    @Test
    void updateWithNoStatus() {
        final var request = this.enhancedRandom.nextObject(OrderUpdateRequestDTO.class, "status");

        final var exception = assertThrows(BusinessException.class, () -> this.orderService.update(request).block());
        assertEquals("Invalid order status", exception.getMessage());
    }

    @Test
    void updateWithInvalidStatus() {
        final var request = this.enhancedRandom.nextObject(OrderUpdateRequestDTO.class, "status");
        request.setStatus(OrderStatusType.ERROR);

        final var exception = assertThrows(BusinessException.class, () -> this.orderService.update(request).block());
        assertEquals("Invalid order status", exception.getMessage());
    }

    @Test
    void updateNotFound() {
        final var request = this.enhancedRandom.nextObject(OrderUpdateRequestDTO.class);
        request.setId("NOT_FOUND");

        final var exception = assertThrows(BusinessException.class, () -> this.orderService.update(request).block());
        assertEquals("Order not found", exception.getMessage());
    }

    @Test
    void delete() {
        final var sample = this.createSample();

        final var mono = this.orderService.delete(sample.getId());

        StepVerifier.create(mono)
                .expectComplete()
                .verify();

        final var message = this.messageCollector.forChannel(this.orderChannels.orderProducer()).poll();
        Assertions.assertNotNull(message);

        final var payload = Try.of(() -> this.objectMapper.readValue((String ) message.getPayload(), OrderOutputDTO.class)).getOrNull();
        Assertions.assertNotNull(payload);
        assertNotNull(payload);
        assertEquals(sample.getCreationDate(), payload.getCreationDate());
        assertEquals(sample.getId(), payload.getId());
        assertEquals(sample.getCustomerId(), payload.getCustomerId());
        assertEquals(sample.getStatus(), payload.getStatus());
        assertEquals(ActionType.DELETE, payload.getAction());

        IntStream.range(0, sample.getMeals().size()).forEach(i -> {
            final var mocked = sample.getMeals().get(i);
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
    void deleteEmptyRequest() {
        final var exception = assertThrows(BusinessException.class, () -> this.orderService.delete("").block());
        assertEquals("Id is required", exception.getMessage());
    }

    @Test
    void deleteNotFound() {
        final var exception = assertThrows(BusinessException.class, () -> this.orderService.delete("NOT_FOUND").block());
        assertEquals("Order not found", exception.getMessage());
    }

    private Order createSample() {
        final var order = this.enhancedRandom.nextObject(Order.class);
        order.setId(null);
        order.setStatus(OrderStatusType.DELIVERED);

        return this.orderRepository.insert(order).block();
    }
}

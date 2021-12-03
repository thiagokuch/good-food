package io.good.food.configuration;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.nio.charset.StandardCharsets;

@TestConfiguration
public class RandomBeanConfiguration {

    @Bean
    public EnhancedRandom enhancedRandom() {
        return newEnhancedRandom();
    }

    public static EnhancedRandom newEnhancedRandom() {
        return EnhancedRandomBuilder
                .aNewEnhancedRandomBuilder()
                .charset(StandardCharsets.UTF_8)
                .collectionSizeRange(1, 2)
                .stringLengthRange(1, 3)
                .objectPoolSize(3)
                .build();
    }
}

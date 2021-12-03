package io.good.food.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ConsumerConfiguration {

	@Bean
	public Duration consumerTimeout(@Value("${consumer.timeout:30000}") final Long timeoutEmMillisegundos) {
		return Duration.ofMillis(timeoutEmMillisegundos);
	}
	
}

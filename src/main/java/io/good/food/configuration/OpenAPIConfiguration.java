package io.good.food.configuration;

import io.good.food.Application;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfiguration {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public-api")
                .packagesToScan(Application.class.getPackageName())
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        final var description = "API to order food";
        final var info = new Info()
                .version("v1")
                .description(description)
                .title("Good Food API");

        return new OpenAPI()
                .addServersItem(new Server().url("/").description(description))
                .info(info);
    }

}

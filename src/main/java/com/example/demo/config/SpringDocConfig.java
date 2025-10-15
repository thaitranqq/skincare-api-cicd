package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;

import java.util.List;

/**
 * Configuration class for SpringDoc OpenAPI and Web-related beans.
 */
@Configuration
public class SpringDocConfig {

    @Value("${app.base-url}")
    private String baseUrl;

    /**
     * Explicitly configures the server URL for SpringDoc to ensure it works correctly behind a reverse proxy.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(new Server().url(baseUrl)))
                .info(new Info().title("LADANV API")
                        .description("API documentation for the LADANV application.")
                        .version("v1.0.0"));
    }

    /**
     * Provides a filter that processes X-Forwarded-* headers, which is crucial for applications
     * running behind a reverse proxy like Azure App Service.
     */
    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }
}

package com.example.demo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
     * Configures the OpenAPI definition, including server URL and security scheme for JWT.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "Bearer Authentication";
        return new OpenAPI()
                // Add server URL for correct link generation behind a proxy
                .servers(List.of(new Server().url(baseUrl)))
                // Add security scheme definition
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                // Apply the security scheme globally to all endpoints
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                // Add basic API info
                .info(new Info().title("LADANV API")
                        .description("API documentation for the LADANV application.")
                        .version("v1.0.0"));
    }

    /**
     * Provides a filter that processes X-Forwarded-* headers, crucial for applications
     * running behind a reverse proxy like Azure App Service.
     */
    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }
}

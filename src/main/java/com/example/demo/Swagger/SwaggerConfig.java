package com.example.demo.Swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String bearerAuth = "bearerAuth";
        final String googleOAuth = "googleOAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("LADANV API")
                        .version("1.0")
                        .description("API documentation with JWT & Google OAuth2 login"))
                // 🔒 Cho phép cả 2 loại bảo mật
                .addSecurityItem(new SecurityRequirement().addList(bearerAuth))
                .addSecurityItem(new SecurityRequirement().addList(googleOAuth))
                .components(new io.swagger.v3.oas.models.Components()
                        // 🔑 Cấu hình JWT
                        .addSecuritySchemes(bearerAuth,
                                new SecurityScheme()
                                        .name(bearerAuth)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT"))
                        // 🌐 Cấu hình Google OAuth2
                        .addSecuritySchemes(googleOAuth,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.OAUTH2)
                                        .description("Login with Google")
                                        .flows(new OAuthFlows()
                                                .authorizationCode(new OAuthFlow()
                                                        .authorizationUrl("https://accounts.google.com/o/oauth2/v2/auth")
                                                        .tokenUrl("https://oauth2.googleapis.com/token")
                                                        .scopes(new Scopes()
                                                                .addString("email", "Access email")
                                                                .addString("profile", "Access profile"))))));
    }
}

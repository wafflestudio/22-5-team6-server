package com.example.toyTeam6Airbnb

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.OAuthFlow
import io.swagger.v3.oas.models.security.OAuthFlows
import io.swagger.v3.oas.models.security.Scopes
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@OpenAPIDefinition(
    info = io.swagger.v3.oas.annotations.info.Info(
        title = "Airbnb API",
        version = "1.0"
    )
)
class SwaggerConfig : WebMvcConfigurer {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .addSecurityItem(
                SecurityRequirement()
                    .addList("Bearer Authentication")
                    .addList("Google OAuth2")
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        "Bearer Authentication",
                        SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .bearerFormat("JWT")
                            .scheme("bearer")
                    )
                    .addSecuritySchemes(
                        "Google OAuth2",
                        SecurityScheme()
                            .type(SecurityScheme.Type.OAUTH2)
                            .flows(
                                OAuthFlows()
                                    .authorizationCode(
                                        OAuthFlow()
                                            .authorizationUrl("https://accounts.google.com/o/oauth2/auth")
                                            .tokenUrl("https://oauth2.googleapis.com/token")
                                            .scopes(
                                                Scopes()
                                                    .addString("email", "email access")
                                            )
                                    )
                            )
                    )
            )
            .addServersItem(Server().url("/"))
            .info(
                Info().title("Airbnb API")
                    .description("Airbnb API Spec")
                    .version("v1.0.0")
            )
    }
}

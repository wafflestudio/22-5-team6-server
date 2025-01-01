package com.example.toyTeam6Airbnb

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.security.SecurityScheme.Type
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
                SecurityRequirement().addList("Bearer Authentication")
            )
            .components(
                Components().addSecuritySchemes
                ("Bearer Authentication", createAPIKeyScheme())
            )
            .addServersItem(Server().url("/"))
            .info(
                Info().title("WEBTOON API")
                    .description("Webtoon API Spec")
                    .version("v1.0.0")
            )
    }

    fun createAPIKeyScheme(): SecurityScheme? {
        return SecurityScheme().type(Type.HTTP)
            .bearerFormat("JWT")
            .scheme("bearer")
    }
}

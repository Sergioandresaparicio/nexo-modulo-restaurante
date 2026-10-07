package com.roles.usermanagement.web.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    public static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI userManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Gestión de usuarios y roles")
                        .version("1.0")
                        .description("API para administrar usuarios y asignar roles. "
                                + "Cada operación requiere un JWT y su permiso específico. Los permisos efectivos son los del rol más los individuales."))
                .components(new Components().addSecuritySchemes(BEARER_AUTH,
                        new SecurityScheme().type(SecurityScheme.Type.HTTP)
                                .scheme("bearer").bearerFormat("JWT")
                                .description("Introduce el token JWT sin el prefijo Bearer.")));
    }
}

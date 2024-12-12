package com.goldroad.goldroad.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@OpenAPIDefinition(
	info = @Info(
		title = "goldroead API 명세서",
		description = "goldroead API 명세서",
		version = "v1"
	)
)
@Configuration
public class SwaggerConfig {

	@Bean
	@Profile("!prod")
	public OpenAPI openAPI() {
		String jwtSchemeName = "access-token";
		SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
		Components components = new Components()
			.addSecuritySchemes(jwtSchemeName, new SecurityScheme()
				.name(jwtSchemeName)
				.type(SecurityScheme.Type.HTTP)
				.bearerFormat("Bearer"));

		return new OpenAPI()
			.addSecurityItem(securityRequirement)
			.components(components);
	}
}

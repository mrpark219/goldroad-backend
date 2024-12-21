package com.goldroad.goldroad.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;

@OpenAPIDefinition(
	info = @Info(
		title = "goldroead API 명세서",
		description = "goldroead API 명세서",
		version = "v1"
	)
)
@Configuration
public class SwaggerConfig {

	public final String ACCESS_TOKEN_HEADER;

	public final String REFRESH_TOKEN_HEADER;

	public SwaggerConfig(@Value("${jwt.access-header}") String accessTokenHeader, @Value("${jwt.refresh-header}") String refreshTokenHeader) {
		this.ACCESS_TOKEN_HEADER = accessTokenHeader;
		this.REFRESH_TOKEN_HEADER = refreshTokenHeader;
	}

	@Bean
	@Profile("!prod")
	public OpenAPI openAPI() {

		// 액세스 토큰에 대한 보안 스킴
		SecurityScheme accessTokenScheme = new SecurityScheme()
			.type(SecurityScheme.Type.APIKEY)
			.in(SecurityScheme.In.HEADER)
			.name(ACCESS_TOKEN_HEADER);

		// 리프레시 토큰에 대한 보안 스킴
		SecurityScheme refreshTokenScheme = new SecurityScheme()
			.type(SecurityScheme.Type.APIKEY)
			.in(SecurityScheme.In.HEADER)
			.name(REFRESH_TOKEN_HEADER);

		SecurityRequirement accessTokenRequirement = new SecurityRequirement().addList("accessToken");
		SecurityRequirement refreshTokenRequirement = new SecurityRequirement().addList("refreshToken");

		return new OpenAPI()
			.components(new Components()
				.addSecuritySchemes("accessToken", accessTokenScheme)
				.addSecuritySchemes("refreshToken", refreshTokenScheme))
			.security(Arrays.asList(accessTokenRequirement, refreshTokenRequirement));
	}
}

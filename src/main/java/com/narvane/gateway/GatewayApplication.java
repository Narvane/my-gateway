package com.narvane.gateway;

import com.narvane.gateway.filter.AuthenticationFilter;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpMethod;

@SpringBootApplication
@RequiredArgsConstructor
@OpenAPIDefinition(info = @Info(title = "MyRoutine Gateway", version = "1.0", description = "Documentation API Gateway v1.0"))
public class GatewayApplication {

	private final AuthenticationFilter filter;

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("login", r -> r
						.path("/login")
						.uri("no-op://localhost"))
				.route("register", r -> r
						.path("/register")
						.uri("no-op://localhost"))
				.route("my-storage-service", p -> p
						.path("/my-storage/**")
						.filters(f -> f.filter(filter))
						.uri("http://localhost:8081"))
				.route("my-storage-api-docs", p -> p
						.path("/my-storage/v3/api-docs")
						.uri("http://localhost:8081"))
				.route("my-storage-api-swg", p -> p
						.path("/my-storage/swagger-ui/index.html")
						.uri("http://localhost:8081"))
				.build();
	}

	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasenames("languages/messages");
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}

}

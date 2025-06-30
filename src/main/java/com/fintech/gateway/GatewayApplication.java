package com.fintech.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {
	@Value("${app.route.transfer}")
	private String fundTransferUrl;
	@Value("${app.route.user}")
	private String userManagementUrl;
	@Value("${app.route.identity}")
	private String identityServiceUrl;
	@Value("${app.route.bills}")
	private String billsPaymentUrl;
	@Value("${app.security.api-key}")
	private String apiKey;


	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("transfer", r -> r.path("/api/transfer/**")
						.filters(f -> f.addRequestHeader("X-API-KEY", apiKey)
								.removeRequestHeader("Cookie") // Security best practice
								.rewritePath("/api/transfer/(?<segment>.*)", "/${segment}"))
						.uri(fundTransferUrl))
				.route("usermanagement", r -> r.path("/api/customer/**")
						.filters(f -> f.addRequestHeader("X-API-KEY", apiKey)
								.removeRequestHeader("Cookie") // Security best practice
								.rewritePath("/api/customer/(?<segment>.*)", "/api/${segment}"))
						.uri(userManagementUrl))
				.route("identityservice", r -> r.path("/api/identity/**")
						.filters(f -> f.addRequestHeader("X-API-KEY", apiKey)
								.removeRequestHeader("Cookie") // Security best practice
								.rewritePath("/api/identity/(?<segment>.*)", "/${segment}"))
						.uri(identityServiceUrl))
				.route("billspayment", r -> r.path("/api/bills/**")
						.filters(f -> f.addRequestHeader("X-API-KEY", apiKey)
								.removeRequestHeader("Cookie") // Security best practice
								.rewritePath("/api/bills/(?<segment>.*)", "/${segment}"))
						.uri(billsPaymentUrl))
				.build();
	}
}

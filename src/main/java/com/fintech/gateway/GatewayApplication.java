package com.fintech.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {
	@Value("${app.route.transfer}")
	private String fundTransferUrl;
	@Value("${app.route.user}")
	private String userManagementUrl;
	@Value("${app.route.identity}")
	private String identityServiceUrl;
	@Value("${app.route.bills}")
	private String billsPaymentUrl;


	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("transfer-service", r -> r.path("/api/transfer/**")
						.filters(f -> f.addRequestHeader("X-powered-by", "buezcorp")
						)
						.uri(fundTransferUrl))
				.route("user-service", r -> r.path("/api/customer/**")
						.filters(f -> f.addRequestHeader("X-powered-by", "buezcorp")
						)
						.uri(userManagementUrl))
				.route("identity-service", r -> r.path("/api/identity/**")
						.filters(f -> f.addRequestHeader("X-powered-by", "buezcorp")
						)
						.uri(identityServiceUrl))
				.route("bills-service", r -> r.path("/api/bills/**")
						.filters(f -> f.addRequestHeader("X-powered-by", "buezcorp")
						)
						.uri(billsPaymentUrl))
				.build();
	}
}

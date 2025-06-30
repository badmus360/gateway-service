package com.fintech.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${app.security.api-key}")
    private String apiKey;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/api/**").permitAll()
                )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .build();
    }

    private WebFilter apiKeyAuthFilter() {
        return (exchange, chain) -> {
            // Skip API key check for actuator endpoints
            if (exchange.getRequest().getPath().toString().startsWith("/actuator")) {
                return chain.filter(exchange);
            }
            // Check both headers and query parameters
            String requestKey = Optional.ofNullable(
                            exchange.getRequest().getHeaders().getFirst("X-API-KEY"))
                    .orElse(exchange.getRequest().getQueryParams().getFirst("apiKey"));

            if (apiKey.equals(requestKey)) {
                return chain.filter(exchange);
            }
            return Mono.error(new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid or missing API Key"
            ));
        };
    }
}
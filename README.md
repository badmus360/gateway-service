# API Gateway Service

📌 Overview
Spring Cloud Gateway-based service for routing and filtering requests to backend microservices. It handles authentication, route forwarding, and cross-cutting concerns like rate limiting and logging.

🚀 Key Features
- Route mapping to microservices (User Management, Transfer)
- Path rewriting and URI forwarding
- API key authentication filter
- JWT token verification support
- Configurable via application.yml

🔐 Authentication Strategy
- Pre-login (onboarding, login): Uses X-API-KEY in headers
- Post-login (other routes): Uses Bearer JWT Token

Header examples:
X-API-KEY: secure-key-12345  
Authorization: Bearer your-jwt-token  

🌐 Sample Routes (from application.yml)

1. Route: /api/customer/** → usermanagement-service
   - Rewrites `/api/customer/**` to `/api/**`

2. Route: /api/transfer/** → transfer-service
   - Rewrites `/api/transfer/**` to `/api/**`

---

🛠️ Gateway Route Setup (From application.yml)

spring:
  cloud:
    gateway:
      routes:
        - id: usermanagement-service
          uri: http://localhost:8090
          predicates:
            - Path=/api/customer/**
          filters:
            - RewritePath=/api/customer/(?<segment>.*), /api/${segment}

        - id: transfer-service
          uri: http://localhost:8091
          predicates:
            - Path=/api/transfer/**
          filters:
            - RewritePath=/api/transfer/(?<segment>.*), /api/${segment}

---

🧪 RAW CURL TEST CASES (via Gateway)

# ✅ Onboard Customer (forwarded to User Management)
curl -X POST http://localhost:8080/api/customer/onboard/MOBILE \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: secure-key-12345" \
  -d '{
    "email": "user@example.com",
    "firstName": "John",
    "middleName": "Middle",
    "lastName": "Doe",
    "address": "123 Main St",
    "phoneNumber": "08012345678",
    "nin": "42937562241",
    "bvn": "59220822994",
    "password": "securePassword123"
}'

# ✅ Login Customer (forwarded to User Management)
curl -X POST http://localhost:8080/api/customer/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "securePassword123"
}'

# ✅ Get Account Info (forwarded to Transfer Service)
curl -X POST http://localhost:8080/api/transfer/dashboard/account \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-jwt-token" \
  -d '{
    "email": "user@example.com"
}'

# ✅ Name Enquiry (forwarded to Transfer Service)
curl -X POST http://localhost:8080/api/transfer/name-enquiry \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-jwt-token" \
  -d '{
    "bankCode": "058",
    "accountNumber": "0987654321"
}'

# ✅ Funds Transfer (forwarded to Transfer Service)
curl -X POST http://localhost:8080/api/transfer/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-jwt-token" \
  -d '{
    "senderAccount": "1025678901",
    "beneficiaryAccount": "0987654321",
    "beneficiaryBankCode": "058",
    "amount": 5000,
    "narration": "June salary"
}'

# ✅ Get Bank List (forwarded to Transfer Service)
curl -X GET http://localhost:8080/api/transfer/banks \
  -H "Authorization: Bearer your-jwt-token"

---

💡 Java Entry Class (GatewayApplication.java)

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}

---

🔧 application.yml Gateway Config Summary

server:
  port: 8080

app:
  secret-key: secure-key-12345

spring:
  application:
    name: apigateway
  cloud:
    gateway:
      routes:
        - id: usermanagement-service
          uri: http://localhost:8090
          predicates:
            - Path=/api/customer/**
          filters:
            - RewritePath=/api/customer/(?<segment>.*), /api/${segment}
        - id: transfer-service
          uri: http://localhost:8091
          predicates:
            - Path=/api/transfer/**
          filters:
            - RewritePath=/api/transfer/(?<segment>.*), /api/${segment}

---

📊 Notes

- Port 8080 is where the API Gateway runs
- All downstream services are protected by API key or JWT
- Gateway rewrites paths like `/api/customer/login` → `/api/login` before forwarding
- Add `global filters` for rate limiting, circuit breaker, logging if needed


# E-Commerce Microservices Project

A backend E-Commerce platform built with Java Spring Boot, PostgreSQL, and Microservices Architecture. This project demonstrates a modular, scalable, and production-ready setup for an online marketplace.

## Key Features Implemented:
- **Microservices Architecture** - Each service is isolated and independently deployable
- **JWT Security** - Secure authentication and authorization
- **API Gateway** - Single entry point with routing and security
- **Service Discovery** - Eureka for service registration and discovery
- **Database Per Service** - Each service has its own database
- **Feign Clients** - For inter-service communication
- **Event-Driven Architecture** - Using Kafka for asynchronous communication
- **Payment Integration** - Stripe integration for payment processing
- **Inventory Management** - Stock tracking with reservations
- **Notification System** - Email and in-app notifications
- **Comprehensive APIs** - RESTful APIs for all e-commerce operations
This architecture provides a scalable, maintainable, and modern e-commerce backend that can handle high traffic and complex business requirements.

## Tech Stack

- **Backend**: Spring Boot (REST APIs, Microservices)
- **Database**: PostgreSQL
- **Registery Service**: Spring Cloud Netflix Eureka
- **API Gateway**: Spring Cloud Gateway

## Repository Structure

```
ecommerce-microservices/
 ├── user-service/
 ├── product-service/
 ├── inventory-service/
 ├── cart-service/
 ├── order-service/
 ├── payment-service/
 ├── notification-service/
 ├── api-gateway/          # API Gateway
 └── registery-service/    # Eureka Server
```

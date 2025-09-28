# E-Commerce Microservices Project

A modern, scalable backend E-Commerce platform built with Java Spring Boot, PostgreSQL, and Microservices Architecture. This project showcases a modular, production-ready setup for an online marketplace, featuring comprehensive order management, payment processing, and inventory tracking.


## 🚀 Key Features

- **Microservices Architecture** - Each service is isolated and independently deployable
- **JWT Security** - Secure authentication and authorization with role-based access
- **API Gateway** - Single entry point with routing, security, and rate limiting
- **Service Discovery** - Eureka server for dynamic service registration and discovery
- **Database Per Service** - Each microservice has its own database schema
- **Feign Clients** - Declarative REST clients for inter-service communication
- **Event-Driven Architecture** - Using Kafka for asynchronous communication
- **Payment Integration** - Stripe integration for secure payment processing
- **Inventory Management** - Real-time stock tracking with a reservation system
- **Notification System** - Email and in-app notifications for order updates
- **Comprehensive APIs** - RESTful APIs covering all e-commerce operations


## 🏗️ Architecture Overview
```
┌─────────────────┐    ┌──────────────────┐    ┌──────────────────┐
│   Client        │    │   API Gateway    │    │  Service Discovery│
│   (Web/Mobile)  │──▶│   (Spring Cloud  │───▶│  (Eureka Server) │
│                 │    │     Gateway)     │    │                  │
└─────────────────┘    └──────────────────┘    └──────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
        ▼                     ▼                     ▼
┌───────────────┐     ┌───────────────┐     ┌───────────────┐
│   Auth        │     │   User        │     │   Product     │
│   Service     │     │   Service     │     │   Service     │
│               │     │               │     │               │
└───────────────┘     └───────────────┘     └───────────────┘
        │                     │                     │
        ▼                     ▼                     ▼
┌───────────────┐     ┌───────────────┐     ┌───────────────┐
│   Order       │     │   Payment     │     │   Inventory   │
│   Service     │     │   Service     │     │   Service     │
│               │     │               │     │               │
└───────────────┘     └───────────────┘     └───────────────┘
        │                     │                     │
        └─────────────────────┼─────────────────────┘
                              ▼
                    ┌──────────────────┐
                    │   Notification   │
                    │   Service        │
                    │   (Kafka Events) │
                    └──────────────────┘

```

## 📋 Repository Structure
```
ecommerce-microservices/
├── api-gateway/                 # Spring Cloud Gateway - Single entry point
├── discovery-service/           # Eureka Server - Service registry
├── auth-service/                # Authentication & JWT token management
├── user-service/                # User profiles and address management
├── product-service/             # Product catalog and categories
├── inventory-service/           # Stock management and reservations
├── order-service/               # Order processing and management
├── payment-service/             # Payment processing (Stripe integration)
└── notification-service/        # Email and notification system
```


## 🛠️ Technology Stack
- **Java 17** - Primary programming language
- **Spring Boot 3.x** - Application framework
- **Spring Cloud** - Microservices ecosystem
- **PostgreSQL** - Primary database
- **Spring Data JPA** - Database access
- **Spring Security + JWT - Authentication & Authorization**
- **Eureka Server** - Service discovery
- **Spring Cloud Gateway** - API Gateway
- **OpenFeign** - Service-to-service communication
- **Kafka** - Event-driven messaging
- **Stripe API** - Payment processing
- **Maven** - Dependency management


## 📁 Services Overview
**1. API Gateway (api-gateway/)**
- Single entry point for all client requests
- JWT authentication filter
- Request routing and load balancing
- Cross-cutting concerns (CORS, security)

**2. Discovery Service (discovery-service/)**
- Eureka server for service registration
- Health monitoring and service discovery
- Load balancing support

**3. Auth Service (auth-service/)**
- User registration and authentication
- JWT token generation and validation
- Refresh token management
- Role-based access control

**4. User Service (user-service/)**
- User profile management
- Address book functionality
- Personal information updates
- Profile picture handling

**5. Product Service (product-service/)**
- Product catalog management
- Category hierarchy
- Product search and filtering
- Reviews and ratings system

**6. Inventory Service (inventory-service/)**
- Real-time stock management
- Inventory reservations for orders
- Low stock alerts
- Stock transaction history

**7. Order Service (order-service/)**
- Order creation and management
- Order status tracking
- Order history
- Integration with inventory and payment

**8. Payment Service (payment-service/)**
- Stripe payment integration
- Payment intent creation
- Webhook handling for payment events
- Payment method management

**9. Notification Service (notification-service/)**
- Email notifications
- In-app notifications
- Event-driven messaging with Kafka
- Template-based email system

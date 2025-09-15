# E-Commerce Microservices Project

A backend E-Commerce platform built with Java Spring Boot, PostgreSQL, and Microservices Architecture. This project demonstrates a modular, scalable, and production-ready setup for an online marketplace.

## Features

- **User Service**: Authentication, authorization (JWT), user profiles, addresses
- **Product Service**: Product catalog, categories, filtering
- **Inventory Service**: Stock management, synchronization with orders
- **Cart Service**: Add/remove/update items in shopping cart
- **Order Service**: Checkout, order history, order status tracking
- **Payment Service**: Simulated payment gateway integration
- **Notification Service**: Email/SMS notifications for order updates
- **API Gateway**: Single entry point for all services
- **Service Discovery**: Dynamic service registration & discovery with Eureka
- **Database**: PostgreSQL (separate schema per service)
- **Security**: Spring Security + JWT

## Tech Stack

- **Backend**: Spring Boot (REST APIs, Microservices)
- **Database**: PostgreSQL
- **Service Discovery**: Spring Cloud Netflix Eureka
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
 ├── gateway-service/      # API Gateway
 └── discovery-service/    # Eureka Server
```

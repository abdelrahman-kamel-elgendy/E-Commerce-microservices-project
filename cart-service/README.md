# Cart Service

A robust shopping cart microservice built with Java Spring Boot that handles cart operations for both authenticated users and guest sessions.

## Features

**Cart Management:** Create, retrieve, and manage shopping carts
**Dual Support:** Support for both authenticated users and guest sessions
**Cart Merging:** Merge guest carts with user carts upon login
**Automatic Expiration:** Clean up abandoned carts automatically
**Item Operations:** Add, update, remove, and clear cart items
**Automatic Calculations:** Real-time total amount and item count calculations
**Concurrency Control:** Optimistic locking for concurrent cart updates
**Multiple Statuses:** Active, abandoned, converted, expired cart states

## API Endpoints

### Cart Operations
```
GET /api/carts/{cartId} - Get cart by ID
GET /api/carts/user/{userId} - Get user's active cart
GET /api/carts/session/{sessionId} - Get session cart
POST /api/carts - Create or get cart (with userId or sessionId params)
POST /api/carts/{cartId}/convert-to-order - Convert cart to order
POST /api/carts/{cartId}/abandon - Mark cart as abandoned
```

### Item Operations
```
POST /api/carts/{cartId}/items - Add item to cart
PUT /api/carts/{cartId}/items/{productId} - Update item quantity
DELETE /api/carts/{cartId}/items/{productId} - Remove item from cart
DELETE /api/carts/{cartId}/items - Clear all items from cart
```

## Database Schema

### carts Table
|Column   |Type         |Description|
|---------|-------------|-----------|
|id|	UUID|	Primary key|
|user_id|	BIGINT|	User identifier|
|status|	VARCHAR|	Cart status|
|total_amount|	DECIMAL|Total cart value|
|item_count|	INTEGER|	Number of items in cart|
|created_at|	TIMESTAMP|	Creation timestamp|
|updated_at|	TIMESTAMP|	Last update timestamp|

### cart_items Table
|Column   |	Type        |Description|
|---------|-------------|-----------|
|id |	UUID|	Primary key|
|cart_id|	UUID|	Foreign key to carts table|
|product_id|	BIGINT|	Product identifier|
|quantity|	INTEGER|	Item quantity|
|total_price|	DECIMAL|Total price |


## Service Structure
```
cart-service/
├── controller/      # REST API controllers
├── dtos/            # Data Transfer Objects
├── errors/       # Custom exception handling
├── model/          # JPA entities
├── repository/      # Data access layer
├── service/        # Business logic layer
└── CartServiceApplication.java
```
# Product Service

A comprehensive product catalog microservice built with Java Spring Boot that handles product management, categorization, and inventory operations.

## Features

**Product Management:** Create, retrieve, update, and delete products with full CRUD operations
**Advanced Filtering:** Filter products by category, brand, price range, and multiple criteria
**Search Capabilities:** Full-text search across product names and descriptions
**Category Hierarchy:** Support for nested categories with parent-child relationships
**Brand Management:** Complete brand management with product associations
**Image Management:** Handle product images with ordering and metadata
**Attribute System:** Flexible product attributes for detailed specifications
**Pagination & Sorting:** Efficient data retrieval with pagination and multiple sorting options
**Validation:** Comprehensive input validation and error handling
**Soft Delete:** Non-destructive deletion with active/inactive status

## API Endpoints

### Product Operations
```
GET    /api/products                   - Get all products (with pagination)
GET    /api/products/{id}              - Get product by ID
GET    /api/products/sku/{sku}         - Get product by SKU
GET    /api/products/category/{categoryId} - Get products by category
GET    /api/products/brand/{brandId}   - Get products by brand
GET    /api/products/search            - Search products by query
GET    /api/products/price-range       - Get products by price range
GET    /api/products/filter            - Filter products by multiple criteria
POST   /api/products                   - Create new product
PUT    /api/products/{id}              - Update product
DELETE /api/products/{id}              - Delete product (soft delete)
POST   /api/products/batch             - Get multiple products by IDs
GET    /api/products/category/{categoryId}/count - Count products by category
GET    /api/products/brand/{brandId}/count       - Count products by brand
```

### Category Operations
```
GET    /api/categories                 - Get all categories
GET    /api/categories/page            - Get categories with pagination
GET    /api/categories/{id}            - Get category by ID
GET    /api/categories/roots           - Get root categories (no parent)
GET    /api/categories/{parentId}/children - Get child categories
POST   /api/categories                 - Create new category
PUT    /api/categories/{id}            - Update category
DELETE /api/categories/{id}            - Delete category
GET    /api/categories/{id}/has-products - Check if category has products
```

### Brand Operations
```
GET    /api/brands                     - Get all brands
GET    /api/brands/page                - Get brands with pagination
GET    /api/brands/{id}                - Get brand by ID
POST   /api/brands                     - Create new brand
PUT    /api/brands/{id}                - Update brand
DELETE /api/brands/{id}                - Delete brand
GET    /api/brands/{id}/has-products   - Check if brand has products
```

## Database Schema

### products Table

|Column   |Type         |Description|
|---------|-------------|-----------|
|id|	BIGINT|	Primary key, auto-increment| 
|sku|	VARCHAR(255)|	Unique stock keeping unit|
|name|	VARCHAR(255)|	Product name|
|description|	TEXT|	Product description|
|price|	DECIMAL (10,2)|	Product price|
|category_id|	BIGINT|	Foreign key to categories|
|brand_id|	BIGINT|	Foreign key to brands|
|active|	BOOLEAN|	Active status flag|
|created_at|	TIMESTAMP|	Creation timestamp|
|updated_at|	TIMESTAMP|	Last update timestamp|

### categories Table

|Column   |Type         |Description|
|---------|-------------|-----------|
|id|	BIGINT|	Primary key, auto-increment|
|name|	VARCHAR|(255)	Category name|
|description|	TEXT|	Category description|
|parent_id|	BIGINT|	Self-referencing foreign key|
|active|	BOOLEAN|	Active status flag|

### brands Table
|Column   |Type         |Description|
|---------|-------------|-----------|
|id|	BIGINT|	Primary key, auto-increment|
|name|	VARCHAR(255)|	Brand name|
|description|	TEXT|	Brand description|
|logo_url|	VARCHAR(500)|	Brand logo URL|
|active|	BOOLEAN|	Active status flag|

### product_attributes Table
|Column   |Type         |Description|
|---------|-------------|-----------|
|id|	BIGINT|	Primary key, auto-increment|
|product_id|	BIGINT|	Foreign key to products|
|name|	VARCHAR(255)|	Attribute name|
|value|	VARCHAR(500)|	Attribute value|

### product_images Table
|Column   |Type         |Description|
|---------|-------------|-----------|
|id|	BIGINT|	Primary key, auto-increment|
|product_id|	BIGINT|	Foreign key to products|
|image_url|	VARCHAR(500)|	Image URL|
|alt_text|	VARCHAR(255)|	Image alt text|
|display_order|	INTEGER|	Display order position|


## Service Structure
```
product-service/
├── controller/          # REST API controllers
│   ├── ProductController.java
│   ├── CategoryController.java
│   ├── BrandController.java
├── dto/                 # Data Transfer Objects
│   ├── request/         # Request DTOs
│   └── response/        # Response DTOs
├── exception/           # Exception handling
│   ├── GlobalExceptionHandler.java
├── model/               # JPA entities
│   ├── Product.java
│   ├── Category.java
│   ├── Brand.java
│   ├── ProductAttribute.java
│   └── ProductImage.java
├── repository/          # Data access layer
│   ├── ProductRepository.java
│   ├── CategoryRepository.java
│   ├── BrandRepository.java
│   ├── ProductAttributeRepository.java
│   └── ProductImageRepository.java
├── service/             # Business logic layer
│   ├── impl/            # Service implementations
|   |── BrandService.java
|   |── CategoryService.java
|   |── ProductImageService.java
│   └── ProductService.java
└── ProductServiceApplication.java
```
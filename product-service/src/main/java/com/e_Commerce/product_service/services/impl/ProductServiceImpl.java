package com.e_Commerce.product_service.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.e_Commerce.product_service.dtos.request.ProductCreateRequest;
import com.e_Commerce.product_service.dtos.request.ProductUpdateRequest;
import com.e_Commerce.product_service.dtos.response.ProductResponse;
import com.e_Commerce.product_service.models.Brand;
import com.e_Commerce.product_service.models.Category;
import com.e_Commerce.product_service.models.Product;
import com.e_Commerce.product_service.models.ProductAttribute;
import com.e_Commerce.product_service.models.ProductImage;
import com.e_Commerce.product_service.repositories.BrandRepository;
import com.e_Commerce.product_service.repositories.CategoryRepository;
import com.e_Commerce.product_service.repositories.ProductAttributeRepository;
import com.e_Commerce.product_service.repositories.ProductImageRepository;
import com.e_Commerce.product_service.repositories.ProductRepository;
import com.e_Commerce.product_service.services.ProductService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService{
    
    @Autowired
    ProductRepository productRepository;
    
    @Autowired
    CategoryRepository categoryRepository;
    
    @Autowired
    BrandRepository brandRepository;
    
    @Autowired
    ProductAttributeRepository productAttributeRepository;
    
    @Autowired
    ProductImageRepository productImageRepository;



    @Override
    public ProductResponse createProduct(ProductCreateRequest request) {
        if (productRepository.existsBySku(request.getSku())) 
            throw new ResponseStatusException(HttpStatus.CONFLICT,  "Product with SKU " + request.getSku() + " already exists");

        Product product = new Product();
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with id: " + request.getCategoryId()));   
        product.setCategory(category);

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found with id: " + request.getBrandId()));
        product.setBrand(brand);
        
        if (request.getAttributes() != null) {
            for (ProductCreateRequest.AttributeRequest attributeRequest : request.getAttributes()) {
                ProductAttribute attribute = new ProductAttribute();
                attribute.setName(attributeRequest.getName());
                attribute.setValue(attributeRequest.getValue());
                product.addAttribute(attribute);
            }
        }

        if (request.getImages() != null) {
            for (ProductCreateRequest.ImageRequest imageRequest : request.getImages()) {
                ProductImage image = new ProductImage();
                image.setImageUrl(imageRequest.getImageUrl());
                image.setAltText(imageRequest.getAltText());
                image.setDisplayOrder(imageRequest.getDisplayOrder());
                product.addImage(image);
            }
        }
        
        return mapToProductResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Product not found with id: " + id));
        return mapToProductResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Product not found with SKU: " + sku));
        return mapToProductResponse(product);
    }

    @Override
    @Transactional
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable)
                .map(this::mapToProductResponse);
    }

    @Override
    @Transactional
    public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable)
                .map(this::mapToProductResponse);
    }

    @Override
    @Transactional
    public Page<ProductResponse> getProductsByBrand(Long brandId, Pageable pageable) {
        return productRepository.findByBrandIdAndActiveTrue(brandId, pageable)
                .map(this::mapToProductResponse);
    }

    @Override
    @Transactional
    public Page<ProductResponse> searchProducts(String query, Pageable pageable) {
        return productRepository.searchByName(query, pageable)
                .map(this::mapToProductResponse);
    }

    @Override
    public Page<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findByPriceRange(minPrice, maxPrice, pageable)
                .map(this::mapToProductResponse);
    }

    @Override
    public Page<ProductResponse> getProductsByCategoryAndPriceRange(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findByCategoryAndPriceRange(categoryId, minPrice, maxPrice, pageable)
                .map(this::mapToProductResponse);
    }

    @Override
    public Page<ProductResponse> getProductsByFilters(Long categoryId, Long brandId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findByFilters(categoryId, brandId, minPrice, maxPrice, pageable)
                .map(this::mapToProductResponse);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Product not found with id: " + id));
        
        if (request.getSku() != null && !request.getSku().equals(product.getSku())) {
            if (productRepository.existsBySkuAndIdNot(request.getSku(), id)) 
                throw new ResponseStatusException(HttpStatus.CONFLICT,"Product with SKU " + request.getSku() + " already exists");
            product.setSku(request.getSku());
        }
        
        if (request.getName() != null) 
            product.setName(request.getName());
        
        
        if (request.getDescription() != null) 
            product.setDescription(request.getDescription());
        
        
        if (request.getPrice() != null) 
            product.setPrice(request.getPrice());
        
    
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with id: " + request.getCategoryId()));
            product.setCategory(category);
        }
        
        if (request.getBrandId() != null) {
            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found with id: " + request.getBrandId()));
            product.setBrand(brand);
        }
        
        if (request.getActive() != null) 
            product.setActive(request.getActive());
        
        
        if (request.getAttributes() != null) {
            productAttributeRepository.deleteByProductId(id);

            for (ProductUpdateRequest.AttributeRequest attributeRequest : request.getAttributes()) {
                ProductAttribute attribute = new ProductAttribute();
                attribute.setName(attributeRequest.getName());
                attribute.setValue(attributeRequest.getValue());
                product.addAttribute(attribute);
            }
        }
        
        if (request.getImages() != null) {
            productImageRepository.deleteByProductId(id);
            
            for (ProductUpdateRequest.ImageRequest imageRequest : request.getImages()) {
                ProductImage image = new ProductImage();
                image.setImageUrl(imageRequest.getImageUrl());
                image.setAltText(imageRequest.getAltText());
                image.setDisplayOrder(imageRequest.getDisplayOrder());
                product.addImage(image);
            }
        }
        
        return mapToProductResponse(productRepository.save(product));
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() ->new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + id));
        productRepository.delete(product);
    }

    @Override
    @Transactional
    public List<ProductResponse> getProductsByIds(List<Long> ids) {
        return productRepository.findByIdIn(ids).stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public long countProductsByCategory(Long categoryId) {
        return productRepository.countByCategoryId(categoryId);
    }

    @Override
    @Transactional
    public long countProductsByBrand(Long brandId) {
        return productRepository.countByBrandId(brandId);
    }

    private ProductResponse mapToProductResponse(Product product) {

        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setSku(product.getSku());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setStockQuantity(0);
        response.setPrice(product.getPrice());
        response.setActive(product.isActive());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        response.setCategoryName(product.getCategory().getName());
        response.setBrandName(product.getBrand().getName());
        
        if (product.getAttributes() != null) {
            List<ProductResponse.AttributeResponse> attributeResponses = product.getAttributes().stream()
                    .map(attr -> new ProductResponse.AttributeResponse(attr.getName(), attr.getValue()))
                    .collect(Collectors.toList());
            response.setAttributes(attributeResponses);
        }
        
        if (product.getImages() != null) {
            List<ProductResponse.ImageResponse> imageResponses = product.getImages().stream()
                    .map(img -> new ProductResponse.ImageResponse(
                            img.getImageUrl(), 
                            img.getAltText(), 
                            img.getDisplayOrder()))
                    .collect(Collectors.toList());
            response.setImages(imageResponses);
        }
        
        return response;
    }
}

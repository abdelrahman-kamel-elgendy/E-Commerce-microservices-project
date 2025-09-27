package com.e_Commerce.product_service.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.e_Commerce.product_service.exception.ResourceNotFoundException;
import com.e_Commerce.product_service.exception.ApiException;

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

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProductService {

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

    public ProductResponse createProduct(ProductCreateRequest request) {
        if (productRepository.existsBySku(request.getSku()))
            throw new ApiException(HttpStatus.CONFLICT, "Product with SKU " + request.getSku() + " already exists");

        Product product = new Product();
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        product.setCategory(category);

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + request.getBrandId()));
        product.setBrand(brand);

        if (request.getAttributes() != null)
            for (ProductCreateRequest.AttributeRequest attributeRequest : request.getAttributes()) {
                ProductAttribute attribute = new ProductAttribute();
                attribute.setName(attributeRequest.getName());
                attribute.setValue(attributeRequest.getValue());
                product.addAttribute(attribute);
            }

        if (request.getImages() != null)
            for (ProductCreateRequest.ImageRequest imageRequest : request.getImages()) {
                ProductImage image = new ProductImage();
                image.setImageUrl(imageRequest.getImageUrl());
                image.setAltText(imageRequest.getAltText());
                image.setDisplayOrder(imageRequest.getDisplayOrder());
                product.addImage(image);
            }

        return mapToProductResponse(productRepository.save(product));
    }

    @Transactional
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findByActiveTrue(pageable);
        List<ProductResponse> responseList = productPage.getContent()
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responseList, productPage.getPageable(), productPage.getTotalElements());
    }

    @Transactional
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToProductResponse(product);
    }

    @Transactional
    public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        Page<Product> productPage = productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable);
        List<ProductResponse> responseList = productPage.getContent()
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responseList, productPage.getPageable(), productPage.getTotalElements());
    }

    @Transactional
    public Page<ProductResponse> getProductsByBrand(Long brandId, Pageable pageable) {
        Page<Product> productPage = productRepository.findByBrandIdAndActiveTrue(brandId, pageable);
        List<ProductResponse> responseList = productPage.getContent()
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responseList, productPage.getPageable(), productPage.getTotalElements());
    }

    @Transactional
    public Page<ProductResponse> searchProducts(String query, Pageable pageable) {
        Page<Product> productPage = productRepository.searchByName(query, pageable);
        List<ProductResponse> responseList = productPage.getContent()
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responseList, productPage.getPageable(), productPage.getTotalElements());
    }

    public Page<ProductResponse> getProductsByFilters(Long categoryId, Long brandId, BigDecimal minPrice,
            BigDecimal maxPrice, Pageable pageable) {
        Page<Product> productPage = productRepository.findByFilters(categoryId, brandId, minPrice, maxPrice, pageable);
        List<ProductResponse> responseList = productPage.getContent()
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responseList, productPage.getPageable(), productPage.getTotalElements());
    }

    public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (request.getSku() != null && !request.getSku().equals(product.getSku())) {
            if (productRepository.existsBySkuAndIdNot(request.getSku(), id))
                throw new ApiException(HttpStatus.CONFLICT, "Product with SKU " + request.getSku() + " already exists");
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
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category not found with id: " + request.getCategoryId()));
            product.setCategory(category);
        }

        if (request.getBrandId() != null) {
            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Brand not found with id: " + request.getBrandId()));
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

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setActive(false);
        productRepository.save(product);
    }

    public boolean existsBySku(String sku) {
        return productRepository.existsBySku(sku);
    }

    public List<ProductResponse> getProductByIds(List<Long> ids) {
        return productRepository.findAllById(ids)
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    private ProductResponse mapToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setSku(product.getSku());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
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

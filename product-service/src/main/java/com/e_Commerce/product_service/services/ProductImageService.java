package com.e_Commerce.product_service.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.e_Commerce.product_service.models.Product;
import com.e_Commerce.product_service.models.ProductImage;
import com.e_Commerce.product_service.repositories.ProductImageRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProductImageService {

    @Autowired
    private ProductImageRepository productImageRepository;

    public ProductImage addImageToProduct(Product product, String imageUrl, String altText, Integer displayOrder) {
        ProductImage image = new ProductImage();
        image.setImageUrl(imageUrl);
        image.setAltText(altText);
        image.setDisplayOrder(displayOrder != null ? displayOrder : getNextDisplayOrder(product.getId()));
        image.setProduct(product);
        
        return productImageRepository.save(image);
    }

    public List<ProductImage> getProductImages(Long productId) {
        return productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId);
    }

    public ProductImage getProductImage(Long productId, Long imageId) {
        return productImageRepository.findByProductIdAndId(productId, imageId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product image not found"));
    }

    public ProductImage updateProductImage(Long productId, Long imageId, String imageUrl, String altText, Integer displayOrder) {
        ProductImage image = this.getProductImage(productId, imageId);
        
        if (imageUrl != null) 
            image.setImageUrl(imageUrl);
        
        if (altText != null) 
            image.setAltText(altText);
        
        if (displayOrder != null) 
            image.setDisplayOrder(displayOrder);
        
        
        return productImageRepository.save(image);
    }

    public void deleteProductImage(Long productId, Long imageId) {
        if (!productImageRepository.existsById(imageId)) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product image not found");
        
        productImageRepository.deleteByProductIdAndImageId(productId, imageId);
    }

    private int getNextDisplayOrder(Long productId) {
        Long count = productImageRepository.countByProductId(productId);
        return count != null ? count.intValue() : 0;
    }

    public boolean imageExistsForProduct(Long productId, String imageUrl) {
        return productImageRepository.existsByProductIdAndImageUrl(productId, imageUrl);
    }
}

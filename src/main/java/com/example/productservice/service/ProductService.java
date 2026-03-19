package com.example.productservice.service;

import com.example.productservice.messaging.ProductMessagePublisher;
import com.example.productservice.model.Product;
import com.example.productservice.model.ProductNotification;
import com.example.productservice.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final ProductMessagePublisher messagePublisher;

    public ProductService(ProductRepository productRepository, ProductMessagePublisher messagePublisher) {
        this.productRepository = productRepository;
        this.messagePublisher = messagePublisher;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    @Transactional
    public Product createProduct(Product product) {
        if (productRepository.existsByName(product.getName())) {
            throw new IllegalArgumentException("Product already exists with name: " + product.getName());
        }

        Product savedProduct = productRepository.save(product);
        logger.info("Product created successfully: {}", savedProduct.getId());

        try {
            ProductNotification notification = ProductNotification.forNewProduct(savedProduct);
            messagePublisher.publishProductNotification(notification);
        } catch (Exception e) {
            logger.error("Failed to process product notification: {}", e.getMessage());
        }

        return savedProduct;
    }

    @Transactional
    public Optional<Product> updateProduct(Long id, Product productDetails) {
        return productRepository.findById(id)
                .map(existing -> {
                    existing.setName(productDetails.getName());
                    existing.setDescription(productDetails.getDescription());
                    existing.setPrice(productDetails.getPrice());
                    existing.setStock(productDetails.getStock());
                    existing.setCategory(productDetails.getCategory());
                    return productRepository.save(existing);
                });
    }

    @Transactional
    public boolean deleteProduct(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    productRepository.delete(product);
                    logger.info("Product deleted: {}", id);
                    return true;
                })
                .orElse(false);
    }
}

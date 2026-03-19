package com.example.productservice.model;

import com.example.userservice.model.EmailNotification;

import java.io.Serializable;
import java.util.Objects;

public class ProductNotification implements Serializable {

    private Long productId;
    private String productName;
    private String action;
    private String details;

    // Constructors
    public ProductNotification() {
    }

    public ProductNotification(Long productId, String productName, String action, String details) {
        this.productId = productId;
        this.productName = productName;
        this.action = action;
        this.details = details;
    }

    public EmailNotification toAdminEmailNotification(String adminEmail) {
        String subject = "Nuevo producto creado: " + productName;
        String body = String.format(
                "Se ha creado un nuevo producto en el sistema.\n\n" +
                "ID: %d\n" +
                "Nombre: %s\n" +
                "Acción: %s\n\n" +
                "Detalles: %s\n\n" +
                "Saludos,\n" +
                "Sistema de Productos",
                productId, productName, action, details);
        return new EmailNotification(adminEmail, subject, body);
    }

    public static ProductNotification forNewProduct(Product product) {
        String details = String.format(
                "Nuevo producto creado: %s | Precio: $%.2f | Stock: %d | Categoría: %s",
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getCategory() != null ? product.getCategory() : "Sin categoría");

        return new ProductNotification(product.getId(), product.getName(), "CREATED", details);
    }

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    // equals, hashCode and toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductNotification that = (ProductNotification) o;
        return Objects.equals(productId, that.productId) &&
                Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, action);
    }

    @Override
    public String toString() {
        return "ProductNotification{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", action='" + action + '\'' +
                ", details='" + details + '\'' +
                '}';
    }
}

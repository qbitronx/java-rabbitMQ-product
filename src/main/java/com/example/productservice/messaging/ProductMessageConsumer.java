package com.example.productservice.messaging;

import com.example.productservice.model.ProductNotification;
import com.example.userservice.model.EmailNotification;
import com.example.userservice.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProductMessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ProductMessageConsumer.class);

    private final EmailService emailService;

    @Value("${app.admin.email}")
    private String adminEmail;

    public ProductMessageConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "${app.rabbitmq.product.queue}")
    public void receiveProductNotification(ProductNotification notification) {
        logger.info("Received product notification: {}", notification);
        try {
            logger.info("Processing product event - Action: {} | Product: {} (ID: {})",
                    notification.getAction(),
                    notification.getProductName(),
                    notification.getProductId());
            logger.info("Details: {}", notification.getDetails());

            EmailNotification email = notification.toAdminEmailNotification(adminEmail);
            emailService.sendEmail(email);
            logger.info("Admin notification email sent to: {}", adminEmail);
        } catch (Exception e) {
            logger.error("Failed to process product notification: {}", e.getMessage(), e);
        }
    }
}

package com.example.productservice.messaging;

import com.example.productservice.model.ProductNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ProductMessagePublisher {

    private static final Logger logger = LoggerFactory.getLogger(ProductMessagePublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private boolean rabbitMQAvailable = true;

    @Value("${app.rabbitmq.product.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.product.routingkey}")
    private String routingKey;

    public ProductMessagePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        try {
            rabbitTemplate.execute(channel -> null);
            logger.info("RabbitMQ connection successful (ProductMessagePublisher)");
        } catch (AmqpConnectException e) {
            logger.warn("RabbitMQ connection failed: {}. Product notifications will be logged only.", e.getMessage());
            rabbitMQAvailable = false;
        }
    }

    public void publishProductNotification(ProductNotification notification) {
        try {
            if (rabbitMQAvailable) {
                logger.info("Sending product notification to queue: {} [{}]", notification.getProductName(), notification.getAction());
                rabbitTemplate.convertAndSend(exchange, routingKey, notification);
                logger.info("Product notification sent successfully to queue");
            } else {
                logger.info("RabbitMQ not available. Would have sent product notification: {}", notification);
            }
        } catch (Exception e) {
            logger.error("Error sending product notification: {}", e.getMessage(), e);
            logger.info("Product notification would have been sent: {}", notification);
        }
    }
}

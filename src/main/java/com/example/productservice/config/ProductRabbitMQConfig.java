package com.example.productservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductRabbitMQConfig {

    @Value("${app.rabbitmq.product.queue}")
    private String productQueueName;

    @Value("${app.rabbitmq.product.exchange}")
    private String productExchange;

    @Value("${app.rabbitmq.product.routingkey}")
    private String productRoutingKey;

    @Bean
    Queue productQueue() {
        return new Queue(productQueueName, true);
    }

    @Bean
    TopicExchange productExchange() {
        return new TopicExchange(productExchange);
    }

    @Bean
    Binding productBinding(Queue productQueue, TopicExchange productExchange) {
        return BindingBuilder.bind(productQueue).to(productExchange).with(productRoutingKey);
    }
}

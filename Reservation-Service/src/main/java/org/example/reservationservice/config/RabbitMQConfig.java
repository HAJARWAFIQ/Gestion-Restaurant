package org.example.reservationservice.config;

import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;

import java.util.Queue;

public class RabbitMQConfig {
    // Définir une queue RabbitMQ pour envoyer les notifications
    @Bean
    public Queue notificationQueue() {
        return (Queue) QueueBuilder.durable("notificationQueue")  // Rendre la queue persistante
                .build();
    }
}

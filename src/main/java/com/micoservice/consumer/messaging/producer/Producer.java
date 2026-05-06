package com.micoservice.consumer.messaging.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Producer {
    // injetar o nome da fila do application.properties
    @Value("${broker.queue.processamento.name}")
    private String routingKey;
    // injetar o RabbitTemplate para enviar mensagens para a fila
    private final RabbitTemplate rabbitTemplate;

    public Producer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(Object request) {
        // envia a msg para a fila, o primeiro parametro é o nome da fila e o segundo
        // parametro é a msg/objeto que vai ser enviada
        rabbitTemplate.convertAndSend(routingKey, request);
    }
}

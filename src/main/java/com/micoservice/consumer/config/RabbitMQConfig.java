package com.micoservice.consumer.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// classe de config do RabbitMQ, onde é criado a fila e o conversor de mensagens
// Resumo do que é @Configuration & @Bean: São anotações do Spring Framework usadas para configurar e gerenciar beans (objetos) dentro do contexto da aplicação. @Configuration indica que a classe contém definições de beans, enquanto @Bean é usada para marcar métodos que retornam objetos que devem ser gerenciados pelo Spring, permitindo a injeção de dependências e a configuração centralizada dos componentes da aplicação.
@Configuration
public class RabbitMQConfig {

    // pegando o endereço/nome da sua fila do application.properties
    @Value("${broker.queue.processamento.name}")
    private String queue;

    @Bean
    public Queue queue() {
        // criando a fila, o segundo parametro é para dizer que a fila é durável, ou
        // seja, ela vai sobreviver mesmo que o RabbitMQ seja reiniciado
        return new Queue(queue, true);
    }

    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

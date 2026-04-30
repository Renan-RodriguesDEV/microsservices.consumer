package com.micoservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tools.jackson.databind.json.JsonMapper;

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
    public JacksonJsonMessageConverter jsonMessageConverter() {
        // criando o conversor de mensagens, para converter os objetos em JSON e
        // vice-versa, para isso é necessário adicionar a dependência do Jackson no
        // pom.xml
        JsonMapper mapper = new JsonMapper();
        return new JacksonJsonMessageConverter(mapper);
    }
}

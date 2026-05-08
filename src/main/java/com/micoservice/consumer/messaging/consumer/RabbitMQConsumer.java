package com.micoservice.consumer.messaging.consumer;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

// componente que vai consumir as mensagens da fila do RabbitMQ, ele vai ser responsável por receber as mensagens e processá-las, nesse caso, ele vai salvar os clientes no banco de dados, mas poderia ser qualquer outra coisa, como enviar um email, processar um pagamento, etc.
@Component
public class RabbitMQConsumer {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${api.email.url}")
    private String apiEmailUrl;

    @RabbitListener(queues = "${broker.queue.processamento.name}")
    public void listenerQueue(Map<String, String> message) {
        // notificar por e-mail
        sendWithEmail(message);
    }

    public boolean sendWithEmail(Map<String, String> message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("to", message.get("email"));
        body.put("message", message.get("message"));
        body.put("from", message.get("from"));
        restTemplate.postForObject(apiEmailUrl, body, Object.class);
        return true;
    }

}

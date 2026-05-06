package com.micoservice.consumer.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.micoservice.consumer.model.Pedido;

// componente que vai consumir as mensagens da fila do RabbitMQ, ele vai ser responsável por receber as mensagens e processá-las, nesse caso, ele vai salvar os clientes no banco de dados, mas poderia ser qualquer outra coisa, como enviar um email, processar um pagamento, etc.
@Component
public class RabbitMQConsumer {

    @RabbitListener(queues = "${broker.queue.processamento.name}")
    public void listenerQueue(Pedido pedido) {
        System.out.println("Mensagem recebida: " + pedido.getDescricao());

    }


}

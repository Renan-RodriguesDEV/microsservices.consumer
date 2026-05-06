package com.micoservice.consumer.controllers;

import com.micoservice.consumer.dto.ClienteDTO;
import com.micoservice.consumer.model.Cliente;
import com.micoservice.consumer.security.TokenService;
import com.micoservice.consumer.services.ClienteService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("clientes")
public class ClienteControler {
    private final ClienteService clienteService;
    // injeção do RabbitTemplate para enviar mensagens para o RabbitMQ
    private final RabbitTemplate rabbitTemplate;

    @Value("${broker.queue.processamento.name}")
    private String routingKey;

    public ClienteControler(ClienteService clienteService, RabbitTemplate rabbitTemplate,
            AuthenticationManager authenticationManager, TokenService tokenService) {
        this.clienteService = clienteService;
        this.rabbitTemplate = rabbitTemplate;

    }

    @GetMapping("{id}")
    public Cliente get(@PathVariable Long id) {

        return clienteService.findById(id);
    }

    @GetMapping
    public List<Cliente> get() {

        rabbitTemplate.convertAndSend("", routingKey, "Alguem chamou");
        return clienteService.findAll();
    }

    @PutMapping("{id}")
    public Cliente put(Long id, ClienteDTO cliente) {
        return clienteService.update(id, cliente);
    }

    @DeleteMapping("{id}")
    public void delete(Long id) {
        clienteService.deleteById(id);
    }

}

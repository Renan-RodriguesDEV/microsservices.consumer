package com.micoservice.consumer.controllers;

import com.micoservice.consumer.dto.ClienteDTO;
import com.micoservice.consumer.model.Cliente;
import com.micoservice.consumer.services.ClienteService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("clientes")
public class ClienteControler {
    private final ClienteService clienteService;
    private final RabbitTemplate rabbitTemplate;
    @Value("${broker.queue.processamento.name}")
    private String routingKey;

    public ClienteControler(ClienteService clienteService, RabbitTemplate rabbitTemplate) {
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

    @PostMapping
    public Cliente post(ClienteDTO cliente) {
        Cliente cliente_db = clienteService.create(cliente);
        rabbitTemplate.convertAndSend("", routingKey, cliente.nome());
        return cliente_db;

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

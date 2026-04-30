package com.micoservice.consumer.controllers;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.micoservice.consumer.model.Cliente;
import com.micoservice.consumer.services.ClienteService;

@RestController
@RequestMapping("/clientes")
public class ClienteControler {
    private final ClienteService clienteService;
    private final RabbitTemplate rabbitTemplate;
    @Value("${broker.queue.processamento.name}")
    private String routingKey;

    public ClienteControler(ClienteService clienteService, RabbitTemplate rabbitTemplate) {
        this.clienteService = clienteService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping("/{id}")
    public Cliente get(@PathVariable Long id) {
        return clienteService.findById(id);
    }

    @GetMapping
    public List<Cliente> get() {
        return clienteService.findAll();
    }

    @PostMapping
    public Cliente post(Cliente cliente) {
        Cliente cliente_db = clienteService.create(cliente);
        rabbitTemplate.convertAndSend("", routingKey, cliente);
        return cliente_db;

    }

    @PutMapping("/{id}")
    public Cliente put(Long id, Cliente cliente) {
        return clienteService.update(id, cliente);
    }

    @DeleteMapping("/{id}")
    public void delete(Long id) {
        clienteService.deleteById(id);
    }

}

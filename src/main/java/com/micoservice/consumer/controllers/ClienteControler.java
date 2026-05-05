package com.micoservice.consumer.controllers;

import com.micoservice.consumer.dto.ClienteDTO;
import com.micoservice.consumer.dto.ClienteLoginDTO;
import com.micoservice.consumer.model.Cliente;
import com.micoservice.consumer.security.TokenService;
import com.micoservice.consumer.services.ClienteService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("clientes")
public class ClienteControler {
    private final ClienteService clienteService;
    // injeção do RabbitTemplate para enviar mensagens para o RabbitMQ
    private final RabbitTemplate rabbitTemplate;
    // injeção do AuthenticationManager para autenticar os clientes no endpoint de
    // login
    private final AuthenticationManager authenticationManager;
    // injeção do TokenService para gerar tokens JWT
    private final TokenService tokenService;
    @Value("${broker.queue.processamento.name}")
    private String routingKey;

    public ClienteControler(ClienteService clienteService, RabbitTemplate rabbitTemplate,
            AuthenticationManager authenticationManager, TokenService tokenService) {
        this.clienteService = clienteService;
        this.rabbitTemplate = rabbitTemplate;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
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

    @PostMapping("login")
    public ResponseEntity<ClienteLoginDTO> login(@RequestBody ClienteLoginDTO cliente) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                cliente.name(),
                cliente.password());
        authenticationManager.authenticate(authenticationToken);
        String token = tokenService.generateToken(cliente.name());
        return ResponseEntity.ok().header("Authorization", token).build();
    }

}

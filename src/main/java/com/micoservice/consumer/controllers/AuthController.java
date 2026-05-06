package com.micoservice.consumer.controllers;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.micoservice.consumer.dto.ClienteDTO;
import com.micoservice.consumer.dto.ClienteLoginDTO;
import com.micoservice.consumer.model.Cliente;
import com.micoservice.consumer.security.TokenService;
import com.micoservice.consumer.services.ClienteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("auth")
public class AuthController {
    private final ClienteService clienteService;
    // injeção do AuthenticationManager para autenticar os clientes no endpoint de
    // login
    private final AuthenticationManager authenticationManager;
    // injeção do TokenService para gerar tokens JWT
    private final TokenService tokenService;
    // injeção do RabbitTemplate para enviar mensagens para o RabbitMQ
    private final RabbitTemplate rabbitTemplate;

    @Value("${broker.queue.processamento.name}")
    private String routingKey;

    public AuthController(ClienteService clienteService, AuthenticationManager authenticationManager,
            TokenService tokenService, RabbitTemplate rabbitTemplate) {
        this.clienteService = clienteService;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("login")
    public ResponseEntity login(@Valid @RequestBody ClienteLoginDTO cliente) {
        System.out.println("Chamando rota de login");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                cliente.name(),
                cliente.password());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        String token = tokenService.generateToken(cliente.name());
        System.out.println("Token gerado com sucesso!!");
        return ResponseEntity.ok().header("Authorization", token).build();
    }

    @PostMapping("register")
    public ResponseEntity<Cliente> register(@Valid @RequestBody ClienteDTO cliente) {

        Cliente cliente_db = clienteService.create(cliente);
        rabbitTemplate.convertAndSend("", routingKey, cliente.nome());
        return ResponseEntity.ok(cliente_db);
    }
}

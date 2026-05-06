package com.micoservice.consumer.services;

import java.util.List;

import com.micoservice.consumer.dto.ClienteDTO;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.micoservice.consumer.model.Cliente;
import com.micoservice.consumer.repositories.ClienteRepository;

@Service
public class ClienteService {
    private final ClienteRepository clienteReporitory;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(ClienteRepository clienteReporitory, PasswordEncoder passwordEncoder) {
        this.clienteReporitory = clienteReporitory;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Cliente> findAll() {
        return clienteReporitory.findAll();
    }

    public Cliente findById(Long id) {
        return clienteReporitory.findById(id).orElse(null);
    }

    public Cliente create(ClienteDTO cliente) {
        // salva o cliente no banco de dados, usando o nome e a senha do DTO, mas antes
        // de salvar, é necessário criptografar a senha usando o BCryptPasswordEncoder
        return clienteReporitory.save(new Cliente(cliente.nome(), passwordEncoder.encode(cliente.password())));
    }

    public Cliente update(Long id, ClienteDTO cliente) {
        Cliente cliente_exists = this.findById(id);
        if (cliente_exists == null) {
            return null;
        }
        cliente_exists.setName(cliente.nome());
        return clienteReporitory.save(cliente_exists);
    }

    public void deleteById(Long id) {
        Cliente cliente = this.findById(id);
        if (cliente != null) {
            clienteReporitory.delete(cliente);
        }
    }
}

package com.micoservice.consumer.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.micoservice.consumer.model.Cliente;
import com.micoservice.consumer.repositories.ClienteRepository;

@Service
public class ClienteService {
    private final ClienteRepository clienteReporitory;

    public ClienteService(ClienteRepository clienteReporitory) {
        this.clienteReporitory = clienteReporitory;
    }

    public List<Cliente> findAll() {
        return clienteReporitory.findAll();
    }

    public Cliente findById(Long id) {
        return clienteReporitory.findById(id).orElse(null);
    }

    public Cliente create(Cliente cliente) {
        return clienteReporitory.save(cliente);
    }

    public Cliente update(Long id, Cliente cliente) {
        Cliente cliente_exists = this.findById(id);
        if (cliente != null) {
            cliente_exists.setName(cliente.getName());
        }
        return clienteReporitory.save(cliente);
    }

    public void deleteById(Long id) {
        Cliente cliente = this.findById(id);
        if (cliente != null) {
            clienteReporitory.delete(cliente);
        }
    }
}

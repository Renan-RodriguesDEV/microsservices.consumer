package com.micoservice.consumer.domain.services;

import com.micoservice.consumer.domain.dto.requests.ContaDTO;
import com.micoservice.consumer.domain.model.Conta;
import com.micoservice.consumer.domain.model.User;
import com.micoservice.consumer.domain.repositories.ContaRepository;
import com.micoservice.consumer.domain.repositories.UserRepository;
import com.micoservice.consumer.exceptions.ResourceNotFound;
import com.micoservice.consumer.exceptions.UnauthorizedException;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContaService {
    private final ContaRepository contaRepository;
    private final UserRepository userRepository;

    public ContaService(ContaRepository contaRepository, UserRepository userRepository) {
        this.contaRepository = contaRepository;
        this.userRepository = userRepository;
    }

    public Conta findById(Long id) {
        return contaRepository.findById(id).orElse(null);
    }

    public Conta findByUser(String username) {
        User user = userRepository.findByUsername(username);
        return contaRepository.findByUser(user);
    }

    public List<Conta> findAll() {
        return contaRepository.findAll();
    }

    public Conta create(ContaDTO data) {
        Conta conta = new Conta();
        conta.setSaldo(data.saldo());
        return contaRepository.save(conta);
    }

    public Conta update(Long id, ContaDTO data) {
        Conta conta = this.findById(id);
        if (conta == null)
            throw new ResourceNotFound("Conta não encontrada");
        conta.setSaldo(data.saldo());
        return contaRepository.save(conta);
    }

    public void deleteById(Long id) {
        Conta conta = this.findById(id);
        if (conta == null)
            throw new ResourceNotFound("Conta não encontrada");
        if (conta.getSaldo() < 0) {
            throw new UnauthorizedException("Conta com saldo negativo, não pode ser deletada");
        }
        contaRepository.delete(conta);
    }
}

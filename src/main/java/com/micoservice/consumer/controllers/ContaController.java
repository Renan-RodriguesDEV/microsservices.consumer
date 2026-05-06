package com.micoservice.consumer.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.micoservice.consumer.domain.dto.requests.ContaDTO;
import com.micoservice.consumer.domain.dto.responses.ContaResponseDTO;
import com.micoservice.consumer.domain.dto.responses.UserResponseDTO;
import com.micoservice.consumer.domain.model.Conta;
import com.micoservice.consumer.domain.services.ContaService;

@RestController
@RequestMapping("/contas")
public class ContaController {
    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaResponseDTO> get(@PathVariable Long id) {
        Conta conta = contaService.findById(id);
        UserResponseDTO user = new UserResponseDTO(conta.getUser().getId(), conta.getUser().getUsername(),
                conta.getUser().getCreatedAt());
        ContaResponseDTO response = new ContaResponseDTO(conta.getId(), conta.getSaldo(), user);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ContaResponseDTO>> get() {
        List<Conta> contas = contaService.findAll();
        List<ContaResponseDTO> responses = contas.stream()
                .map(c -> new ContaResponseDTO(
                        c.getId(),
                        c.getSaldo(),
                        new UserResponseDTO(c.getUser().getId(), c.getUser().getUsername(),
                                c.getUser().getCreatedAt())))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContaResponseDTO> put(@PathVariable Long id, @RequestBody ContaDTO conta) {
        Conta updated = contaService.update(id, conta);
        UserResponseDTO user = new UserResponseDTO(updated.getUser().getId(), updated.getUser().getUsername(),
                updated.getUser().getCreatedAt());
        ContaResponseDTO response = new ContaResponseDTO(updated.getId(), updated.getSaldo(), user);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ContaResponseDTO> post(@RequestBody ContaDTO conta) {
        Conta createdConta = contaService.create(conta);
        UserResponseDTO user;
        if (createdConta.getUser() != null) {

            user = new UserResponseDTO(createdConta.getUser().getId(), createdConta.getUser().getUsername(),
                    createdConta.getUser().getCreatedAt());
        } else {
            user = null;
        }
        ContaResponseDTO response = new ContaResponseDTO(createdConta.getId(), createdConta.getSaldo(), user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

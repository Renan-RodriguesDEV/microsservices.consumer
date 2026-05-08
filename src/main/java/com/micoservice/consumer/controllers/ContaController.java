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

        ContaResponseDTO contaResponseDTO = ContaResponseDTO.fromEntity(conta);
        return ResponseEntity.ok(contaResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<ContaResponseDTO>> get() {
        List<Conta> contas = contaService.findAll();
        List<ContaResponseDTO> responses = List.of();
        for (Conta conta : contas) {
            responses.add(ContaResponseDTO.fromEntity(conta));
        }
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContaResponseDTO> put(@PathVariable Long id, @RequestBody ContaDTO conta) {
        Conta updated = contaService.update(id, conta);

        ContaResponseDTO response = ContaResponseDTO.fromEntity(updated);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}")
    public ResponseEntity<ContaResponseDTO> post(@PathVariable Long id, @RequestBody ContaDTO conta) {
        Conta createdConta = contaService.create(conta, id);

        ContaResponseDTO response = ContaResponseDTO.fromEntity(createdConta);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

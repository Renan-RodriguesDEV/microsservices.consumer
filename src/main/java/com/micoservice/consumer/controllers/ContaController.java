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
    public ResponseEntity<Conta> get(@PathVariable Long id) {
        return ResponseEntity.ok(contaService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<Conta>> get() {
        return ResponseEntity.ok(contaService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Conta> put(@PathVariable Long id, @RequestBody ContaDTO conta) {
        return ResponseEntity.ok(contaService.update(id, conta));
    }

    @PostMapping
    public ResponseEntity<Conta> post(@RequestBody ContaDTO conta) {
        Conta createdConta = contaService.create(conta);
        return ResponseEntity.ok(createdConta);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        contaService.deleteById(id);
    }
}

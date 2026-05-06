package com.micoservice.consumer.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.micoservice.consumer.domain.dto.requests.TransacaoDTO;
import com.micoservice.consumer.domain.dto.responses.TransacaoResponseDTO;
import com.micoservice.consumer.domain.services.TransacaoService;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {
    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @PostMapping
    public ResponseEntity<TransacaoResponseDTO> transferir(@RequestBody TransacaoDTO transacao) {
        return ResponseEntity.ok(transacaoService.tranferir(transacao));
    }

    // @GetMapping()
    // public List<TransacaoResponseDTO> listarTransacoes() {
    // return transacaoService.listarTransacoes();
    // }
}

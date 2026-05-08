package com.micoservice.consumer.domain.dto.responses;

import com.micoservice.consumer.domain.dto.enums.TipoTransacao;
import com.micoservice.consumer.domain.model.Transacao;

public record TransacaoResponseDTO(
        Long id, Double valor, TipoTransacao tipoTransacao, ContaResponseDTO origem, ContaResponseDTO destino) {
    public static TransacaoResponseDTO fromEntity(Transacao t) {
        ContaResponseDTO origem = ContaResponseDTO.fromEntity(t.getOrigem());
        ContaResponseDTO destino = ContaResponseDTO.fromEntity(t.getDestino());
        TransacaoResponseDTO response = new TransacaoResponseDTO(t.getId(), t.getValor(), t.getTipo(), origem, destino);
        return response;

    }
}

package com.micoservice.consumer.domain.dto.responses;

import com.micoservice.consumer.domain.dto.enums.TipoTransacao;

public record TransacaoResponseDTO(
        ContaResponseDTO origem, ContaResponseDTO destino, Double valor, TipoTransacao tipoTransacao) {
    
}

package com.micoservice.consumer.domain.dto.responses;

import com.micoservice.consumer.domain.dto.enums.TipoTransacao;
import com.micoservice.consumer.domain.model.Conta;

public record TransacaoResponseDTO(
                Long id, Double valor, TipoTransacao tipoTransacao, Conta origem, Conta destino) {

}

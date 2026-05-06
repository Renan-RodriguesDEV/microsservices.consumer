package com.micoservice.consumer.domain.dto.requests;

import com.micoservice.consumer.domain.dto.enums.TipoTransacao;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransacaoDTO(@NotNull Long idOrigem, @NotNull Long idDestino,
        @NotNull @Positive(message = "O valor não pode ser negativo") Double valor,
        @NotNull(message = "O tipo de transação é obrigatório") TipoTransacao tipoTransacao) {

}

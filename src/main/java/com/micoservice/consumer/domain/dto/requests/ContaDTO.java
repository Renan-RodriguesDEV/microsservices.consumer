package com.micoservice.consumer.domain.dto.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ContaDTO(@NotNull @Positive(message = "O saldo não pode ser negativo") Double saldo) {
}

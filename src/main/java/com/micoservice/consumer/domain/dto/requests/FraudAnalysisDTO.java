package com.micoservice.consumer.domain.dto.requests;

import com.micoservice.consumer.domain.dto.enums.FraudType;

import jakarta.validation.constraints.NotNull;

public record FraudAnalysisDTO(@NotNull Long transferencyId, @NotNull FraudType tipo) {
}

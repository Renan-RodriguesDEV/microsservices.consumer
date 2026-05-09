package com.micoservice.consumer.domain.dto.responses;

import com.micoservice.consumer.domain.model.Conta;

import java.time.LocalDateTime;

public record ContaResponseDTO(Long id, Double saldo, LocalDateTime createdAt, LocalDateTime updatedAt, UserResponseDTO user) {
    public static ContaResponseDTO fromEntity(Conta conta) {
        UserResponseDTO user = new UserResponseDTO(conta.getUser().getId(), conta.getUser().getUsername(),
                conta.getUser().getCreatedAt(), conta.getUser().getUpdatedAt(), conta.getUser().getRole(),
                conta.getId());
        ContaResponseDTO response = new ContaResponseDTO(conta.getId(), conta.getSaldo(), conta.getCreatedAt(),
                conta.getUpdatedAt(), user);
        return response;
    }
}

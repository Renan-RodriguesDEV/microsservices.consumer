package com.micoservice.consumer.domain.dto.responses;

import com.micoservice.consumer.domain.dto.enums.RoleEnum;
import com.micoservice.consumer.domain.model.User;

import java.time.LocalDateTime;

public record UserResponseDTO(Long id, String username, LocalDateTime createdAt, LocalDateTime updatedAt, RoleEnum role,
                              Long contaId) {
    public static UserResponseDTO fromEntity(User user) {
        UserResponseDTO response = new UserResponseDTO(user.getId(), user.getUsername(), user.getCreatedAt(),
                user.getUpdatedAt(), user.getRole(), user.getConta().getId());
        return response;
    }
}

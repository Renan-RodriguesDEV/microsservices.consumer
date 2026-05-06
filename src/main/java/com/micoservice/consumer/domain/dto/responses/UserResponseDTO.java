package com.micoservice.consumer.domain.dto.responses;

import java.time.LocalDate;

public record UserResponseDTO(Long id, String username, LocalDate createdAt) {
}

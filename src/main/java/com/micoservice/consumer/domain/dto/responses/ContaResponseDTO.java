package com.micoservice.consumer.domain.dto.responses;

import com.micoservice.consumer.domain.model.User;

public record ContaResponseDTO(Long id, Double saldo, User user) {

}

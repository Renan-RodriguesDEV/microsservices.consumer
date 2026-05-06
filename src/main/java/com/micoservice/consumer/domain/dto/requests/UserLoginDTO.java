package com.micoservice.consumer.domain.dto.requests;

import jakarta.validation.constraints.NotNull;

public record UserLoginDTO(@NotNull(message = "O nome de usuário é obrigatório") String username,
        @NotNull(message = "A senha é obrigatória") String password) {

}

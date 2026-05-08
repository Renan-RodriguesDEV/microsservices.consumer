package com.micoservice.consumer.domain.dto.requests;

import com.micoservice.consumer.domain.dto.enums.RoleEnum;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

public record UserLoginDTO(@NotNull(message = "O nome de usuário é obrigatório") String username,
                @NotNull(message = "A senha é obrigatória") String password, @Null RoleEnum role) {
}

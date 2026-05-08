package com.micoservice.consumer.domain.dto.requests;

import com.micoservice.consumer.domain.dto.enums.RoleEnum;
import com.micoservice.consumer.domain.model.User;

public record UserDTO(String username, String password, RoleEnum role) {
    public User toEntity() {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        return user;
    }
}

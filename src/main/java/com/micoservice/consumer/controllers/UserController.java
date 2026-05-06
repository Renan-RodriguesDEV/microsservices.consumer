package com.micoservice.consumer.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.micoservice.consumer.domain.dto.requests.UserLoginDTO;
import com.micoservice.consumer.domain.dto.responses.UserResponseDTO;
import com.micoservice.consumer.domain.model.User;
import com.micoservice.consumer.domain.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService clienteService;

    public UserController(UserService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> get(@PathVariable Long id) {
        User user = clienteService.findById(id);
        UserResponseDTO response = new UserResponseDTO(user.getId(), user.getUsername(), user.getCreatedAt());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> get() {
        List<User> users = clienteService.findAll();
        List<UserResponseDTO> responses = users.stream()
                .map(u -> new UserResponseDTO(u.getId(), u.getUsername(), u.getCreatedAt()))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> put(@PathVariable Long id, @Valid @RequestBody UserLoginDTO cliente) {
        User user = clienteService.update(id, cliente);
        UserResponseDTO response = new UserResponseDTO(user.getId(), user.getUsername(), user.getCreatedAt());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clienteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}

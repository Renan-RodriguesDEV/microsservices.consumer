package com.micoservice.consumer.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.micoservice.consumer.domain.dto.requests.UserDTO;
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
        UserResponseDTO response = UserResponseDTO.fromEntity(user);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> get() {
        List<User> users = clienteService.findAll();
        List<UserResponseDTO> responses = List.of();
        for (User user : users) {
            responses.add(UserResponseDTO.fromEntity(user));
        }
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}")
    public ResponseEntity<UserResponseDTO> post(@Valid @RequestBody UserDTO cliente) {
        User user = cliente.toEntity();
        User userCreated = clienteService.create(user);
        UserResponseDTO response = UserResponseDTO.fromEntity(userCreated);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> put(@PathVariable Long id, @Valid @RequestBody UserLoginDTO cliente) {
        User user = clienteService.update(id, cliente);
        UserResponseDTO response = UserResponseDTO.fromEntity(user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clienteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}

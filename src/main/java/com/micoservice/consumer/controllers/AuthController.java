package com.micoservice.consumer.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.micoservice.consumer.domain.dto.requests.UserLoginDTO;
import com.micoservice.consumer.domain.dto.responses.UserResponseDTO;
import com.micoservice.consumer.domain.model.User;
import com.micoservice.consumer.domain.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<String> get() {
        return ResponseEntity.ok("Rota de autenticação funcionando!!");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserLoginDTO user) {
        String token = userService.login(user);
        // retornar o token no header da resposta, para que o cliente possa usar esse
        // token
        return ResponseEntity.ok().header("Authorization", token).build();
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserLoginDTO data) {

        User user = userService.register(data);
        UserResponseDTO response = new UserResponseDTO(user.getId(), user.getUsername(), user.getCreatedAt());
        return ResponseEntity.ok(response);
    }
}

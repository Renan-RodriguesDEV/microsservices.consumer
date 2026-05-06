package com.micoservice.consumer.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.micoservice.consumer.domain.dto.requests.UserLoginDTO;
import com.micoservice.consumer.domain.model.User;
import com.micoservice.consumer.domain.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService clienteService;

    public UserController(UserService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/{id}")
    public User get(@PathVariable Long id) {

        return clienteService.findById(id);
    }

    @GetMapping
    public List<User> get() {
        return clienteService.findAll();
    }

    @PutMapping("/{id}")
    public User put(@PathVariable Long id, @RequestBody UserLoginDTO cliente) {
        return clienteService.update(id, cliente);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        clienteService.deleteById(id);
    }

}

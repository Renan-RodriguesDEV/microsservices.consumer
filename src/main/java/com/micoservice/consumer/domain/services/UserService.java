package com.micoservice.consumer.domain.services;

import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.micoservice.consumer.domain.dto.requests.UserLoginDTO;
import com.micoservice.consumer.domain.model.User;
import com.micoservice.consumer.domain.repositories.UserRepository;
import com.micoservice.consumer.exceptions.AlreadyExists;
import com.micoservice.consumer.exceptions.ResourceNotFound;
import com.micoservice.consumer.security.TokenService;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    // injeção do AuthenticationManager para autenticar os clientes no endpoint de
    // login
    private final AuthenticationManager authenticationManager;
    // injeção do TokenService para gerar tokens JWT
    private final TokenService tokenService;

    public UserService(UserRepository userRepository,
            PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User register(UserLoginDTO data) {
        if (userRepository.findByUsername(data.username()) != null) {
            throw new AlreadyExists("Usuario já existe");
        }
        User user = new User();
        user.setUsername(data.username());
        user.setPassword(passwordEncoder.encode(data.password()));
        user.setRole(data.role());

        // Salvar User PRIMEIRO
        user = userRepository.save(user);

        return user;
    }

    public User create(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new AlreadyExists("Usuario já existe");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User update(Long id, UserLoginDTO data) {
        User user = this.findById(id);
        if (user == null) {
            throw new ResourceNotFound("Usuario não encontrado");
        }
        user.setUsername(data.username());
        user.setPassword(passwordEncoder.encode(data.password()));
        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        User user = this.findById(id);
        if (user != null) {
            userRepository.delete(user);
        }
    }

    public String login(UserLoginDTO usuario) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                usuario.username(),
                usuario.password());
        authenticationManager.authenticate(authenticationToken);
        String token = tokenService.generateToken(usuario.username());
        return token;
    }
}

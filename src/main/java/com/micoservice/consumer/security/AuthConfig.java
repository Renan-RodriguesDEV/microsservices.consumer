package com.micoservice.consumer.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.micoservice.consumer.domain.repositories.UserRepository;

@Service
public class AuthConfig implements UserDetailsService {

    public final UserRepository clienteRepository;

    public AuthConfig(UserRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return clienteRepository.findByUsername(username);
    }

}

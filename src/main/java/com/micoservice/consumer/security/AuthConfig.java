package com.micoservice.consumer.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.micoservice.consumer.repositories.ClienteRepository;

@Service
public class AuthConfig implements UserDetailsService {

    public final ClienteRepository clienteRepository;

    public AuthConfig(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return clienteRepository.findByName(username);
    }

}

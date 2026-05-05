package com.micoservice.consumer.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // indica que essa classe é uma classe de configuração do Spring, ou seja, ela
               // vai conter definições de beans e outras configurações para a aplicação
@EnableWebSecurity // habilita a segurança web do Spring Security, isso é necessário para que o
                   // Spring Security possa interceptar as requisições e aplicar as regras de
                   // segurança definidas na aplicação
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    public SecurityConfig(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        return httpSecurity.csrf(csrf -> csrf.disable()) // desativa o csrf pq estamos usando token JWT, ou seja, não
                                                         // estamos usando sessões, então o csrf não é necessário
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // deixa a
                                                                                                              // aplicação
                                                                                                              // sem
                                                                                                              // estado,
                                                                                                              // ou
                                                                                                              // seja, o
                                                                                                              // usuario
                                                                                                              // vai
                                                                                                              // enviar
                                                                                                              // o token
                                                                                                              // JWT em
                                                                                                              // cada
                                                                                                              // requisição,
                                                                                                              // então
                                                                                                              // não é
                                                                                                              // necessário
                                                                                                              // criar
                                                                                                              // sessões
                                                                                                              // para
                                                                                                              // armazenar
                                                                                                              // o
                                                                                                              // estado
                                                                                                              // do
                                                                                                              // usuário
                .authorizeHttpRequests(authorize -> authorize.requestMatchers(HttpMethod.POST, "/").permitAll()
                        .requestMatchers(HttpMethod.GET, "/").permitAll()
                        .anyRequest().authenticated() // todas as outras requisições precisam estar autenticadas, ou
                                                      // seja, precisam enviar um token JWT válido para acessar os
                                                      // recursos protegidos

                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class) // adiciona um filtro antes
                                                                                             // do filtro de
                                                                                             // autenticação do Spring
                                                                                             // Security, ou seja, o
                                                                                             // filtro de segurança
                                                                                             // personalizado vai ser
                                                                                             // executado antes do
                                                                                             // filtro de autenticação
                                                                                             // do Spring Security, isso
                                                                                             // é necessário para que o
                                                                                             // filtro de segurança
                                                                                             // personalizado possa
                                                                                             // validar o token JWT e
                                                                                             // autenticar o usuário
                                                                                             // antes que o filtro de
                                                                                             // autenticação do Spring
                                                                                             // Security tente
                                                                                             // autenticar o usuário
                                                                                             // usando as credenciais
                                                                                             // tradicionais (username e
                                                                                             // password)
                .build();
    }

    // Para obter o autenticador
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Para obter o encriptador de senhas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
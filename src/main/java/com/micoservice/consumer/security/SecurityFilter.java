package com.micoservice.consumer.security;

import com.micoservice.consumer.repositories.ClienteRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // vai ser injetado no contexto do Spring, ou seja, ele vai ser criado e
// gerenciado pelo Spring, isso é necessário para que ele seja reconhecido como
// um filtro de segurança e possa ser aplicado nas requisições
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final ClienteRepository clienteRepository;

    public SecurityFilter(TokenService tokenService, ClienteRepository clienteRepository) {
        this.tokenService = tokenService;
        this.clienteRepository = clienteRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // pegar o token do header Authorization
        String token = getToken(request);
        // se o token for diferente de null, ou seja, se o token existir, validar o
        // token e
        if (token != null) {
            try {
                // validar o token
                String name = tokenService.validadeToken(token);
                // Pega o cliente do banco de dados usando o nome do usuário extraído do token
                UserDetails userDetails = clienteRepository.findByName(name);
                if (userDetails != null) {
                    // Criar um objeto de autenticação do Spring Security corretamente:
                    // - principal: nome do usuário
                    // - credentials: null (pois já foi autenticado via token)
                    // - authorities: permissões do usuário
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    // definir o objeto de autenticação no contexto de segurança do Spring Security
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // Se o token for inválido, continua sem autenticação
                return;
            }
        }
        // se for null, ou seja, se o token não existir, simplesmente continuar com a
        // cadeia de filtros sem fazer nada, ou seja, o usuário não estará autenticado e
        // não terá acesso aos recursos protegidos
        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        // pegar o token do header Authorization, o token geralmente é enviado no
        // formato "Bearer <token>", então é necessário remover o prefixo "Bearer " para
        // obter apenas o token
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer "))
            return null;
        return header.replace("Bearer ", "");
    }
}

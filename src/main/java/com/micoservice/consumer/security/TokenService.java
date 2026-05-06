package com.micoservice.consumer.security;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@Service
public class TokenService {

    @Value("${jwt.secret:mysecretkey}")
    private String secretKey;

    // Gerar um token JWT para um usuário específico
    public String generateToken(String username) {
        // Criar o algoritmo de assinatura usando a chave secreta (HS256)
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        // Gerar o token JWT com o nome de usuário como claim
        String token = JWT.create()
                .withIssuer("nome-issue") // quem emitiu o token, pode ser o nome da sua aplicação ou qualquer
                // identificador
                .withSubject(username) // quem é o dono do token, ou seja, o nome do usuário
                .withExpiresAt(generateExpirationDate()) // data de expiração do token, pode ser null para não expirar
                // ou você pode definir um tempo específico
                .sign(algorithm); // assinar o token usando o algoritmo criado, isso vai gerar a string do token
        // JWT
        return token;
    }

    // validar um token JWT e extrair o nome de usuário do claim "sub" (subject)
    public String validadeToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String username = JWT.require(algorithm) // criar um verificador de token usando o mesmo algoritmo, isso vai
                // validar a assinatura do token
                .withIssuer("nome-issue")
                .build() // construir o verificador
                .verify(token).getSubject(); // verificar o token e pegar o nome de usuário do claim "sub" (subject)
        return username;
    }

    // Gerar um data de expiração para o token, por exemplo, 1 hora a partir do
    // momento da geração
    private Instant generateExpirationDate() {
        long expirationTimesInSeconds = (60 * 60) * 24; // 24 horas
        return Instant.now().plusSeconds(expirationTimesInSeconds);
    }
}

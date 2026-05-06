package com.micoservice.consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.micoservice.consumer.domain.dto.requests.ContaDTO;
import com.micoservice.consumer.domain.dto.requests.UserLoginDTO;
import com.micoservice.consumer.domain.model.Conta;
import com.micoservice.consumer.domain.services.ContaService;
import com.micoservice.consumer.domain.services.UserService;
import com.micoservice.consumer.exceptions.AlreadyExists;

@SpringBootTest
class ConsumerApplicationTests {

    @Autowired
    private UserService userService;
    @Autowired
    private ContaService contaService;

    @Test
    void testCreateUser() {

        assertThrows(AlreadyExists.class, () -> userService.register(new UserLoginDTO("tester", "1234")));
    }

    @Test
    void testSaldoDaConta() {
        ContaDTO conta = new ContaDTO(10.0);
        Conta createdConta = contaService.create(conta, 1L);
        assertEquals(conta.saldo(), createdConta.getSaldo());
    }
}

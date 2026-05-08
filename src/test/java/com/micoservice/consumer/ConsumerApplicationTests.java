package com.micoservice.consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.micoservice.consumer.domain.dto.enums.RoleEnum;
import com.micoservice.consumer.domain.dto.requests.ContaDTO;
import com.micoservice.consumer.domain.model.Conta;
import com.micoservice.consumer.domain.model.User;
import com.micoservice.consumer.domain.services.ContaService;
import com.micoservice.consumer.domain.services.UserService;

@SpringBootTest
class ConsumerApplicationTests {

    @Autowired
    private UserService userService;
    @Autowired
    private ContaService contaService;

    @Test
    void testCreateUser() {
        User user = new User("user_" + Math.random() * 100, "1234", RoleEnum.ADMIN);
        User userCreated = userService.create(user);
        assertEquals(user.getUsername(), userCreated.getUsername());
    }

    @Test
    void testSaldoDaConta() {
        ContaDTO conta = new ContaDTO(10.0);
        Conta createdConta = contaService.create(conta, 1L);
        assertEquals(conta.saldo(), createdConta.getSaldo());
    }
}

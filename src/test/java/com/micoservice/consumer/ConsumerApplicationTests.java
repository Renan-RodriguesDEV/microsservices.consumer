package com.micoservice.consumer;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.micoservice.consumer.dto.ClienteLoginDTO;
import com.micoservice.consumer.model.Cliente;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConsumerApplicationTests {

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    void testGetRouter() {

        String url = "http://localhost:" + port + "/auth";

        var response = restTemplate.getForObject(url, String.class);

        assertNotNull(response);
    }

    @Test
    void TestRegisterUser() {
        String url = "http://localhost:" + port + "/auth/register";

        var response = restTemplate.postForObject(url, new ClienteLoginDTO("Novo", "123"), Cliente.class);

        assertNotNull(response);
    }
}

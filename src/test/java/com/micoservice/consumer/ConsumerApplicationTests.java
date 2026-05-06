package com.micoservice.consumer;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import com.micoservice.consumer.dto.ClienteDTO;
import com.micoservice.consumer.model.Cliente;

@SpringBootTest
class ConsumerApplicationTests {

	TestRestTemplate restTemplate;

	public ConsumerApplicationTests() {
		this.restTemplate = new TestRestTemplate();
	}

	@Test
	void validCreateClient() {
		Cliente cliente = restTemplate.postForObject("/auth/register", new ClienteDTO("Nome", "Senha"), Cliente.class);
		assertNotNull(cliente);
	}

}

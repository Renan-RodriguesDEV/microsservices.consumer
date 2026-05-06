package com.micoservice.consumer.domain.repositories;

import com.micoservice.consumer.domain.model.Conta;
import com.micoservice.consumer.domain.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaRepository extends JpaRepository<Conta, Long> {
    public Conta findByUser(User user);
}

package com.micoservice.consumer.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.micoservice.consumer.domain.model.Transacao;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    
}

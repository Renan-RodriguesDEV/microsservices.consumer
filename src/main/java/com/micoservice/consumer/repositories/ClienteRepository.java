package com.micoservice.consumer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.micoservice.consumer.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente,Long>{
    
}

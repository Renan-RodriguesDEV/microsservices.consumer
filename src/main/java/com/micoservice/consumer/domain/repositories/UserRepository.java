package com.micoservice.consumer.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.micoservice.consumer.domain.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    public User findByUsername(String username);
}

package com.micoservice.consumer.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Conta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double saldo;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore // Evita a serialização do campo user para evitar recursão infinita
    private User user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Conta() {

    }

    public Conta(Double saldo) {
        this.saldo = saldo;

    }

    public Conta(Double saldo, User user) {
        this.saldo = saldo;
        this.user = user;

    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @PrePersist
    private void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    private void preUpdate() {
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }
}

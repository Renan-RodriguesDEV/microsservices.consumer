package com.micoservice.consumer.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Conta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double saldo;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
    private LocalDate createdAt;
    private LocalDate updatedAt;

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

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
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
            this.createdAt = LocalDate.now();
        }
    }

    @PreUpdate
    private void preUpdate() {
        if (this.updatedAt == null) {
            this.updatedAt = LocalDate.now();
        }
    }
}

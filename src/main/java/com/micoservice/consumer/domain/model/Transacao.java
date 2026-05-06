package com.micoservice.consumer.domain.model;

import java.time.LocalDateTime;

import com.micoservice.consumer.domain.dto.enums.TipoTransacao;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Entity
public class Transacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double valor;
    @Enumerated(EnumType.STRING)
    private TipoTransacao tipo;
    @ManyToOne // muitas transações podem estar associadas a uma conta
    private Conta origem;
    @ManyToOne // muitas transações podem estar associadas a uma conta
    private Conta destino;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Transacao() {
    }

    public Transacao(Double valor) {
        this.valor = valor;
    }

    public Transacao(Double valor, TipoTransacao tipo) {
        this.valor = valor;
        this.tipo = tipo;
    }

    public Transacao(Double valor, TipoTransacao tipo, Conta origem, Conta destino) {
        this.valor = valor;
        this.tipo = tipo;
        this.origem = origem;
        this.destino = destino;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransacao tipo) {
        this.tipo = tipo;
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

    public Conta getDestino() {
        return destino;
    }

    public Conta getOrigem() {
        return origem;
    }

    public void setDestino(Conta destino) {
        this.destino = destino;
    }

    public void setOrigem(Conta origem) {
        this.origem = origem;
    }

    @PrePersist
    void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}

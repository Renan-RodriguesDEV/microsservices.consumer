package com.micoservice.consumer.model;

import com.micoservice.consumer.dto.PagamentoEnum;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descricao;
    @Enumerated(EnumType.STRING)
    private PagamentoEnum pagamento;

    // Um pedido pertence a um cliente, ou seja, um cliente pode ter muitos pedidos,
    // mas um pedido só pode pertencer a um cliente. Não usamos cascade aqui porque
    // não queremos deletar o cliente quando um pedido for deletado.
    @ManyToOne(fetch = FetchType.LAZY)
    // a anotação @JoinColumn é usada para especificar a coluna que será usada para
    // fazer a junção entre as tabelas, nesse caso, a coluna cliente_id na tabela
    // pedido vai ser usada para fazer a junção com a tabela cliente, ou seja, ela
    // vai ser a chave estrangeira que vai referenciar o cliente dono do pedido.
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    private LocalDate created_at;
    private LocalDate updated_at;

    public Pedido() {

    }

    public Pedido(String descricao) {
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDate getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDate created_at) {
        this.created_at = created_at;
    }

    public LocalDate getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDate updated_at) {
        this.updated_at = updated_at;
    }

    @PrePersist
    private void prePersist() {
        if (this.created_at == null) {
            this.created_at = LocalDate.now();
        }
    }

    @PreUpdate
    private void preUpdate() {
        if (this.updated_at == null) {
            this.updated_at = LocalDate.now();
        }
    }
}

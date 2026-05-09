package com.micoservice.consumer.domain.model;

import com.micoservice.consumer.domain.dto.enums.RoleEnum;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users") // "user" é uma palavra reservada no SQL, então é melhor usar outro nome para a
// tabela
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false, unique = true)
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private RoleEnum role;
    // Um user pode ter apenas uma conta.
    @OneToOne(mappedBy = "user") // mappedBy indica que a relação é mapeada pelo campo "user" da classe Conta, ou
    // seja, a classe Conta é a dona da relação
    private Conta conta;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User(String username) {
        this.username = username;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, RoleEnum role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User(String username, String password, RoleEnum role, Conta conta) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.conta = conta;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public User() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime created_at) {
        this.createdAt = created_at;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updated_at) {
        this.updatedAt = updated_at;
    }

    @PrePersist
    private void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public RoleEnum getRole() {
        return role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // se for admin, retorna as roles de admin e user, caso contrário, retorna
        // apenas a role de user
        if (this.role == RoleEnum.ADMIN)
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

}

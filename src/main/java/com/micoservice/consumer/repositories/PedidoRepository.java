package com.micoservice.consumer.repositories;

import com.micoservice.consumer.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido,Long> {
}

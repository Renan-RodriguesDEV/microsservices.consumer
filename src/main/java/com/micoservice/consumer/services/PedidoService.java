package com.micoservice.consumer.services;

import com.micoservice.consumer.model.Pedido;
import com.micoservice.consumer.repositories.PedidoRepository;
import com.micoservice.consumer.dto.PedidoDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoService {
    private final PedidoRepository pedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public Pedido findById(Long id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    public Pedido create(PedidoDTO pedido) {
        Pedido pedidoDB = new Pedido(pedido.descricao());
        return pedidoRepository.save(pedidoDB);
    }

    public Pedido update(Long id, PedidoDTO pedido) {
        Pedido pedidoDB = this.findById(id);
        if (pedidoDB == null) return null;
        pedidoDB.setDescricao(pedido.descricao());
        return pedidoRepository.save(pedidoDB);
    }

    public void deleteById(Long id) {
        Pedido pedidoDB = this.findById(id);
        if (pedidoDB == null) return;
        pedidoRepository.delete(pedidoDB);
    }
}

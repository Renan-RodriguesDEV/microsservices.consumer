package com.micoservice.consumer.controllers;

import com.micoservice.consumer.model.Pedido;
import com.micoservice.consumer.services.PedidoService;
import com.micoservice.consumer.dto.PedidoDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("pedidos")
public class PedidoController {
    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("{id}")
    public Pedido get(@PathVariable Long id) {
        return pedidoService.findById(id);
    }

    @GetMapping
    public List<Pedido> get() {
        return pedidoService.findAll();
    }

    @PutMapping("{id}")
    public Pedido put(@PathVariable Long id, PedidoDTO pedido) {
        return pedidoService.update(id, pedido);
    }

    @PostMapping("{id}")
    public Pedido post(PedidoDTO pedido) {
        return pedidoService.create(pedido);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        pedidoService.deleteById(id);
    }
}

package com.micoservice.consumer.domain.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.micoservice.consumer.domain.dto.requests.ContaDTO;
import com.micoservice.consumer.domain.dto.requests.TransacaoDTO;
import com.micoservice.consumer.domain.dto.responses.TransacaoResponseDTO;
import com.micoservice.consumer.domain.model.Conta;
import com.micoservice.consumer.domain.model.Transacao;
import com.micoservice.consumer.domain.repositories.TransacaoRepository;
import com.micoservice.consumer.exceptions.ResourceNotFound;
import com.micoservice.consumer.exceptions.UnauthorizedException;
import com.micoservice.consumer.messaging.producer.Producer;

@Service
public class TransacaoService {
    private final TransacaoRepository transacaoRepository;
    private final ContaService contaService;
    // injetar o producer para enviar mensagens para a fila
    private final Producer producer;

    public TransacaoService(TransacaoRepository transacaoRepository, ContaService contaService, Producer producer) {
        this.transacaoRepository = transacaoRepository;
        this.contaService = contaService;
        this.producer = producer;
    }

    @Transactional // abre um bloco transacional para garantir que as operações de débito e crédito
                   // sejam atômicas (ou seja, ou ambas ocorrem ou nenhuma ocorre)
    public TransacaoResponseDTO tranferir(TransacaoDTO data) {
        Conta origem = contaService.findById(data.idOrigem());
        Conta destino = contaService.findById(data.idDestino());
        if (origem == null || destino == null) {
            throw new ResourceNotFound("Conta de origem ou destino não encontrada");
        }
        if (origem.getSaldo() <= 0 || origem.getSaldo() < data.valor() ||
                origem.getSaldo() - data.valor() < 0) {
            throw new UnauthorizedException("Saldo insuficiente");
        }
        // cria transação
        Transacao transacao = new Transacao();
        transacao.setOrigem(origem);
        transacao.setDestino(destino);
        transacao.setValor(data.valor());
        transacao.setTipo(data.tipoTransacao());

        // subtrai o valor da conta de origem e salva a transação
        Double novo_saldo_origem = origem.getSaldo() - data.valor();
        Double novo_saldo_destino = destino.getSaldo() + data.valor();
        contaService.update(origem.getId(), new ContaDTO(novo_saldo_origem));
        contaService.update(destino.getId(), new ContaDTO(novo_saldo_destino));

        transacaoRepository.save(transacao);

        producer.send(transacao);

        TransacaoResponseDTO response = TransacaoResponseDTO.fromEntity(transacao);
        return response;
    }

}

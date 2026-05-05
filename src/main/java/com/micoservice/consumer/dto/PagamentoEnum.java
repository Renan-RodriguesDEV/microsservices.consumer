package com.micoservice.consumer.dto;

public enum PagamentoEnum {
    PAGO("PAGO"), PENDENTE("PENDENTE"), EM_ANDAMENTO("EM_ANDAMENTO");

    private String tipo_pagamento;

    PagamentoEnum(String tipo_pagamento) {
        this.tipo_pagamento = tipo_pagamento;

    }

    public String getPagamento() {
        return tipo_pagamento;
    }
}

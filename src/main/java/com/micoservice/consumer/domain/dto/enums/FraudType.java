package com.micoservice.consumer.domain.dto.enums;

public enum FraudType {
    SAQUE_SUSPEITO("Tentando sacar tudo"), FORA_DE_HORA("Fora de hora"),
    TENTATIVA_EXCESSIVA("Tentou fazer diversas vezes a msm transferencia");

    String tipo;

    FraudType(String tipo) {
    }

    public String getTipo() {
        return tipo;
    }
}

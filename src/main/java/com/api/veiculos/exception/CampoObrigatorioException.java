package com.api.veiculos.exception;

public class CampoObrigatorioException extends RuntimeException {
    public CampoObrigatorioException(String campo) {
        super("O campo obrigatório '" + campo + "' não foi preenchido.");
    }
}

package com.api.veiculos.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class CotacaoDolarService {

    private final RestTemplate restTemplate = new RestTemplate();

    public double buscarCotacaoAtual() {
        try {
            return consultarAwesomeAPI();
        } catch (Exception e) {
            System.out.println("Falha na AwesomeAPI, usando Frankfurter como fallback");
            return consultarFrankfurterAPI();
        }
    }

    private double consultarAwesomeAPI() {
        Map<String, Map<String, String>> resposta = restTemplate.getForObject(
                "https://economia.awesomeapi.com.br/json/last/USD-BRL",
                Map.class
        );

        return Double.parseDouble(resposta.get("USDBRL").get("bid"));
    }

    private double consultarFrankfurterAPI() {
        Map<String, Object> resposta = restTemplate.getForObject(
                "https://api.frankfurter.app/latest?from=USD&to=BRL",
                Map.class
        );

        Map<String, Double> rates = (Map<String, Double>) resposta.get("rates");
        return rates.get("BRL");
    }
}

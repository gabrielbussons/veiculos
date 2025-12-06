package com.api.veiculos.repository;

import com.api.veiculos.infrastructure.entity.Veiculo;
import com.api.veiculos.infrastructure.repository.VeiculoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class VeiculoRepositoryTest {

    @Autowired
    private VeiculoRepository repository;

    @Test
    @DisplayName("Deve aplicar filtros corretamente")
    void deveAplicarFiltros() {
        Veiculo v1 = Veiculo.builder()
                .marca("Ford")
                .ano(2020)
                .cor("Preto")
                .placa("AAA-1111")
                .ativo(true)
                .preco(90000.0)
                .build();

        Veiculo v2 = Veiculo.builder()
                .marca("Ford")
                .ano(2020)
                .cor("Preto")
                .placa("BBB-2222")
                .ativo(true)
                .preco(110000.0)
                .build();

        Veiculo v3 = Veiculo.builder()
                .marca("Ford")
                .ano(2020)
                .cor("Branco")
                .placa("CCC-3333")
                .ativo(true)
                .preco(80000.0)
                .build();

        repository.save(v1);
        repository.save(v2);
        repository.save(v3);

        var pageable = PageRequest.of(0, 10);

        List<Veiculo> resultado = repository.buscarFiltrados(
                "Ford",
                2020,
                "Preto",
                85000.0,
                120000.0,
                pageable
        );

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(v -> v.getCor().equals("Preto")));
        assertFalse(resultado.stream().anyMatch(v -> v.getCor().equals("Branca")));
    }

    @Test
    @DisplayName("Deve falhar ao salvar duas placas iguais por causa do unique constraint")
    void deveFalharPlacaDuplicada() {
        Veiculo v1 = Veiculo.builder()
                .marca("Ford")
                .ano(2022)
                .cor("Preto")
                .placa("BBB-2222")
                .ativo(true)
                .preco(110000.0)
                .build();

        Veiculo v2 = Veiculo.builder()
                .marca("Ford")
                .ano(2020)
                .cor("Branco")
                .placa("BBB-2222")
                .ativo(true)
                .preco(120000.0)
                .build();

        repository.saveAndFlush(v1);

        assertThrows(DataIntegrityViolationException.class, () -> {
            repository.saveAndFlush(v2);
        });
    }
}
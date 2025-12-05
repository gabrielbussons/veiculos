package com.api.veiculos.service;

import com.api.veiculos.infrastructure.entity.Veiculo;
import com.api.veiculos.infrastructure.repository.VeiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class VeiculoServiceTest {

    private VeiculoService service;
    private VeiculoRepository repository;
    private CotacaoDolarService cotacaoDolarService;

    @BeforeEach
    void setup() {
        repository = mock(VeiculoRepository.class);
        cotacaoDolarService = mock(CotacaoDolarService.class);

        service = new VeiculoService(repository, cotacaoDolarService);
    }

    @Test
    void deveLancarExcecaoQuandoPlacaDuplicada() {
        Veiculo existente = Veiculo.builder()
                .id(1L)
                .placa("ABC1234")
                .build();

        when(repository.findByPlaca("ABC1234"))
                .thenReturn(Optional.of(existente));

        Veiculo novo = Veiculo.builder()
                .placa("ABC1234")
                .build();

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.salvarVeiculo(novo)
        );

        assertEquals("Já existe um veículo cadastrado com esta placa", ex.getMessage());
    }

    @Test
    void devePermitirSalvarQuandoPlacaNaoDuplicada() {
        when(repository.findByPlaca("XYZ9999"))
                .thenReturn(Optional.empty());

        when(cotacaoDolarService.buscarCotacaoAtual()).thenReturn(5.0);

        Veiculo novo = Veiculo.builder()
                .placa("XYZ9999")
                .preco(100.0)
                .build();

        service.salvarVeiculo(novo);

        verify(repository, times(1)).saveAndFlush(any(Veiculo.class));
    }

    @Test
    void deveAplicarFiltrosCombinados() {
        Veiculo v1 = Veiculo.builder().id(1L).marca("Ford").cor("Preto").build();
        Veiculo v2 = Veiculo.builder().id(2L).marca("Ford").cor("Preto").build();

        when(repository.buscarFiltrados(
                eq("Ford"),
                eq(2020),
                eq("Preto"),
                eq(50000.0),
                eq(120000.0),
                any()
        )).thenReturn(List.of(v1, v2));

        List<Veiculo> resultado = service.buscarVeiculos(
                "Ford",
                2020,
                "Preto",
                50000.0,
                120000.0,
                "preco",
                "asc",
                0,
                10
        );

        assertEquals(2, resultado.size());
        assertEquals("Ford", resultado.get(0).getMarca());
        assertEquals("Preto", resultado.get(0).getCor());

        verify(repository, times(1)).buscarFiltrados(
                eq("Ford"),
                eq(2020),
                eq("Preto"),
                eq(50000.0),
                eq(120000.0),
                any()
        );
    }

    @Test
    void deveFalharAoAtualizarVeiculoInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Veiculo veiculoAtualizar = Veiculo.builder()
                .marca("Fiat")
                .modelo("Uno")
                .build();

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.atualizarVeiculo(99L, veiculoAtualizar)
        );

        assertEquals("Veículo não encontrado", ex.getMessage());
    }
}

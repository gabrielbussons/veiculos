package com.api.veiculos.service;

import com.api.veiculos.infrastructure.dto.RelatorioMarcaDTO;
import com.api.veiculos.infrastructure.entity.Veiculo;
import com.api.veiculos.infrastructure.repository.VeiculoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class VeiculoService {

    private final VeiculoRepository repository;
    private final CotacaoDolarService cotacaoDolarService;


    public VeiculoService(VeiculoRepository repository, CotacaoDolarService cotacaoDolarService) {
        this.repository = repository;
        this.cotacaoDolarService = cotacaoDolarService;
    }

    public List<Veiculo> buscarVeiculos(String marca, Integer ano, String cor, Double minPreco, Double maxPreco, String campoOrdenacao, String direcaoOrdenacao) {
        Sort ordenacao = criarOrdenacao(campoOrdenacao, direcaoOrdenacao);

        return repository.buscarFiltrados(marca, ano, cor, minPreco, maxPreco, ordenacao);
    }

    public Veiculo buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Veículo não encontrado"));
    }

    public void salvarVeiculo(Veiculo veiculo) {
        validarPlacaDuplicada(veiculo.getPlaca(), null);

        veiculo.setPreco(obterValorDolar(veiculo.getPreco()));

        if (veiculo.getAtivo() == null) {
            veiculo.setAtivo(true);
        }
        repository.saveAndFlush(veiculo);
    }

    public void atualizarVeiculo(Long id, Veiculo dados) {
        Veiculo veiculoEntity = buscarPorId(id);

        validarPlacaDuplicada(dados.getPlaca(), id);

        Veiculo atualizado = Veiculo.builder()
                .id(veiculoEntity.getId())
                .marca(dados.getMarca() != null ? dados.getMarca() : veiculoEntity.getMarca())
                .modelo(dados.getModelo() != null ? dados.getModelo() : veiculoEntity.getModelo())
                .ano(dados.getAno() != null ? dados.getAno() : veiculoEntity.getAno())
                .cor(dados.getCor() != null ? dados.getCor() : veiculoEntity.getCor())
                .preco(dados.getPreco() != null ? obterValorDolar(dados.getPreco()) : obterValorDolar(veiculoEntity.getPreco()))
                .ativo(veiculoEntity.getAtivo())
                .build();

        repository.saveAndFlush(atualizado);
    }

    public void atualizarVeiculoParcialmente(Long id, Map<String, Object> campos) {
        Veiculo veiculo = buscarPorId(id);

        campos.forEach((campo, valor) -> {
            switch (campo) {
                case "marca" -> veiculo.setMarca((String) valor);
                case "modelo" -> veiculo.setModelo((String) valor);
                case "ano" -> veiculo.setAno((Integer) valor);
                case "cor" -> veiculo.setCor((String) valor);
                case "preco" -> veiculo.setPreco(obterValorDolar(Double.valueOf(valor.toString())));
                case "ativo" -> veiculo.setAtivo((Boolean) valor);
                case "placa" -> {
                    String novaPlaca = (String) valor;
                    validarPlacaDuplicada(novaPlaca, veiculo.getId());
                    veiculo.setPlaca(novaPlaca);
                }
                default -> throw new RuntimeException("Campo inválido: " + campo);
            }
        });

        repository.saveAndFlush(veiculo);
    }

    public void deletarVeiculo(Long id) {
        Veiculo veiculo = buscarPorId(id);

        veiculo.setAtivo(false);
        repository.saveAndFlush(veiculo);
    }

    public List<RelatorioMarcaDTO> relatorioVeiculoPorMarca() {
        return repository.relatorioPorMarca();
    }

    private void validarPlacaDuplicada(String placa, Long idAtual) {
        var existente = repository.findByPlaca(placa);

        if (existente.isPresent() && !existente.get().getId().equals(idAtual)) {
            throw new RuntimeException("Já existe um veículo cadastrado com esta placa");
        }
    }

    private Double obterValorDolar(Double valorReal) {
        Double cotacao = cotacaoDolarService.buscarCotacaoAtual();
        return valorReal / cotacao;
    }

    private Sort criarOrdenacao(String campoOrdenacao, String direcaoOrdenacao) {
        return Sort.by(
                direcaoOrdenacao.equalsIgnoreCase("desc") ?
                        Sort.Order.desc(campoOrdenacao) :
                        Sort.Order.asc(campoOrdenacao)
        );
    }
}
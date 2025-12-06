package com.api.veiculos.service;

import com.api.veiculos.exception.CampoObrigatorioException;
import com.api.veiculos.exception.PlacaDuplicadaException;
import com.api.veiculos.infrastructure.dto.RelatorioMarcaDTO;
import com.api.veiculos.infrastructure.dto.VeiculoDTO;
import com.api.veiculos.infrastructure.dto.VeiculoRequestDTO;
import com.api.veiculos.infrastructure.entity.Veiculo;
import com.api.veiculos.infrastructure.repository.VeiculoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
public class VeiculoService {

    private final VeiculoRepository repository;
    private final CotacaoDolarService cotacaoDolarService;
    private final ObjectMapper objectMapper;


    public VeiculoService(VeiculoRepository repository, CotacaoDolarService cotacaoDolarService, ObjectMapper objectMapper) {
        this.repository = repository;
        this.cotacaoDolarService = cotacaoDolarService;
        this.objectMapper = objectMapper;
    }

    public List<VeiculoDTO> buscarVeiculos(String marca, Integer ano, String cor, Double minPreco, Double maxPreco, String campoOrdenacao, String direcaoOrdenacao, int pagina, int tamanho) {
        Sort ordenacao = criarOrdenacao(campoOrdenacao, direcaoOrdenacao);
        Pageable paginacao = PageRequest.of(pagina, tamanho, ordenacao);

        List<Veiculo> veiculos = repository.buscarFiltrados(marca, ano, cor, minPreco, maxPreco, paginacao);
        return veiculos.stream().map(this::converterParaVeiculoDto).toList();
    }

    public VeiculoDTO buscarVeiculoPorId(Long id) {
        Veiculo veiculo = obterVeiculoPorId(id);
        return converterParaVeiculoDto(veiculo);
    }

    public void salvarVeiculo(VeiculoRequestDTO veiculo) {
        validarPlacaDuplicada(veiculo.getPlaca(), null);

        validarCamposObrigatorios(veiculo);

        veiculo.setPreco(obterValorDolar(veiculo.getPreco()));

        if (veiculo.getAtivo() == null) {
            veiculo.setAtivo(true);
        }
        repository.saveAndFlush(converterParaVeiculoEntity(veiculo));
    }

    public void atualizarVeiculo(Long id, VeiculoRequestDTO veiculo) {
        repository.findById(id).orElseThrow(() -> new RuntimeException("Veículo não encontrado"));

        validarPlacaDuplicada(veiculo.getPlaca(), id);

        validarCamposObrigatorios(veiculo);

        repository.saveAndFlush(converterParaVeiculoEntity(veiculo));
    }

    public void atualizarVeiculoParcialmente(Long id, Map<String, Object> campos) {
        Veiculo veiculo = obterVeiculoPorId(id);

        campos.forEach((campo, valor) -> {
            switch (campo) {
                case "marca" -> veiculo.setMarca((String) valor);
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
        Veiculo veiculo = obterVeiculoPorId(id);

        veiculo.setAtivo(false);
        repository.saveAndFlush(veiculo);
    }

    public List<RelatorioMarcaDTO> relatorioVeiculoPorMarca() {
        return repository.relatorioPorMarca();
    }

    private Veiculo obterVeiculoPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Veículo não encontrado"));
    }

    private void validarPlacaDuplicada(String placa, Long idAtual) {
        var existente = repository.findByPlaca(placa);

        if (existente.isPresent() && !existente.get().getId().equals(idAtual)) {
            throw new PlacaDuplicadaException("Já existe um veículo cadastrado com esta placa");
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

    private VeiculoDTO converterParaVeiculoDto(Veiculo veiculo) {
        return objectMapper.convertValue(veiculo, VeiculoDTO.class);
    }

    private Veiculo converterParaVeiculoEntity(VeiculoRequestDTO dto) {
        return objectMapper.convertValue(dto, Veiculo.class);
    }

    private void validarCamposObrigatorios(VeiculoRequestDTO veiculo) {
        if (veiculo.getMarca() == null || veiculo.getMarca().isBlank()) {
            throw new CampoObrigatorioException("marca");
        }
        if (veiculo.getAno() == null) {
            throw new CampoObrigatorioException("ano");
        }
        if (veiculo.getPlaca() == null || veiculo.getPlaca().isBlank()) {
            throw new CampoObrigatorioException("placa");
        }
        if (veiculo.getCor() == null || veiculo.getCor().isBlank()) {
            throw new CampoObrigatorioException("cor");
        }
        if (veiculo.getPreco() == null) {
            throw new CampoObrigatorioException("preco");
        }
        if (veiculo.getAtivo() == null) {
            throw new CampoObrigatorioException("ativo");
        }
    }
}
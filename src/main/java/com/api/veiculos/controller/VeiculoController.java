package com.api.veiculos.controller;

import com.api.veiculos.infrastructure.dto.RelatorioMarcaDTO;
import com.api.veiculos.infrastructure.entity.Veiculo;
import com.api.veiculos.service.VeiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/veiculos")
@Tag(name = "Veículos", description = "API para gerenciar veículos")
public class VeiculoController {

    private final VeiculoService veiculoService;

    public VeiculoController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    @Operation(
            summary = "Buscar veículos",
            description = "Retorna lista de veículos filtrando por marca, ano, cor e preço",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de veículos retornada com sucesso"),
                    @ApiResponse(responseCode = "401", description = "Não autorizado")
            }
    )
    public ResponseEntity<List<Veiculo>> buscarVeiculos(
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) String cor,
            @RequestParam(required = false) Double minPreco,
            @RequestParam(required = false) Double maxPreco) {

        List<Veiculo> veiculos = veiculoService.buscarVeiculos(marca, ano, cor, minPreco, maxPreco);
        return ResponseEntity.ok(veiculos);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar veículo por ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Veículo retornado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Veículo não encontrado"),
                    @ApiResponse(responseCode = "401", description = "Não autorizado")
            }
    )
    public ResponseEntity<Veiculo> buscarVeiculoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(veiculoService.buscarPorId(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(
            summary = "Criar veículo",
            description = "Cria um novo veículo",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Veículo criado com sucesso"),
                    @ApiResponse(responseCode = "401", description = "Não autorizado"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado")
            }
    )
    public ResponseEntity<Void> salvarVeiculo(@RequestBody Veiculo veiculo) {
        veiculoService.salvarVeiculo(veiculo);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar veículo",
            description = "Atualiza os dados de um veículo existente",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Veículo atualizado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Veículo não encontrado"),
                    @ApiResponse(responseCode = "401", description = "Não autorizado"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado")
            }
    )
    public ResponseEntity<Void> atualizarVeiculo(@PathVariable Long id, @RequestBody Veiculo veiculo) {
        veiculoService.atualizarVeiculo(id, veiculo);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    @Operation(
            summary = "Atualizar parcialmente veículo",
            description = "Atualiza apenas os campos informados de um veículo",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Veículo atualizado parcialmente com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Veículo não encontrado"),
                    @ApiResponse(responseCode = "401", description = "Não autorizado"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado")
            }
    )
    public ResponseEntity<Void> atualizarVeiculoParcialmente(@PathVariable Long id, @RequestBody Map<String, Object> campos) {
        veiculoService.atualizarVeiculoParcialmente(id, campos);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar veículo",
            description = "Deleta um veículo pelo ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Veículo deletado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Veículo não encontrado"),
                    @ApiResponse(responseCode = "401", description = "Não autorizado"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado")
            }
    )
    public ResponseEntity<Void> deletarVeiculo(@PathVariable Long id) {
        veiculoService.deletarVeiculo(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/relatorios/por-marca")
    @Operation(
            summary = "Relatório de veículos por marca",
            description = "Retorna a quantidade de veículos por marca",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
                    @ApiResponse(responseCode = "401", description = "Não autorizado")
            }
    )
    public ResponseEntity<List<RelatorioMarcaDTO>> relatorioVeiculoPorMarca() {
        return ResponseEntity.ok(veiculoService.relatorioVeiculoPorMarca());
    }
}

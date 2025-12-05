package com.api.veiculos.controller;

import com.api.veiculos.infrastructure.dto.RelatorioMarcaDTO;
import com.api.veiculos.infrastructure.entity.Veiculo;
import com.api.veiculos.service.VeiculoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/veiculos")
public class VeiculoController {

    private final VeiculoService veiculoService;

    public VeiculoController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
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
    public ResponseEntity<Veiculo> buscarVeiculoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(veiculoService.buscarPorId(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Void> salvarVeiculo(@RequestBody Veiculo veiculo) {
        veiculoService.salvarVeiculo(veiculo);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizarVeiculo(@PathVariable Long id, @RequestBody Veiculo veiculo) {
        veiculoService.atualizarVeiculo(id, veiculo);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<Void> atualizarVeiculoParcialmente(@PathVariable Long id, @RequestBody Map<String, Object> campos) {
        veiculoService.atualizarVeiculoParcialmente(id, campos);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVeiculo(@PathVariable Long id) {
        veiculoService.deletarVeiculo(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/relatorios/por-marca")
    public ResponseEntity<List<RelatorioMarcaDTO>> relatorioVeiculoPorMarca() {
        return ResponseEntity.ok(veiculoService.relatorioVeiculoPorMarca());
    }
}

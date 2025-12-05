package com.api.veiculos.infrastructure.repository;

import com.api.veiculos.infrastructure.dto.RelatorioMarcaDTO;
import com.api.veiculos.infrastructure.entity.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

    @Query("""
                SELECT v FROM Veiculo v
                WHERE (:marca IS NULL OR v.marca = :marca)
                  AND (:ano IS NULL OR v.ano = :ano)
                  AND (:cor IS NULL OR v.cor = :cor)
                  AND (:minPreco IS NULL OR v.preco >= :minPreco)
                  AND (:maxPreco IS NULL OR v.preco <= :maxPreco)
                  AND v.ativo = TRUE
            """)
    List<Veiculo> buscarFiltrados(String marca, Integer ano, String cor, Double minPreco, Double maxPreco, Sort sort);

    @Query("""
                SELECT new com.api.veiculos.infrastructure.dto.RelatorioMarcaDTO(
                    v.marca,
                    COUNT(v)
                )
                FROM Veiculo v
                WHERE v.ativo = TRUE
                GROUP BY v.marca
            """)
    List<RelatorioMarcaDTO> relatorioPorMarca();

    Optional<Veiculo> findByPlaca(String placa);
}


package com.api.veiculos.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VeiculoDTO {
    private Long id;
    private String marca;
    private String modelo;
    private String placa;
    private Integer ano;
    private String cor;
    private Double preco;
    private Boolean ativo;
}

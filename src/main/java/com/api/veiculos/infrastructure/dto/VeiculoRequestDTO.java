package com.api.veiculos.infrastructure.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VeiculoRequestDTO {

    @NotBlank
    private String marca;

    @NotBlank
    private String placa;

    @NotNull
    private Integer ano;

    @NotBlank
    private String cor;

    @NotNull
    private Double preco;

    @NotNull
    private Boolean ativo;
}

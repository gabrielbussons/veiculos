package com.api.veiculos.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "veiculo")
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "marca")
    private String marca;

    @Column(name = "modelo")
    private String modelo;

    @Column(name = "placa")
    private String placa;

    @Column(name = "ano")
    private Integer ano;

    @Column(name = "cor")
    private String cor;

    @Column(name = "preco")
    private Double preco;

    @Column(name = "ativo")
    private Boolean ativo;
}
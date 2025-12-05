package com.api.veiculos.e2e;

import com.api.veiculos.infrastructure.entity.Usuario;
import com.api.veiculos.infrastructure.entity.Veiculo;
import com.api.veiculos.infrastructure.enums.Role;
import com.api.veiculos.infrastructure.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class VeiculoE2ETest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setupUser() {
        Usuario admin = Usuario.builder()
                .username("admin")
                .senha(passwordEncoder.encode("123"))
                .perfil(Role.valueOf("ADMIN"))
                .build();

        usuarioRepository.save(admin);
    }

    @Test
    void fluxoCompletoDeveFuncionar() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        // 1️⃣ OBTER TOKEN (LOGIN)
        String bodyLogin = """
            {
              "username": "admin",
              "senha": "123"
            }
            """;

        MvcResult loginResult = mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(bodyLogin)
                )
                .andExpect(status().isOk())
                .andReturn();

        String token = mapper.readTree(loginResult.getResponse().getContentAsString()).get("token").asText();


        // 2️⃣ CRIAR VEÍCULO (ADMIN)
        Veiculo v = Veiculo.builder()
                .marca("Ford")
                .modelo("Uno")
                .ano(2020)
                .cor("Preto")
                .placa("CCC-3333")
                .ativo(true)
                .preco(80000.0)
                .build();

        mockMvc.perform(
                        post("/veiculos")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(v))
                )
                .andExpect(status().isOk())
                .andReturn();


        // 3️⃣ LISTAR VEÍCULOS
        mockMvc.perform(
                        get("/veiculos")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].marca").value("Ford"));


        // 4️⃣ FILTRAR VEÍCULOS
        mockMvc.perform(
                        get("/veiculos")
                                .header("Authorization", "Bearer " + token)
                                .param("modelo", "Uno")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].modelo").value("Uno"));


        // 5️⃣ DETALHAR VEÍCULO POR ID
        mockMvc.perform(
                        get("/veiculos/{id}", 1)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cor").value("Preto"));
    }
}

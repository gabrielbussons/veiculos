package com.api.veiculos.controller;

import com.api.veiculos.infrastructure.dto.LoginDTO;
import com.api.veiculos.infrastructure.dto.TokenDTO;
import com.api.veiculos.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "API para autenticação de usuários")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    @Operation(
            summary = "Login do usuário",
            description = "Recebe username e senha e retorna token JWT",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login bem-sucedido"),
                    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
            }
    )
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO dto) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getSenha());

        authManager.authenticate(authToken);

        String token = jwtService.gerarToken(dto.getUsername());
        return ResponseEntity.ok(new TokenDTO(token));
    }
}

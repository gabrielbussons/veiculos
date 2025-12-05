package com.api.veiculos.controller;

import com.api.veiculos.infrastructure.dto.LoginDTO;
import com.api.veiculos.infrastructure.dto.TokenDTO;
import com.api.veiculos.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {

        System.out.println("### LOGIN RECEBIDO:");
        System.out.println("Username: " + dto.getUsername());
        System.out.println("Senha: " + dto.getSenha());
        System.out.println("### Chamando AuthenticationManager...");

        try {

            PasswordEncoder encoder = new BCryptPasswordEncoder();
            System.out.println(encoder.matches("123456", "$2a$10$E1lefQTXFLTOQ/7rCQr0iO00FiETVafGJ6JtktGBhlux0GA2CrKk."));

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getSenha());

            authManager.authenticate(authToken);

            System.out.println("### LOGIN OK!");

            String token = jwtService.gerarToken(dto.getUsername());
            return ResponseEntity.ok(new TokenDTO(token));

        } catch (Exception e) {
            System.out.println("### ERRO NO LOGIN:");
            e.printStackTrace();
            return ResponseEntity.status(401).body("Credenciais inválidas");
        }
    }
}

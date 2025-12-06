O projeto utiliza as seguintes tecnologias e dependências:

- **Spring Boot 4.0.0**
- **Spring Security** (para autenticação e autorização)
- **H2 Database** (em memória)
- **Maven**
- **Jdk17**


Detalhes da configuração do banco de dados H2:

H2 Console: habilitado e acessível em http://localhost:8081/h2-console
Banco de dados: H2 em memória, com URL jdbc:h2:mem:veiculos
Usuário: sa
sem senha

Porta do servidor: 8081

Para cadastro de usuários iniciais, utilize os seguintes comandos SQL no console do H2:

INSERT INTO usuario (username, senha, perfil, ativo) VALUES ('admin', '$2a$10$E1lefQTXFLTOQ/7rCQr0iO00FiETVafGJ6JtktGBhlux0GA2CrKk.', 'ADMIN', TRUE);
INSERT INTO usuario (username, senha, perfil, ativo) VALUES ('user', '$2a$10$E1lefQTXFLTOQ/7rCQr0iO00FiETVafGJ6JtktGBhlux0GA2CrKk.', 'USER', TRUE);
-- A senha para ambos os usuários é '123456' (criptografada com BCrypt)


Além disso, o projeto conta com testes de service, repository e e2e.

Documentação da API disponível em /swagger-ui.html após iniciar a aplicação.
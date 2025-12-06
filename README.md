O projeto utiliza as seguintes tecnologias e dependências:

- **Spring Boot 4.0.0**
- **Spring Security** (para autenticação e autorização)
- **H2 Database** (em memória)
- **Maven**
- **Jdk17**
- **Redis**


Detalhes da configuração do banco de dados H2:

H2 Console: habilitado e acessível em http://localhost:8081/h2-console
Banco de dados: H2 em memória, com URL jdbc:h2:mem:veiculos
Usuário: sa
sem senha

Porta do servidor: 8081


Rodando o Redis com Docker
Para executar o Redis localmente, basta rodar:
docker run --name redis -p 6379:6379 -d redis
O Redis ficará disponível em localhost:6379 e a aplicação já está configurada para se conectar automaticamente.
Certifique-se de ter o Docker instalado e em execução na sua máquina.
OBS: O Redis é utilizado para cache do valor atual do Dolar, melhorando a performance da aplicação. A cada 1 minuto, o valor é atualizado a partir de uma API externa.

Para cadastro de usuários iniciais, utilize os seguintes comandos SQL no console do H2:

INSERT INTO usuario (username, senha, perfil, ativo) VALUES ('admin', '$2a$10$E1lefQTXFLTOQ/7rCQr0iO00FiETVafGJ6JtktGBhlux0GA2CrKk.', 'ADMIN', TRUE);
INSERT INTO usuario (username, senha, perfil, ativo) VALUES ('user', '$2a$10$E1lefQTXFLTOQ/7rCQr0iO00FiETVafGJ6JtktGBhlux0GA2CrKk.', 'USER', TRUE);
-- A senha para ambos os usuários é '123456' (criptografada com BCrypt)


Além disso, o projeto conta com testes de service, repository e e2e.

Documentação da API disponível em /swagger-ui.html após iniciar a aplicação.
# Spring - Projeto Base

Este projeto é implementa autenticação e autorização com JWT (JSON Web Tokens) em uma aplicação Spring Boot.
Ele foi desenvolvido com o objetivo de demonstrar as melhores práticas para uma aplicação Spring Boot com segurança.

## Funcionalidades Implementadas

*   **Autenticação:**
    *   Cadastro de usuários com roles (USER, MODERATOR, ADMIN).
    *   Login de usuários com geração de tokens JWT.
    *   Validação de tokens JWT.
    *   Autenticação baseada em JWT para acesso aos endpoints da API.
*   **Autorização:**
    *   Proteção de endpoints com diferentes níveis de acesso (ANONIMO, USER, MODERATOR e ADMIN).
    *   Validação de roles do usuário ao acessar os endpoints da API.
*   **Segurança:**
    *   Implementação do filtro `AuthTokenFilter` para extrair e validar tokens JWT.
    *   Uso de `AuthenticationEntryPoint` para tratar erros de autenticação.
    *   Uso de `PasswordEncoder` para criptografar as senhas.
*  **Testes**:
* Implementação de testes unitários.
* Implementação de testes de integração.
    * Testes de integração para o ciclo de vida completo de filtros da spring security.
    * Testes de integração para todos os endpoints da sua aplicação, desde o cadastro até a validação da autenticação.

## Tecnologias Utilizadas

*   **Spring Boot:** Framework para desenvolvimento rápido de aplicações Java.
*   **Spring Security:** Framework para segurança de aplicações Spring.
*   **JSON Web Tokens (JWT):** Mecanismo para gerar tokens de acesso.
*   **Lombok:** Biblioteca para redução de código boilerplate.
*   **JUnit:** Framework para testes unitários e de integração.
*   **Mockito:** Framework para criação de mocks e stubs.
*   **H2 Database:** Banco de dados em memória para testes.
*   **Java 17:** Versão da linguagem Java utilizada.
*   **Hibernate:** Framework para o mapeamento objeto-relacional.

## Como Usar o Projeto

### Requisitos

*   Java 17 ou superior instalado.
*   Maven.
*   Um editor de código ou IDE de sua preferência.

### Passo a Passo

1.  **Clonar o Repositório:**
    ```bash
    git clone [URL do repositório]
    cd spring-boot-security-jwt
    ```

2.  **Construir o Projeto:**
    ```bash
    mvn clean install

3.  **Executar a Aplicação:**
    ```bash
    mvn spring-boot:run

    A aplicação estará disponível em `http://localhost:8080`.

4.  **Utilizar os Endpoints:**
    *   **Cadastro:** `POST /api/auth/signup`
        *   Corpo da requisição (exemplo):
            ```json
            {
              "username": "seu_usuario",
              "password": "sua_senha",
              "role": ["user", "admin"] // opcional
            }
            ```
    *   **Login:** `POST /api/auth/signin`
        *   Corpo da requisição (exemplo):
            ```json
            {
              "username": "seu_usuario",
              "password": "sua_senha"
            }
            ```
        *   Retorna um token JWT no corpo da resposta, dentro de um atributo chamado `accessToken`.


*  **Endpoints de Teste:**
    *   `/api/test/all` (acesso público)
    *   `/api/test/user` (acesso USER, MODERATOR, ADMIN)
    *   `/api/test/mod` (acesso MODERATOR)
    *   `/api/test/admin` (acesso ADMIN)
    * `/api/test/test` (acesso público)

*  Para acessar os endpoints protegidos, você deve incluir o token gerado no cabeçalho de autorização da requisição como `Authorization: Bearer seu_token`.

5. **Executar os Testes:**
    * Para executar os testes você pode usar a sua IDE ou executar a partir do terminal com:
   ```bash
   mvn test

### Testes

Os testes deste projeto:

*   **Testes de Integração:** Verificam a interação entre componentes da aplicação, como controllers, serviços e o banco de dados.
*  Testes dos endpoints de cadastro e login
*  Testes de acesso aos endpoints com e sem roles
*  Testes dos filtros do spring security.
* Testes de funcionamento do JWT.
*   **Testes Unitários:** Verificam o comportamento de classes individualmente **utilizando mocks**.

## Considerações Finais

Este projeto tem o objetivo de apresentar uma arquitetura robusta para autenticação e autorização em aplicações Spring Boot.
Os testes de integração e unitários ajudam a garantir a qualidade do código e dão mais segurança sobre a implementação das funcionalidades.

Contribuições e sugestões para o projeto são sempre bem-vindas! :D

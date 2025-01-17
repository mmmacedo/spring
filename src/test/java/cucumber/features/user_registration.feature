Feature: Cadastro de novos usuários
  Para que novos usuários possam acessar o sistema

  Scenario: Cadastro de novo usuário com sucesso
    Given Eu estou autenticado como administrador
    And campo "username" com "johndoe1"
    And campo "password" com "securePassword123"
    When Eu submeto o formulário de cadastro
    Then Eu devo ver status OK
    And O usuário "johndoe1" deve estar registrado no sistema
    And O usuário "johndoe1" deve ter a role "ROLE_USER"

#  Scenario: Cadastro de novo usuário com sucesso
#    Given Eu estou autenticado como administrador
#    And campo "username" com "johndoe2"
#    And campo "password" com "securePassword123"
#    And campo "role" com "admin"
#    When Eu submeto o formulario de cadastro de usuario com perfil
#    Then Eu devo ver status OK
#    And O usuário "johndoe2" deve estar registrado no sistema
#    And O usuário "johndoe2" deve ter a role "ROLE_ADMIN"
#
#  Scenario: Cadastro de novo usuário com sucesso
#    Given Eu estou autenticado como usuario
#    And campo "username" com "johndoe3"
#    And campo "password" com "securePassword123"
#    When Eu submeto o formulário de cadastro
#    Then Eu devo ver status OK
#    And O usuário "johndoe3" deve estar registrado no sistema
#    And O usuário "johndoe3" deve ter a role "ROLE_USER"
#
#  Scenario: Cadastro de novo usuário com falha
#    Given Eu estou autenticado como usuario
#    And campo "username" com "johndoe4"
#    And campo "password" com "securePassword123"
#    And campo "role" com "admin"
#    When Eu submeto o formulario de cadastro de usuario com perfil
#    Then Eu devo ver status FORBIDDEN
#
#  Scenario: Cadastro de novo usuário com falha
#    Given Eu estou nao estou autenticado
#    And campo "username" com "johndoe5"
#    And campo "password" com "securePassword123"
#    When Eu submeto o formulário de cadastro
#    Then Eu devo ver status UNAUTHORIZED
#
#  Scenario: Cadastro de novo usuário com falha
#    Given Eu estou autenticado como administrador
#    And campo "username" com "johndoe6"
#    And campo "password" com "securePassword123"
#    When Eu submeto o formulário de cadastro
#    Then Eu devo ver status OK
#    And campo "username" com "johndoe6"
#    And campo "password" com "securePassword123"
#    When Eu submeto o formulário de cadastro
#    Then Eu devo ver status BAD_REQUEST
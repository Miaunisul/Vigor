# VIGOR — Plano de Testes

## 1. Objetivo

Definir a estratégia inicial para verificar as funcionalidades e regras de negócio do VIGOR, com foco no cadastro e na consulta de alunos.

## 2. Escopo dos testes

Os testes devem verificar:

- Cadastro de aluno com dados válidos.
- Validação de campos obrigatórios.
- Validação do CPF, incluindo dígitos verificadores.
- Prevenção de CPF duplicado.
- Consulta de alunos cadastrados.
- Persistência e consistência dos dados.
- Tratamento de erros durante as operações.

## 3. Tipos de teste

### 3.1 Testes unitários

Verificam regras e componentes isoladamente, sem depender de uma execução completa da aplicação.

**Ferramentas previstas:** JUnit 5 e Mockito.

### 3.2 Testes de integração

Verificam a interação entre componentes, incluindo serviços, repositórios e persistência.

O ambiente de teste deve ser isolado para evitar alterações nos dados de desenvolvimento.

### 3.3 Testes de aceitação

Verificam se o comportamento observado corresponde aos critérios de aceite documentados em `docs/requirements/criterios-de-aceite.md`.

## 4. Casos de teste previstos

| ID | Cenário | Resultado esperado | Tipo |
|---|---|---|---|
| CT01 | Cadastrar aluno com dados válidos | Cadastro realizado com sucesso | Integração/aceitação |
| CT02 | Omitir campo obrigatório | Cadastro rejeitado com indicação do erro | Unitário/aceitação |
| CT03 | Informar CPF inválido | Cadastro rejeitado sem novo registro | Unitário/integração |
| CT04 | Repetir CPF já cadastrado | Segundo cadastro impedido | Integração |
| CT05 | Consultar aluno existente | Dados cadastrados apresentados corretamente | Integração/aceitação |
| CT06 | Consultar após novo cadastro | Registro continua disponível | Integração |
| CT07 | Informar dados com acentos | Dados armazenados e apresentados corretamente | Integração |
| CT08 | Ocorrer falha de persistência | Erro tratado sem indicar sucesso indevido | Integração |

## 5. Ambiente e ferramentas

- Java 21.
- Maven.
- JUnit 5.
- Mockito, quando necessário.
- Spring Boot Test.
- Banco de dados de teste isolado.
- JaCoCo para medição da cobertura de testes.

As ferramentas e versões efetivas devem ser confirmadas na configuração final do projeto.

## 6. Execução

Após a configuração do projeto, os testes deverão poder ser executados pelo Maven:

`mvn test`

O relatório de cobertura deverá ser gerado conforme a configuração do JaCoCo.

## 7. Critérios de qualidade

- Os testes devem verificar resultados esperados, não apenas a ausência de exceções.
- Cenários inválidos não podem criar registros indevidos.
- Os testes devem ser repetíveis e não depender de dados preexistentes.
- A meta de cobertura é de pelo menos 75%, a ser confirmada por relatório real.
- Falhas devem ser registradas e corrigidas antes de considerar o cenário aprovado.

## 8. Registro dos resultados

Para cada caso executado, registrar:

- Resultado esperado.
- Resultado obtido.
- Situação: aprovado, reprovado ou bloqueado.
- Evidência ou referência ao relatório.
- Defeito associado, se houver.

## 9. Situação do documento

Plano inicial. Os casos serão ajustados conforme os requisitos forem confirmados e a aplicação for implementada. Nenhum resultado é considerado aprovado antes da execução efetiva.
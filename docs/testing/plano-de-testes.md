# Plano de Testes — VIGOR

## 1. Objetivo

Definir a estratégia de testes do VIGOR — Sistema de Cadastro de Academia, para verificar se as funcionalidades implementadas atendem aos requisitos funcionais, às regras de negócio e aos requisitos de qualidade estabelecidos para a A3 de Gestão e Qualidade de Software da UNISUL.

## 2. Escopo dos testes

Os testes contemplarão as funcionalidades previstas para o sistema, conforme sua implementação e priorização.

### 2.1. Cadastro de membros

- Cadastro com dados válidos.
- Validação de campos obrigatórios.
- Rejeição de CPF inválido.
- Rejeição de CPF duplicado.
- Verificação de persistência dos registros válidos.

### 2.2. Consulta de membros

- Listagem de membros cadastrados.
- Consulta sem resultados.
- Verificação da consistência das informações apresentadas.

### 2.3. Atualização e exclusão

- Atualização de dados válidos.
- Rejeição de alterações inválidas.
- Verificação da integridade dos registros relacionados durante a exclusão.

### 2.4. Treinos

- Cadastro de treinos válidos.
- Associação do treino a um membro existente.
- Rejeição de associação a membro inexistente.

### 2.5. Pagamentos

- Registro de pagamentos válidos.
- Validação dos valores informados.
- Verificação dos status de pagamento definidos pela equipe.

### 2.6. Histórico de atividades

- Registro de atividades associadas a membros.
- Consulta do histórico de atividades.
- Verificação da consistência dos registros.

### 2.7. Funcionários

- Validação dos dados de cadastro.
- Teste de login com credenciais válidas.
- Rejeição de credenciais inválidas.
- Verificação da proteção das senhas.

## 3. Tipos de teste

### 3.1. Testes unitários

Verificar componentes e regras de negócio isoladamente, incluindo validações, CPF, prevenção de duplicidade e operações de cadastro.

### 3.2. Testes de integração

Verificar a comunicação entre os componentes da aplicação e a persistência dos dados no banco de dados, incluindo as operações de consulta e cadastro.

### 3.3. Testes de usabilidade

Avaliar a clareza dos formulários, a compreensão das mensagens de validação, a navegação e a facilidade de utilização das telas implementadas.

### 3.4. Testes de sistema e validação

Verificar os fluxos completos implementados, desde a entrada das informações até a apresentação do resultado ao usuário.

## 4. Estratégia de execução

Os testes serão elaborados de acordo com os requisitos, as regras de negócio e os critérios de aceite documentados.

A estratégia prevê:

1. Criar os testes correspondentes às funcionalidades implementadas.
2. Executar os testes automatizados durante o desenvolvimento.
3. Corrigir falhas identificadas.
4. Reexecutar os testes após as correções.
5. Registrar os resultados e as evidências.
6. Verificar a cobertura de código.

## 5. Ambiente de testes

O ambiente previsto utiliza:

- Java 21.
- Spring Boot.
- Maven.
- JUnit 5.
- Mockito.
- Spring MockMvc, quando aplicável.
- JaCoCo para análise de cobertura.
- PostgreSQL para testes que necessitem de persistência real.

A configuração definitiva do ambiente de testes deverá acompanhar a configuração efetivamente adotada pela aplicação.

## 6. Dados de teste

Serão utilizados dados fictícios, evitando a utilização de informações pessoais reais.

Os conjuntos de dados deverão incluir:

- Membros com informações válidas.
- Campos obrigatórios vazios.
- CPFs inválidos.
- CPFs já cadastrados.
- Consultas com resultados e sem resultados.
- Treinos, pagamentos e atividades válidos e inválidos, conforme as regras implementadas.
- Credenciais fictícias para os testes de autenticação.

## 7. Critérios de aceitação dos testes

Uma funcionalidade será considerada aprovada quando:

- Atender aos critérios de aceite correspondentes.
- Produzir o resultado esperado para entradas válidas.
- Rejeitar entradas inválidas conforme as regras definidas.
- Não apresentar falhas que comprometam a integridade dos dados.
- Possuir resultados de teste registrados.

A meta de cobertura de código definida para o projeto é de **75%**, conforme o planejamento da A3. A cobertura deverá ser medida com JaCoCo; atingir esse percentual, isoladamente, não comprova que todos os requisitos foram testados.

## 8. Ferramentas e execução

Os testes automatizados deverão poder ser executados com:

```bash
mvn test
```

A geração do relatório de cobertura dependerá da configuração do JaCoCo no projeto. Após essa configuração, o comando e o caminho do relatório deverão ser registrados neste documento.

## 9. Responsabilidades

A elaboração e a execução dos testes deverão respeitar a divisão de responsabilidades definida no documento `docs/planning/plano-de-tasks.md`.

Cada integrante deverá contribuir com os testes relacionados às funcionalidades sob sua responsabilidade, conforme os acordos do grupo.

## 10. Registro de resultados e defeitos

Os resultados deverão ser documentados em `docs/testing/evidencias.md`.

Para cada execução relevante, registrar:

- Identificador do teste.
- Funcionalidade verificada.
- Resultado obtido.
- Data da execução.
- Evidência disponível.
- Falhas encontradas e situação da correção.

Os defeitos deverão ser acompanhados pela equipe até que sejam corrigidos e verificados novamente.

## 11. Situação atual

Este documento representa o planejamento dos testes. Os testes descritos não devem ser considerados executados ou aprovados até que existam resultados e evidências correspondentes.

## 12. Pendências

- Implementar os testes automatizados.
- Configurar o JaCoCo.
- Confirmar a configuração do ambiente de integração com o banco de dados.
- Realizar os testes de usabilidade.
- Registrar os resultados e as evidências.
- Verificar a cobertura mínima planejada de 75%.
- Relacionar cada integrante às funcionalidades e aos respectivos arquivos de teste.

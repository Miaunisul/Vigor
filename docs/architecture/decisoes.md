# VIGOR — Registro de Decisões Técnicas

## 1. Objetivo

Registrar as decisões técnicas do projeto, suas justificativas e os pontos que ainda precisam ser definidos. Este documento deve acompanhar a evolução real do sistema.

## 2. Tecnologias selecionadas

| Tecnologia | Utilização prevista |
|---|---|
| Java 21 | Linguagem de programação |
| Spring Boot | Estrutura principal da aplicação |
| Maven | Gerenciamento de dependências e build |
| Spring Web | Rotas e integração web |
| Thymeleaf | Renderização de páginas HTML |
| Jakarta Bean Validation | Validação dos dados de entrada |
| Spring Data JPA | Persistência e acesso aos dados |
| PostgreSQL | Banco de dados previsto para o projeto |
| JUnit 5 | Testes automatizados |
| Mockito | Isolamento de dependências nos testes |
| JaCoCo | Medição de cobertura de testes |
| GitHub Actions | Automação de build e testes |

As tecnologias listadas representam as escolhas e o planejamento técnico atual. A configuração e o funcionamento de cada componente deverão ser verificados durante a implementação.

## 3. Decisões registradas

### DEC01 — Linguagem Java
**Decisão:** utilizar Java 21.

**Justificativa:** estabelecer uma versão consistente para desenvolvimento e execução do projeto.

**Situação:** selecionada; ambiente local conferido.

### DEC02 — Estrutura Spring Boot
**Decisão:** utilizar Spring Boot com gerenciamento de dependências via Maven.

**Justificativa:** organizar a aplicação e facilitar a integração entre as camadas.

**Situação:** estrutura inicial gerada; integração ao repositório e execução ainda precisam ser verificadas.

### DEC03 — Separação de responsabilidades
**Decisão:** organizar a aplicação em camadas de controller, service e repository.

**Justificativa:** separar a interface de entrada, as regras de negócio e o acesso aos dados.

**Situação:** definida como diretriz; implementação pendente.

### DEC04 — Banco de dados
**Decisão prevista:** utilizar PostgreSQL para persistência dos alunos.

**Justificativa:** manter os registros disponíveis para consultas posteriores.

**Situação:** tecnologia planejada; configuração e conexão ainda pendentes.

### DEC05 — Validação e integridade
**Decisão:** validar os dados na aplicação e reforçar a unicidade do CPF no banco.

**Justificativa:** impedir cadastros inválidos e duplicados.

**Situação:** definida como requisito técnico; implementação e testes pendentes.

### DEC06 — Qualidade de software
**Decisão:** utilizar testes automatizados, JaCoCo e GitHub Actions.

**Justificativa:** verificar o comportamento da aplicação e acompanhar a qualidade do código.

**Situação:** planejada; configuração e resultados ainda pendentes.

## 4. Pontos pendentes

- Confirmar os campos definitivos da entidade Aluno.
- Definir a configuração local e de teste do PostgreSQL.
- Definir como serão gerenciadas as alterações do schema do banco.
- Confirmar as versões e dependências definitivas após verificar a compatibilidade.
- Configurar e executar os testes automatizados.
- Configurar o workflow de integração contínua e registrar seus resultados.

## 5. Histórico de atualização

Este documento deverá ser atualizado quando uma decisão for confirmada, alterada ou implementada. A situação descrita deve corresponder ao estado real do projeto.
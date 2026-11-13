# VIGOR — Registro de Evidências

## 1. Objetivo

Registrar as evidências de execução, testes, cobertura de código e integração contínua do VIGOR, permitindo acompanhar a qualidade do projeto de forma verificável.

## 2. Estado atual

Em 10/11/2026 foram registradas uma execução local de `mvn clean verify` e a primeira execução bem-sucedida da CI no GitHub Actions (run #27). Os resultados informados estão discriminados abaixo. Os relatórios HTML e XML do JaCoCo são gerados em `target/site/jacoco/`; na execução de CI, o relatório foi publicado como artefato `relatorio-jacoco`.

Em 13/11/2026 foram registradas novas execuções locais de `mvn test` e `mvn clean verify` na branch `feat/revisao-relacionamentos-banco`, com PostgreSQL local e as seis classes de testes de integração com PostgreSQL. Os resultados e a associação dos testes aos itens validados na task L07 estão na seção 4.

## 3. Evidências de execução

| ID | Verificação | CT/VN e cenário a registrar | Evidência esperada | Situação |
|---|---|---|---|---|
| EV01 | Build do projeto | Execução local `mvn clean verify` e GitHub Actions — CI - VIGOR, run #27, branch `feat/postgres-persistencia`, 10/11/2026 | `BUILD SUCCESS` nas execuções registradas | Executado com sucesso |
| EV02 | Testes automatizados | GitHub Actions — CI - VIGOR, run #27, branch `feat/postgres-persistencia`, 10/11/2026 | 37 executados; 0 falhas; 0 erros; 0 ignorados | Executado com sucesso |
| EV03 | Teste de contexto Spring e banco | `VigorApplicationTests.contextLoads`, incluído no run #27 | Contexto iniciado; PostgreSQL 18.6 conectado e Flyway executou V1. Isso não comprova testes funcionais de persistência CRUD. | Executado com sucesso |
| EV04 | Testes funcionais | CT/VN e cenário/variante efetivamente executados; pendente até execução | Capturas ou registro do teste de aceitação | Pendente |
| EV05 | Cobertura de código | Medição local: `mvn clean verify`, JaCoCo 0.8.16, 10/11/2026; CI: run #27 | Medição local: instruções 78%; branches 73%; linhas 98/117; métodos 24/35; classes 6/6. Na CI, JaCoCo analisou 6 classes e o gate de instruções de 75% passou (`All coverage checks have been met.`); o percentual exato da CI não foi informado. Relatório publicado como artefato `relatorio-jacoco`. | Gate de instruções aprovado na CI; percentual exato da CI não disponível |
| EV06 | Integração contínua | Workflow `CI - VIGOR`, execução #27, branch `feat/postgres-persistencia`, 10/11/2026 | `BUILD SUCCESS`; relatório JaCoCo publicado como artefato `relatorio-jacoco` | Executado com sucesso |
| EV07 | Build do projeto | Execução local `mvn clean verify`, branch `feat/revisao-relacionamentos-banco`, 13/11/2026 | `BUILD SUCCESS`; verificação `jacoco:check` aprovada (`All coverage checks have been met.`) | Executado com sucesso |
| EV08 | Testes automatizados | Execuções locais `mvn test` e `mvn clean verify`, branch `feat/revisao-relacionamentos-banco`, 13/11/2026 | 126 executados em 15 classes; 0 falhas; 0 erros; 0 ignorados | Executado com sucesso |
| EV09 | Testes de integração com PostgreSQL | Seis classes `*PostgresIntegrationTest`, incluídas no EV08, com `DB_PASSWORD` configurado e PostgreSQL local | 30 executados; 0 falhas; 0 erros; 0 ignorados. Detalhamento na seção 4. Não substitui a execução individual dos CT funcionais. | Executado com sucesso |
| EV10 | Cobertura de código | Medição local: `mvn clean verify`, JaCoCo 0.8.16, 13/11/2026 | Instruções 80% (965/1.192); branches 68% (92/134); linhas 270/322; métodos 84/104; classes 15/15 | Meta de instruções (75%) atendida |

## 4. Validação de persistência PostgreSQL — Task L07

- **Responsável:** Luan Milian Martins
- **Task:** L07 — Validar persistência e consultas de treinos, pagamentos e histórico de atividades.
- **Data da execução:** 13/11/2026
- **Branch:** `feat/revisao-relacionamentos-banco`
- **Relatório detalhado da conferência:** [relatorio-validacao-persistencia.md](relatorio-validacao-persistencia.md)

### 4.1. Ambiente

| Componente | Versão |
|---|---|
| Sistema operacional | Windows 11 |
| JDK | Eclipse Temurin 21.0.13 |
| Maven (wrapper) | 3.10.0 |
| Spring Boot | 4.1.2 |
| PostgreSQL | 18, local (cliente `psql` 18.7), banco `vigor` com as migrations V1 a V5 aplicadas pelo Flyway |
| JaCoCo | 0.8.16 |

### 4.2. Critério de avaliação

Um item é marcado como **Aprovado** somente quando existe ao menos um teste automatizado que o exercita e que passou na execução de 13/11/2026. Itens sem teste específico identificado permanecem como **Pendente**, conforme a seção 5.

As colunas de referência indicam os métodos de teste. Os testes de integração estão em `src/test/java/br/edu/unisul/vigor/repository/` e executam contra o PostgreSQL; os testes unitários estão em `src/test/java/br/edu/unisul/vigor/service/` e isolam o serviço do banco. A coluna CT indica o caso de teste relacionado no [plano de testes](plano-de-testes.md); essa associação não substitui a execução do CT funcional.

### 4.3. Treinos

| Item | Resultado | Integração com PostgreSQL (`TreinoPostgresIntegrationTest`) | Unitário (`TreinoServiceTest`) | CT |
|---|---|---|---|---|
| Persistência no PostgreSQL | Aprovado | `persisteTreinoVinculadoEConsultaPeloMembro` | `cadastraTreinoVinculadoAMembroExistente` | CT09 |
| Consulta por ID | Pendente | Nenhum teste específico identificado | Nenhum teste específico identificado | — |
| Consulta por membro | Aprovado | `persisteTreinoVinculadoEConsultaPeloMembro` | `consultaTreinosDoMembroSelecionado`; `rejeitaConsultaDeMembroInexistente` | CT10 |
| Relacionamento com membro | Aprovado | `chaveEstrangeiraRejeitaMembroInexistente` | `rejeitaCadastroSemMembro`; `rejeitaCadastroQuandoMembroNaoExiste` | CT09 |
| Rejeição de dados inválidos | Aprovado | `servicoRejeitaDuracaoInvalidaSemPersistirTreino`; `checkDoBancoRejeitaDuracaoNaoPositiva`; `checkDoBancoRejeitaTipoVazio` | `rejeitaDadosInvalidosSemPersistir` (8 variantes) | CT09 |

### 4.4. Pagamentos

| Item | Resultado | Integração com PostgreSQL (`PagamentoPostgresIntegrationTest`) | Unitário (`PagamentoServiceTest`) | CT |
|---|---|---|---|---|
| Persistência no PostgreSQL | Aprovado | `persistePagamentoEConsultaVinculoComOMembro` | `registraPagamentoPagoVinculadoAMembroExistente` | CT11 |
| Consulta por membro | Aprovado em teste unitário | Nenhum teste específico de consulta por membro identificado; o teste de persistência confere o vínculo do pagamento com o membro | `consultaPagamentosDoMembro`; `rejeitaConsultaDeMembroInexistente` | — |
| Validação de valor positivo | Aprovado | `servicoRejeitaValorInvalidoSemPersistirPagamento`; `constraintDoBancoRejeitaValorNaoPositivo` | `rejeitaValorNaoPositivoOuComMaisDeDuasCasas` (3 variantes); `rejeitaValorNulo` | CT12 |
| Validação do status PAGO | Aprovado | `servicoRejeitaStatusInvalidoSemPersistirPagamento`; `constraintDoBancoRejeitaStatusDiferenteDePago` | `rejeitaStatusDiferenteDePago` (4 variantes) | CT12 |
| Relacionamento com membro | Aprovado | `chaveEstrangeiraRejeitaMembroInexistente`; `persistePagamentoEConsultaVinculoComOMembro` | `rejeitaRegistroSemMembro`; `rejeitaRegistroQuandoMembroNaoExiste` | CT12 |

### 4.5. Histórico de atividades

| Item | Resultado | Integração com PostgreSQL (`HistoricoAtividadePostgresIntegrationTest`) | Unitário (`HistoricoAtividadeServiceTest`) | CT |
|---|---|---|---|---|
| Persistência no PostgreSQL | Aprovado | `persisteAtividadeComMembroEFuncionarioEConsultaPorAmbos` | `registraAtividadeComMembroEFuncionarioResponsavelExistentes` | CT13 |
| Consulta por membro | Aprovado | `persisteAtividadeComMembroEFuncionarioEConsultaPorAmbos` | `consultaAtividadesPorMembroExistente`; `rejeitaConsultaPorMembroInexistente` | CT13 |
| Relacionamento com membro | Aprovado | `chaveEstrangeiraRejeitaMembroInexistente` | `rejeitaRegistroSemMembro`; `rejeitaRegistroQuandoMembroNaoExiste` | CT13 |
| Relacionamento com funcionário | Aprovado | `chaveEstrangeiraRejeitaFuncionarioInexistente`; `chaveEstrangeiraTambemExigeFuncionarioResponsavel` | `rejeitaRegistroSemFuncionarioResponsavel`; `rejeitaRegistroQuandoFuncionarioNaoExiste`; `consultaAtividadesPorFuncionarioExistente` | CT13 |
| Validação do tempo de execução | Aprovado | `checkDoBancoRejeitaTempoNaoPositivo`; `servicoRejeitaDadosInvalidosSemPersistirAtividade` | `rejeitaTempoDeExecucaoNaoPositivo` (2 variantes); `rejeitaTempoNulo` | CT13 |

### 4.6. Exclusão de membro com registros vinculados

Verificação complementar dos relacionamentos com `membros`.

| Item | Resultado | Integração com PostgreSQL (`MembroExclusaoPostgresIntegrationTest`) | Unitário (`MembroServiceTest`) | CT |
|---|---|---|---|---|
| Exclusão de membro sem vínculos | Aprovado | `servicoExcluiMembroSemRegistrosRelacionados` | `deveExcluirMembroSemRegistrosRelacionados` | CT08 |
| Bloqueio pela aplicação | Aprovado | `servicoBloqueiaExclusaoDeMembroComTreino`; `servicoBloqueiaExclusaoDeMembroComPagamento`; `servicoBloqueiaExclusaoDeMembroComHistorico` | `deveBloquearExclusaoDeMembroComTreino`; `deveBloquearExclusaoDeMembroComPagamento`; `deveBloquearExclusaoDeMembroComHistorico` | CT08 |
| Bloqueio pelo banco (`ON DELETE RESTRICT`) | Aprovado | `fkDeTreinoBloqueiaExclusaoDiretaNoBanco`; `fkDePagamentoBloqueiaExclusaoDiretaNoBanco`; `fkDeHistoricoBloqueiaExclusaoDiretaNoBanco` | — | CT08 |

Verificação manual complementar: em transação desfeita com `ROLLBACK`, a exclusão direta de um membro com treino vinculado foi rejeitada pelo PostgreSQL com a mensagem `violates RESTRICT setting of foreign key constraint "fk_treinos_membros" on table "treinos"`.

### 4.7. Isolamento dos dados

| Item | Resultado | Observação | CT |
|---|---|---|---|
| Testes com dois membros diferentes | Pendente | Nenhum teste identificado pelo nome do método; a confirmação depende da leitura do corpo dos testes. | CT10, CT13 |
| Consultas retornaram somente os registros do membro solicitado | Pendente | Sem teste específico identificado. Na revisão de código, as consultas `findByMembro_Id` são derivadas pelo Spring Data e filtram por `membro.id`; não há `@Query` manual nas consultas por membro. | CT10, CT13 |

### 4.8. Execução dos testes

**Comando:** `mvn test` (executado com `.\mvnw.cmd test`), 13/11/2026, concluído às 11:46

- Testes executados: 126
- Falhas: 0
- Erros: 0
- Ignorados: 0
- Resultado: `BUILD SUCCESS`

**Comando:** `mvn clean verify` (executado com `.\mvnw.cmd clean verify`), 13/11/2026, concluído às 11:49

- Testes executados: 126 (soma dos relatórios em `target/surefire-reports/`)
- Falhas: 0
- Erros: 0
- Cobertura: instruções 80%; branches 68%; linhas 270/322; métodos 84/104; classes 15/15; `All coverage checks have been met.`
- Resultado: `BUILD SUCCESS`

Testes por classe:

| Classe | Tipo | Testes |
|---|---|---|
| `FuncionarioPostgresIntegrationTest` | Integração com PostgreSQL | 3 |
| `HistoricoAtividadePostgresIntegrationTest` | Integração com PostgreSQL | 7 |
| `MembroCpfPostgresIntegrationTest` | Integração com PostgreSQL | 2 |
| `MembroExclusaoPostgresIntegrationTest` | Integração com PostgreSQL | 7 |
| `PagamentoPostgresIntegrationTest` | Integração com PostgreSQL | 6 |
| `TreinoPostgresIntegrationTest` | Integração com PostgreSQL | 5 |
| `FuncionarioServiceTest` | Unitário | 5 |
| `HistoricoAtividadeServiceTest` | Unitário | 16 |
| `MembroServiceTest` | Unitário | 24 |
| `PagamentoServiceTest` | Unitário | 15 |
| `TreinoServiceTest` | Unitário | 13 |
| `CpfUtilsTest` | Unitário | 12 |
| `FuncionarioValidationTest` | Unitário | 3 |
| `MembroControllerTest` | Controller | 7 |
| `VigorApplicationTests` | Contexto Spring | 1 |
| **Total** | | **126** |

Cobertura por pacote (JaCoCo):

| Pacote | Instruções | Branches |
|---|---|---|
| `br.edu.unisul.vigor.service` | 79% | 65% |
| `br.edu.unisul.vigor.controller` | 71% | 65% |
| `br.edu.unisul.vigor.model` | 85% | n/a |
| `br.edu.unisul.vigor.validation` | 100% | 85% |
| `br.edu.unisul.vigor.config` | 100% | n/a |
| `br.edu.unisul.vigor` | 37% | n/a |
| **Total** | **80%** | **68%** |

Os relatórios são gerados localmente em `target/surefire-reports/` (resultados por classe) e `target/site/jacoco/index.html` (cobertura) e não são versionados.

### 4.9. Problemas encontrados

Nenhuma falha ou erro foi registrado nos testes automatizados.

Itens sem evidência específica:

- Consulta de treino por ID (4.3).
- Consulta de pagamentos por membro no PostgreSQL; o comportamento está coberto apenas por teste unitário (4.4).
- Isolamento dos dados entre dois membros (4.7).

Achados da revisão, não corrigidos nesta task e encaminhados para avaliação da equipe:

- Ausência de índice em `historico_atividades.funcionario_responsavel_id`, coluna usada por `findByFuncionarioResponsavel_Id`. A ausência já está registrada em [relacionamentos.md](../architecture/database/relacionamentos.md).
- `MembroRepository.findByCpf` não é utilizado no projeto; a verificação de duplicidade usa `existsByCpf`.

Problemas de ambiente local, resolvidos e sem relação com o código:

| Mensagem | Causa | Solução |
|---|---|---|
| `No compiler is provided in this environment. Perhaps you are running on a JRE rather than a JDK?` | Maven executado com Java 8 JRE | Instalação do JDK 21 e configuração de `JAVA_HOME` |
| `java.net.ConnectException: Connection refused: getsockopt` | PostgreSQL não instalado na máquina | Instalação do PostgreSQL e criação do banco `vigor` |

### 4.10. Conclusão

A persistência e as consultas de treinos, pagamentos e histórico de atividades foram validadas por testes automatizados executados contra PostgreSQL local, com 126 testes executados sem falhas, erros ou testes ignorados. Os relacionamentos com membro e funcionário, as validações de valor, status e tempo de execução e o bloqueio de exclusão de membros com registros vinculados estão aprovados.

Permanecem pendentes de evidência específica a consulta de treino por ID, a consulta de pagamentos por membro no PostgreSQL e o isolamento dos dados entre dois membros. A conclusão da task depende da avaliação da equipe sobre essas pendências.

**Pull Request:** [adicionar link]

## 5. Como registrar uma evidência

Para cada verificação realizada, registrar:

- Identificador da evidência.
- Identificador do CT ou VN relacionado e cenário/variante efetivamente executados.
- Se o cenário ainda não foi executado, manter a evidência como pendente e não preencher resultado como aprovado.
- Data da execução.
- Comando ou procedimento utilizado.
- Resultado obtido.
- Situação: aprovado, reprovado ou bloqueado.
- Caminho do arquivo ou link para a evidência.
- Defeito relacionado, quando aplicável.

## 6. Organização dos arquivos

Quando existirem evidências reais, organizar os arquivos de forma consistente. Exemplos:

- Capturas de tela dos testes de aceitação.
- Relatórios de testes automatizados.
- Relatório HTML de cobertura.
- Links para execuções do GitHub Actions e artefatos publicados; para o run #27, o artefato foi identificado como `relatorio-jacoco`.
- Registros de defeitos e respectivas verificações.

Não adicionar capturas fictícias nem declarar resultados que ainda não foram verificados.

## 7. Critério de cobertura

A meta definida para o projeto é de pelo menos 75% de cobertura de instruções. A medição local registrada foi 78% de instruções e 73% de branches. Separadamente, o quality gate JaCoCo da CI exige 75% de instruções e passou no run #27; o percentual exato reportado nessa execução não foi informado. Não se deve inferir que branches atingiu 75% na CI.

Na medição local de 13/11/2026 (EV10), a cobertura de instruções foi de 80%, acima da meta, e a de branches foi de 68%. A meta definida se aplica a instruções.

## 8. Histórico

| Data | Alteração | Responsável |
|---|---|---|
| 26/10/2026 | Criação do modelo inicial de registro de evidências | Millena |
| 10/11/2026 | Registro dos resultados de `mvn clean verify` e JaCoCo 0.8.16 | Execução informada pela equipe |
| 10/11/2026 | Registro da primeira execução bem-sucedida do GitHub Actions, run #27, com PostgreSQL, Flyway, gate JaCoCo e artefato de cobertura | Execução informada pela equipe |
| 13/11/2026 | Registro das execuções locais de `mvn test` e `mvn clean verify` (126 testes), da cobertura JaCoCo e da validação de persistência PostgreSQL da task L07 (EV07 a EV10 e seção 4) | Luan Milian Martins |

Atualizar este histórico conforme novas evidências forem produzidas.

## 9. Situação do documento

Os resultados do build, dos testes automatizados, da medição JaCoCo, da primeira execução da CI e da validação de persistência PostgreSQL da task L07 estão registrados. A seção 4 associa os testes automatizados aos CT relacionados, sem substituir a execução individual dos CT. As evidências de execução dos CT/VN funcionais e de usabilidade continuam pendentes até que cada verificação seja executada e registrada individualmente.

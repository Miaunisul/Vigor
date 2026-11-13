# Relatório de Validação — Persistência PostgreSQL

- **Responsável:** Luan Milian Martins
- **Task:** L07 — Validar persistência e consultas de treinos, pagamentos e histórico de atividades.
- **Data da conferência:** 13/11/2026
- **Branch:** `feat/revisao-relacionamentos-banco`, criada a partir da `develop` atualizada
- **Evidências relacionadas:** [evidencias.md](evidencias.md), EV07 a EV10 e seção 4

## Resumo

A conferência cobriu as entidades JPA e as migrations V1 a V5, as chaves estrangeiras e os índices no PostgreSQL, os repositórios e suas consultas, e a execução completa dos testes automatizados. Os 126 testes do projeto foram executados sem falhas, erros ou testes ignorados, incluindo 30 testes de integração com PostgreSQL.

| Seção | Itens | Aprovados | Pendentes | Reprovados |
|---|---|---|---|---|
| 1. Treinos | 5 | 4 | 1 | 0 |
| 2. Pagamentos | 5 | 5 | 0 | 0 |
| 3. Histórico de atividades | 5 | 5 | 0 | 0 |
| 4. Isolamento dos dados | 2 | 0 | 2 | 0 |
| **Total** | **17** | **14** | **3** | **0** |

Nenhum item foi reprovado. Os itens pendentes não têm teste automatizado específico identificado. Dois achados da revisão foram registrados para avaliação da equipe (seção 6.3).

## Ambiente e método

### Ambiente

| Componente | Versão |
|---|---|
| Sistema operacional | Windows 11 |
| JDK | Eclipse Temurin 21.0.13 |
| Maven (wrapper) | 3.10.0 |
| Spring Boot | 4.1.2 |
| PostgreSQL | 18, local (cliente `psql` 18.7), banco `vigor` |
| JaCoCo | 0.8.16 |

### Procedimento

1. Atualização da `develop` e criação da branch `feat/revisao-relacionamentos-banco` a partir dela.
2. Conferência campo a campo das entidades `Membro`, `Funcionario`, `Treino`, `Pagamento` e `HistoricoAtividade` contra as migrations V1 a V5 ([Anexo A](#anexo-a--entidades--migrations)).
3. Inicialização da aplicação em banco recém-criado, com `spring.jpa.hibernate.ddl-auto=validate`. O Flyway aplicou as cinco migrations (`Successfully applied 5 migrations to schema "public", now at version v5`) e a aplicação iniciou sem erros de validação de schema.
4. Consulta das chaves estrangeiras e dos índices no catálogo do PostgreSQL, e teste manual de exclusão com `ROLLBACK` ([Anexo B](#anexo-b--relacionamentos-no-postgresql)).
5. Revisão dos repositórios e do uso de cada método no código de produção ([Anexo C](#anexo-c--repositórios-e-consultas)).
6. Execução de `mvn test` e `mvn clean verify` (seção 5).
7. Associação de cada item deste relatório aos métodos de teste, com base nos relatórios do Surefire em `target/surefire-reports/`.

### Critério de avaliação

- **Aprovado:** existe ao menos um teste automatizado que exercita o item e que passou na execução de 13/11/2026.
- **Pendente:** nenhum teste específico foi identificado para o item.
- **Reprovado:** um teste que exercita o item falhou.

Os testes de integração estão em `src/test/java/br/edu/unisul/vigor/repository/` e executam contra o PostgreSQL. Os testes unitários estão em `src/test/java/br/edu/unisul/vigor/service/` e isolam o serviço do banco. A coluna CT indica o caso de teste relacionado no [plano de testes](plano-de-testes.md); essa associação não substitui a execução do CT funcional.

---

## 1. Testes de Treinos

| Item | Resultado | Integração com PostgreSQL (`TreinoPostgresIntegrationTest`) | Unitário (`TreinoServiceTest`) | CT |
|---|---|---|---|---|
| Persistência no PostgreSQL | Aprovado | `persisteTreinoVinculadoEConsultaPeloMembro` | `cadastraTreinoVinculadoAMembroExistente` | CT09 |
| Consulta por ID | Pendente | Nenhum teste específico identificado | Nenhum teste específico identificado | — |
| Consulta por membro | Aprovado | `persisteTreinoVinculadoEConsultaPeloMembro` | `consultaTreinosDoMembroSelecionado`; `rejeitaConsultaDeMembroInexistente` | CT10 |
| Relacionamento com membro | Aprovado | `chaveEstrangeiraRejeitaMembroInexistente` | `rejeitaCadastroSemMembro`; `rejeitaCadastroQuandoMembroNaoExiste` | CT09 |
| Rejeição de dados inválidos | Aprovado | `servicoRejeitaDuracaoInvalidaSemPersistirTreino`; `checkDoBancoRejeitaDuracaoNaoPositiva`; `checkDoBancoRejeitaTipoVazio` | `rejeitaDadosInvalidosSemPersistir` (8 variantes) | CT09 |

**Observações**

- A FK `fk_treinos_membros` e os `CHECK` de tipo e descrição não vazios e de duração positiva definidos na V3 são exercitados diretamente no banco pelos testes `chaveEstrangeira...` e `checkDoBanco...`.
- A entidade `Treino` mapeia `membro_id` com `@ManyToOne(optional = false)` e `@JoinColumn(nullable = false)`, e valida duração com `@Positive`.
- A consulta por ID usa `findById`, herdado de `JpaRepository`, mas nenhum teste a exercita de forma específica.

## 2. Testes de Pagamentos

| Item | Resultado | Integração com PostgreSQL (`PagamentoPostgresIntegrationTest`) | Unitário (`PagamentoServiceTest`) | CT |
|---|---|---|---|---|
| Persistência no PostgreSQL | Aprovado | `persistePagamentoEConsultaVinculoComOMembro` | `registraPagamentoPagoVinculadoAMembroExistente` | CT11 |
| Consulta por membro | Aprovado em teste unitário | Nenhum teste específico de consulta por membro identificado; o teste de persistência confere o vínculo do pagamento com o membro | `consultaPagamentosDoMembro`; `rejeitaConsultaDeMembroInexistente` | — |
| Validação de valor positivo | Aprovado | `servicoRejeitaValorInvalidoSemPersistirPagamento`; `constraintDoBancoRejeitaValorNaoPositivo` | `rejeitaValorNaoPositivoOuComMaisDeDuasCasas` (3 variantes); `rejeitaValorNulo` | CT12 |
| Validação do status PAGO | Aprovado | `servicoRejeitaStatusInvalidoSemPersistirPagamento`; `constraintDoBancoRejeitaStatusDiferenteDePago` | `rejeitaStatusDiferenteDePago` (4 variantes) | CT12 |
| Relacionamento com membro | Aprovado | `chaveEstrangeiraRejeitaMembroInexistente`; `persistePagamentoEConsultaVinculoComOMembro` | `rejeitaRegistroSemMembro`; `rejeitaRegistroQuandoMembroNaoExiste` | CT12 |

**Observações**

- As regras de valor e status são verificadas em duas camadas: no serviço, antes de persistir, e no banco, pelos `CHECK` da V4 (`valor > 0` e `status = 'PAGO'`).
- A entidade `Pagamento` mapeia `valor` como `BigDecimal` com `precision = 12, scale = 2`, compatível com `NUMERIC(12, 2)`, e `status` com tamanho 4 e `@Pattern("PAGO")`.
- A consulta por membro está aprovada apenas em teste unitário, com o repositório simulado. Não foi identificado teste que execute essa consulta contra o PostgreSQL.

## 3. Testes de Histórico de Atividades

| Item | Resultado | Integração com PostgreSQL (`HistoricoAtividadePostgresIntegrationTest`) | Unitário (`HistoricoAtividadeServiceTest`) | CT |
|---|---|---|---|---|
| Persistência no PostgreSQL | Aprovado | `persisteAtividadeComMembroEFuncionarioEConsultaPorAmbos` | `registraAtividadeComMembroEFuncionarioResponsavelExistentes` | CT13 |
| Consulta por membro | Aprovado | `persisteAtividadeComMembroEFuncionarioEConsultaPorAmbos` | `consultaAtividadesPorMembroExistente`; `rejeitaConsultaPorMembroInexistente` | CT13 |
| Relacionamento com membro | Aprovado | `chaveEstrangeiraRejeitaMembroInexistente` | `rejeitaRegistroSemMembro`; `rejeitaRegistroQuandoMembroNaoExiste` | CT13 |
| Relacionamento com funcionário | Aprovado | `chaveEstrangeiraRejeitaFuncionarioInexistente`; `chaveEstrangeiraTambemExigeFuncionarioResponsavel` | `rejeitaRegistroSemFuncionarioResponsavel`; `rejeitaRegistroQuandoFuncionarioNaoExiste`; `consultaAtividadesPorFuncionarioExistente` | CT13 |
| Validação do tempo de execução | Aprovado | `checkDoBancoRejeitaTempoNaoPositivo`; `servicoRejeitaDadosInvalidosSemPersistirAtividade` | `rejeitaTempoDeExecucaoNaoPositivo` (2 variantes); `rejeitaTempoNulo` | CT13 |

**Observações**

- As duas FKs da V5 (`fk_historico_atividades_membros` e `fk_historico_atividades_funcionarios`) são exercitadas no banco, inclusive a obrigatoriedade do funcionário responsável.
- A entidade `HistoricoAtividade` mapeia o funcionário pela coluna `funcionario_responsavel_id`, com o mesmo nome usado na migration.
- O teste de persistência também consulta as atividades por funcionário responsável.

## 4. Isolamento dos dados

| Item | Resultado | Observação | CT |
|---|---|---|---|
| Testes com dois membros diferentes | Pendente | Nenhum teste identificado pelo nome do método; a confirmação depende da leitura do corpo dos testes. | CT10, CT13 |
| Consultas retornaram somente os registros do membro solicitado | Pendente | Sem teste específico identificado. | CT10, CT13 |

**Observação da revisão de código:** as consultas por membro (`findByMembro_Id`) são derivadas pelo Spring Data a partir do nome do método e filtram por `membro.id`. Não há `@Query` manual nas consultas por membro que possa omitir o filtro. Essa análise reduz o risco, mas não substitui um teste que compare os resultados de dois membros.

## 5. Execução dos testes

**Comando:** `mvn test` (executado com `.\mvnw.cmd test`), concluído em 13/11/2026 às 11:46

- Testes executados: 126
- Falhas: 0
- Erros: 0
- Ignorados: 0
- Resultado: `BUILD SUCCESS`

**Comando:** `mvn clean verify` (executado com `.\mvnw.cmd clean verify`), concluído em 13/11/2026 às 11:49

- Testes executados: 126 (soma dos relatórios em `target/surefire-reports/`)
- Falhas: 0
- Erros: 0
- Cobertura: instruções 80% (965/1.192); branches 68% (92/134); linhas 270/322; métodos 84/104; classes 15/15. Verificação `jacoco:check` aprovada (`All coverage checks have been met.`). A meta do projeto, de 75% de instruções, foi atendida.
- Resultado: `BUILD SUCCESS`

### Testes por classe

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

### Cobertura por pacote

| Pacote | Instruções | Branches |
|---|---|---|
| `br.edu.unisul.vigor.service` | 79% | 65% |
| `br.edu.unisul.vigor.controller` | 71% | 65% |
| `br.edu.unisul.vigor.model` | 85% | n/a |
| `br.edu.unisul.vigor.validation` | 100% | 85% |
| `br.edu.unisul.vigor.config` | 100% | n/a |
| `br.edu.unisul.vigor` | 37% | n/a |
| **Total** | **80%** | **68%** |

Os relatórios são gerados localmente em `target/surefire-reports/` e `target/site/jacoco/index.html` e não são versionados.

## 6. Problemas encontrados

### 6.1. Falhas nos testes

Nenhuma falha ou erro foi registrado nos testes automatizados.

### 6.2. Itens sem evidência específica

| Item | Seção | Situação |
|---|---|---|
| Consulta de treino por ID | 1 | Sem teste específico |
| Consulta de pagamentos por membro no PostgreSQL | 2 | Coberta apenas por teste unitário |
| Isolamento dos dados entre dois membros | 4 | Sem teste específico identificado |

### 6.3. Achados da revisão

Os achados abaixo não foram corrigidos nesta task, conforme a regra de não alterar migrations ou a estrutura do banco sem aprovação da equipe.

**Achado 1 — Índice ausente em `historico_atividades.funcionario_responsavel_id`**

A coluna é usada pela consulta `findByFuncionarioResponsavel_Id` e pela verificação da FK quando um funcionário é excluído. Sem índice, as duas operações percorrem a tabela inteira, o que tende a ficar mais lento conforme o histórico cresce. A ausência já está registrada em [relacionamentos.md](../architecture/database/relacionamentos.md).

Sugestão, caso aprovada, em uma nova migration sem alterar a V5:

```sql
-- V6__cria_indice_historico_funcionario.sql
CREATE INDEX idx_historico_atividades_funcionario_responsavel_id
    ON historico_atividades (funcionario_responsavel_id);
```

**Achado 2 — Método sem uso em `MembroRepository`**

`MembroRepository.findByCpf` não é chamado em nenhum ponto do projeto. A verificação de CPF duplicado usa `existsByCpf`. Sugestão: avaliar a remoção do método ou mantê-lo, caso haja uso planejado.

### 6.4. Problemas de ambiente local

Problemas resolvidos durante a preparação do ambiente, sem relação com o código. Ficam registrados para facilitar a configuração por outros membros da equipe.

| Mensagem | Causa | Solução |
|---|---|---|
| `No compiler is provided in this environment. Perhaps you are running on a JRE rather than a JDK?` | Maven executado com Java 8 JRE | Instalação do JDK 21 e configuração de `JAVA_HOME` |
| `java.net.ConnectException: Connection refused: getsockopt` | PostgreSQL não instalado na máquina | Instalação do PostgreSQL e criação do banco `vigor` |

Os testes de integração também exigem a variável de ambiente `DB_PASSWORD` definida no terminal em que o Maven é executado.

## 7. Conclusão

A persistência e as consultas de treinos, pagamentos e histórico de atividades foram validadas por testes automatizados executados contra PostgreSQL local, com 126 testes executados sem falhas, erros ou testes ignorados. Os relacionamentos com membro e funcionário, as validações de valor, status e tempo de execução e o bloqueio de exclusão de membros com registros vinculados estão aprovados.

Permanecem pendentes de evidência específica a consulta de treino por ID, a consulta de pagamentos por membro no PostgreSQL e o isolamento dos dados entre dois membros. A conclusão da task depende da avaliação da equipe sobre essas pendências e sobre os dois achados da seção 6.3.

**Pull Request:** [Adicionar link]

---

## Anexo A — Entidades × migrations

Todas as colunas das migrations têm campo correspondente nas entidades, com o mesmo nome, tipo compatível e a mesma obrigatoriedade. Nenhuma entidade possui `@OneToMany` ou `cascade`.

### `Membro` × `membros` (V1)

| Coluna | Campo | Resultado |
|---|---|---|
| `id BIGINT GENERATED BY DEFAULT AS IDENTITY` | `Long id`, `GenerationType.IDENTITY` | Consistente |
| `nome VARCHAR(150) NOT NULL` | `String nome`, `length = 150`, `nullable = false` | Consistente |
| `cpf VARCHAR(11) NOT NULL`, `uk_membros_cpf UNIQUE` | `String cpf`, `length = 11`, `@UniqueConstraint(name = "uk_membros_cpf")` | Consistente |
| `telefone VARCHAR(20) NOT NULL` | `String telefone`, `length = 20`, `nullable = false` | Consistente |
| `endereco VARCHAR(255) NOT NULL` | `String endereco`, `length = 255`, `nullable = false` | Consistente |
| `data_cadastro DATE NOT NULL` | `LocalDate dataCadastro`, `updatable = false` | Consistente |

### `Funcionario` × `funcionarios` (V2)

| Coluna | Campo | Resultado |
|---|---|---|
| `id BIGINT GENERATED BY DEFAULT AS IDENTITY` | `Long id`, `GenerationType.IDENTITY` | Consistente |
| `nome VARCHAR(150) NOT NULL`, `CHECK` não vazio | `String nome`, `length = 150`, `@NotBlank` | Consistente |
| `cargo TEXT NOT NULL`, `CHECK` não vazio | `String cargo`, `columnDefinition = "text"`, `@NotBlank` | Consistente |
| `login TEXT NOT NULL`, `CHECK` não vazio, índice único `lower(btrim(login))` | `String login`, `columnDefinition = "text"`, `@NotBlank`; consulta `findByLoginNormalizado` | Consistente |
| `senha_hash TEXT NOT NULL`, `CHECK` não vazio | `String senhaHash`, `name = "senha_hash"`, `@NotBlank` | Consistente |

### `Treino` × `treinos` (V3)

| Coluna | Campo | Resultado |
|---|---|---|
| `membro_id BIGINT NOT NULL`, FK `fk_treinos_membros` | `Membro membro`, `@ManyToOne(optional = false)`, `@JoinColumn(name = "membro_id", nullable = false)` | Consistente |
| `tipo TEXT NOT NULL`, `CHECK` não vazio | `String tipo`, `@NotBlank` | Consistente |
| `descricao TEXT NOT NULL`, `CHECK` não vazio | `String descricao`, `@NotBlank` | Consistente |
| `duracao_minutos INTEGER NOT NULL`, `CHECK > 0` | `Integer duracaoMinutos`, `@Positive` | Consistente |
| `data_inicio DATE NOT NULL` | `LocalDate dataInicio`, `@NotNull` | Consistente |

### `Pagamento` × `pagamentos` (V4)

| Coluna | Campo | Resultado |
|---|---|---|
| `membro_id BIGINT NOT NULL`, FK `fk_pagamentos_membros` | `Membro membro`, `@ManyToOne(optional = false)`, `@JoinColumn(name = "membro_id", nullable = false)` | Consistente |
| `valor NUMERIC(12, 2) NOT NULL`, `CHECK > 0` | `BigDecimal valor`, `precision = 12, scale = 2`, `@DecimalMin` exclusivo, `@Digits(integer = 10, fraction = 2)` | Consistente |
| `data_pagamento DATE NOT NULL` | `LocalDate dataPagamento`, `@NotNull` | Consistente |
| `status VARCHAR(4) NOT NULL`, `CHECK = 'PAGO'` | `String status`, `length = 4`, `@Pattern(regexp = "PAGO")` | Consistente |

### `HistoricoAtividade` × `historico_atividades` (V5)

| Coluna | Campo | Resultado |
|---|---|---|
| `membro_id BIGINT NOT NULL`, FK `fk_historico_atividades_membros` | `Membro membro`, `@ManyToOne(optional = false)`, `@JoinColumn(name = "membro_id", nullable = false)` | Consistente |
| `funcionario_responsavel_id BIGINT NOT NULL`, FK `fk_historico_atividades_funcionarios` | `Funcionario funcionarioResponsavel`, `@ManyToOne(optional = false)`, `@JoinColumn(name = "funcionario_responsavel_id", nullable = false)` | Consistente |
| `atividade TEXT NOT NULL`, `CHECK` não vazio | `String atividade`, `@NotBlank` | Consistente |
| `data_atividade DATE NOT NULL` | `LocalDate dataAtividade`, `@NotNull` | Consistente |
| `tempo_execucao_minutos INTEGER NOT NULL`, `CHECK > 0` | `Integer tempoExecucaoMinutos`, `@Positive` | Consistente |

## Anexo B — Relacionamentos no PostgreSQL

### Chaves estrangeiras

Consulta executada no banco `vigor`:

```sql
SELECT conrelid::regclass AS tabela, conname AS nome_constraint, pg_get_constraintdef(oid) AS definicao
FROM pg_constraint
WHERE contype = 'f' AND connamespace = 'public'::regnamespace
ORDER BY conrelid::regclass::text, conname;
```

| Tabela | Constraint | Definição | Resultado |
|---|---|---|---|
| `historico_atividades` | `fk_historico_atividades_funcionarios` | `FOREIGN KEY (funcionario_responsavel_id) REFERENCES funcionarios(id) ON DELETE RESTRICT` | Consistente |
| `historico_atividades` | `fk_historico_atividades_membros` | `FOREIGN KEY (membro_id) REFERENCES membros(id) ON DELETE RESTRICT` | Consistente |
| `pagamentos` | `fk_pagamentos_membros` | `FOREIGN KEY (membro_id) REFERENCES membros(id) ON DELETE RESTRICT` | Consistente |
| `treinos` | `fk_treinos_membros` | `FOREIGN KEY (membro_id) REFERENCES membros(id) ON DELETE RESTRICT` | Consistente |

### Índices das colunas de relacionamento

Consulta executada no banco `vigor`:

```sql
SELECT tablename, indexname, indexdef
FROM pg_indexes
WHERE schemaname = 'public' AND tablename IN ('treinos', 'pagamentos', 'historico_atividades')
ORDER BY tablename, indexname;
```

| Coluna | Índice | Resultado |
|---|---|---|
| `treinos.membro_id` | `idx_treinos_membro_id` | Presente |
| `pagamentos.membro_id` | `idx_pagamentos_membro_id` | Presente |
| `historico_atividades.membro_id` | `idx_historico_atividades_membro_id` | Presente |
| `historico_atividades.funcionario_responsavel_id` | — | Ausente (achado 1) |

A consulta também retornou os índices de chave primária `treinos_pkey`, `pagamentos_pkey` e `historico_atividades_pkey`.

### Teste manual de exclusão

Executado no `psql`, dentro de uma transação desfeita ao final:

```sql
BEGIN;
WITH m AS (
    INSERT INTO membros (nome, cpf, telefone, endereco, data_cadastro)
    VALUES ('Teste', '99999999999', '48999999999', 'Rua Teste', CURRENT_DATE)
    RETURNING id
)
INSERT INTO treinos (membro_id, tipo, descricao, duracao_minutos, data_inicio)
SELECT id, 'Teste', 'Teste', 30, CURRENT_DATE FROM m;
DELETE FROM membros WHERE cpf = '99999999999';
ROLLBACK;
```

Resultado obtido:

```text
ERRO:  update or delete on table "membros" violates RESTRICT setting of foreign key constraint "fk_treinos_membros" on table "treinos"
DETAIL:  Key (id)=(1) is referenced from table "treinos".
ROLLBACK
```

O banco rejeitou a exclusão do membro com treino vinculado, e o `ROLLBACK` desfez os dados de teste.

## Anexo C — Repositórios e consultas

| Repositório | Método | Uso no código de produção | Resultado |
|---|---|---|---|
| `TreinoRepository` | `findByMembro_Id` | `TreinoService`, linha 65 | Consistente |
| `TreinoRepository` | `existsByMembro_Id` | `MembroService`, linha 93 | Consistente |
| `PagamentoRepository` | `findByMembro_Id` | `PagamentoService`, linha 62 | Consistente |
| `PagamentoRepository` | `existsByMembro_Id` | `MembroService`, linha 97 | Consistente |
| `HistoricoAtividadeRepository` | `findByMembro_Id` | `HistoricoAtividadeService`, linha 76 | Consistente |
| `HistoricoAtividadeRepository` | `findByFuncionarioResponsavel_Id` | `HistoricoAtividadeService`, linha 87 | Consistente |
| `HistoricoAtividadeRepository` | `existsByMembro_Id` | `MembroService`, linha 101 | Consistente |
| `MembroRepository` | `existsByCpf` | `MembroService`, linha 61 | Consistente |
| `MembroRepository` | `findByCpf` | Sem uso | Achado 2 |
| `FuncionarioRepository` | `findByLoginNormalizado` | `FuncionarioService`, linha 29 | Consistente |

- Não há consultas duplicadas. Os métodos `findByMembro_Id` e `existsByMembro_Id` repetem o nome em repositórios diferentes, mas cada um consulta uma entidade distinta.
- Antes de excluir um membro, `MembroService.excluir` verifica os três repositórios com vínculo a `membros`. O comportamento é coberto por `MembroServiceTest` e `MembroExclusaoPostgresIntegrationTest`.
- As consultas por membro usam `@EntityGraph` para carregar as associações. Isso é coerente com `spring.jpa.open-in-view=false` e com os `@ManyToOne` configurados como `LAZY`.
- `FuncionarioRepository.findByLoginNormalizado` usa a mesma expressão do índice único da V2 (`lower(btrim(login))`).

### Exclusão de membro com registros vinculados

| Item | Resultado | Integração com PostgreSQL (`MembroExclusaoPostgresIntegrationTest`) | Unitário (`MembroServiceTest`) | CT |
|---|---|---|---|---|
| Exclusão de membro sem vínculos | Aprovado | `servicoExcluiMembroSemRegistrosRelacionados` | `deveExcluirMembroSemRegistrosRelacionados` | CT08 |
| Bloqueio pela aplicação | Aprovado | `servicoBloqueiaExclusaoDeMembroComTreino`; `servicoBloqueiaExclusaoDeMembroComPagamento`; `servicoBloqueiaExclusaoDeMembroComHistorico` | `deveBloquearExclusaoDeMembroComTreino`; `deveBloquearExclusaoDeMembroComPagamento`; `deveBloquearExclusaoDeMembroComHistorico` | CT08 |
| Bloqueio pelo banco (`ON DELETE RESTRICT`) | Aprovado | `fkDeTreinoBloqueiaExclusaoDiretaNoBanco`; `fkDePagamentoBloqueiaExclusaoDiretaNoBanco`; `fkDeHistoricoBloqueiaExclusaoDiretaNoBanco` | — | CT08 |

## Histórico do documento

| Data | Alteração | Responsável |
|---|---|---|
| 13/11/2026 | Criação do relatório de validação da task L07 | Luan Milian Martins |

# Relacionamentos do Banco de Dados — VIGOR

## 1. Objetivo

Este documento descreve os relacionamentos definidos pelas migrations V1 a V5 e os mapeamentos JPA existentes para as tabelas `membros`, `funcionarios`, `treinos`, `pagamentos` e `historico_atividades`. Também registra as cardinalidades, regras de integridade e índices relacionados às consultas.

## 2. Visão geral

As migrations definem quatro chaves estrangeiras: `treinos.membro_id` e `pagamentos.membro_id` referenciam `membros(id)`; `historico_atividades.membro_id` referencia `membros(id)`; e `historico_atividades.funcionario_responsavel_id` referencia `funcionarios(id)`. Todas as colunas que compõem essas FKs são `NOT NULL`, e cada FK usa `ON DELETE RESTRICT`.

No código, `Treino` e `Pagamento` têm associações obrigatórias `@ManyToOne` com `Membro`. `HistoricoAtividade` tem associações obrigatórias `@ManyToOne` com `Membro` e com `Funcionario` responsável. Os repositórios declaram consultas por membro para treinos, pagamentos e histórico, além de consultas do histórico por funcionário responsável. Não há associações JPA inversas `@OneToMany` nas entidades `Membro` ou `Funcionario`.

As relações físicas e seus atributos estão alinhados à seção de relacionamentos e ao diagrama ER de [modelo-de-dados.md](modelo-de-dados.md), bem como às migrations descritas em [migrations.md](migrations.md).

## 3. Detalhamento

### 3.1 Membro e Treino

- **Entidade de origem:** `Treino`; entidade relacionada: `Membro`.
- **Cardinalidade:** um membro pode ter zero ou muitos treinos; cada treino referencia exatamente um membro (1:N).
- **Chave estrangeira:** `treinos.membro_id` → `membros.id`, constraint `fk_treinos_membros`.
- **Obrigatoriedade:** `membro_id` é `NOT NULL`; em JPA, `Treino.membro` usa `@ManyToOne(optional = false)` e `@JoinColumn(nullable = false)`.
- **Exclusão:** `ON DELETE RESTRICT` impede excluir o membro enquanto houver treinos vinculados.
- **Consultas:** `TreinoRepository.findByMembro_Id` consulta os treinos do membro; `existsByMembro_Id` é utilizado para verificar vínculo antes da exclusão do membro.
- **Regras relacionadas:** RN07–RN09 e RF06–RF07.

### 3.2 Membro e Pagamento

- **Entidade de origem:** `Pagamento`; entidade relacionada: `Membro`.
- **Cardinalidade:** um membro pode ter zero ou muitos pagamentos; cada pagamento referencia exatamente um membro (1:N).
- **Chave estrangeira:** `pagamentos.membro_id` → `membros.id`, constraint `fk_pagamentos_membros`.
- **Obrigatoriedade:** `membro_id` é `NOT NULL`; em JPA, `Pagamento.membro` usa `@ManyToOne(optional = false)` e `@JoinColumn(nullable = false)`.
- **Exclusão:** `ON DELETE RESTRICT` impede excluir o membro enquanto houver pagamentos vinculados.
- **Consultas:** `PagamentoRepository.findByMembro_Id` consulta os pagamentos do membro; `existsByMembro_Id` é utilizado para verificar vínculo antes da exclusão do membro.
- **Regras relacionadas:** RN10–RN13 e RF08.

### 3.3 Membro e Histórico de Atividades

- **Entidade de origem:** `HistoricoAtividade`; entidade relacionada: `Membro`.
- **Cardinalidade:** um membro pode ter zero ou muitos registros de atividade; cada registro referencia exatamente um membro (1:N).
- **Chave estrangeira:** `historico_atividades.membro_id` → `membros.id`, constraint `fk_historico_atividades_membros`.
- **Obrigatoriedade:** `membro_id` é `NOT NULL`; em JPA, `HistoricoAtividade.membro` usa `@ManyToOne(optional = false)` e `@JoinColumn(nullable = false)`.
- **Exclusão:** `ON DELETE RESTRICT` impede excluir o membro enquanto houver atividades vinculadas.
- **Consultas:** `HistoricoAtividadeRepository.findByMembro_Id` consulta o histórico do membro; `existsByMembro_Id` é utilizado para verificar vínculo antes da exclusão do membro.
- **Regras relacionadas:** RN14–RN17 e RF09.

### 3.4 Funcionário e Histórico de Atividades

- **Entidade de origem:** `HistoricoAtividade`; entidade relacionada: `Funcionario` responsável.
- **Cardinalidade:** um funcionário pode estar relacionado a zero ou muitos registros de atividade; cada registro referencia exatamente um funcionário (1:N).
- **Chave estrangeira:** `historico_atividades.funcionario_responsavel_id` → `funcionarios.id`, constraint `fk_historico_atividades_funcionarios`.
- **Obrigatoriedade:** `funcionario_responsavel_id` é `NOT NULL`; em JPA, `HistoricoAtividade.funcionarioResponsavel` usa `@ManyToOne(optional = false)` e `@JoinColumn(nullable = false)`.
- **Exclusão:** `ON DELETE RESTRICT` impede excluir um funcionário referenciado por registros de atividade.
- **Consultas:** `HistoricoAtividadeRepository.findByFuncionarioResponsavel_Id` consulta atividades pelo funcionário responsável.
- **Regras relacionadas:** RN14–RN16 e RF09.

## 4. Integridade referencial e índices

As quatro FKs são nomeadas nas migrations e rejeitam referências a membros ou funcionários inexistentes. Todas usam `ON DELETE RESTRICT`; nenhuma relação utiliza exclusão em cascata.

A exclusão de membros é protegida tanto pela aplicação quanto pelo banco. `MembroService.excluir` verifica se existem treinos, pagamentos ou registros de histórico e bloqueia a operação com mensagem específica. Se houver uma condição concorrente entre essa verificação e a exclusão, as FKs continuam impedindo a remoção no banco. O controller oferece `POST /membros/{id}/excluir` e retorna a mensagem ao usuário. Essa implementação atende ao bloqueio exigido por RN06, RF05, CA08 e CA19, sem apagar os registros relacionados.

Índices e restrições de unicidade registrados nas migrations:

- `uk_membros_cpf`: constraint `UNIQUE` em `membros(cpf)`, que também cria o índice único usado pelo PostgreSQL para impor unicidade.
- `ux_funcionarios_login_normalizado`: índice único em `funcionarios(lower(btrim(login)))`, que considera caixa e espaços comuns nas extremidades.
- `idx_treinos_membro_id`: índice em `treinos(membro_id)`, associado às consultas e verificações por membro.
- `idx_pagamentos_membro_id`: índice em `pagamentos(membro_id)`, associado às consultas e verificações por membro.
- `idx_historico_atividades_membro_id`: índice em `historico_atividades(membro_id)`, associado às consultas e verificações por membro.

A V5 não declara índice adicional em `historico_atividades(funcionario_responsavel_id)`. A consulta por funcionário está definida no repositório, mas não há índice específico para essa coluna nas migrations.

## 5. Pendências

- A estrutura física e os mapeamentos dos quatro relacionamentos estão definidos nas migrations e entidades atuais; não há pendência de nome de FK ou coluna para essas relações.
- As classes de integração PostgreSQL incluem verificações de persistência, consulta e bloqueio pelas FKs, mas dependem de `DB_PASSWORD` e precisam ser executadas em ambiente PostgreSQL para comprovar esse comportamento.

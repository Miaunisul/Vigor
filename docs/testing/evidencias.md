# VIGOR — Registro de Evidências

## 1. Objetivo

Registrar as evidências de execução, testes, cobertura de código e integração contínua do VIGOR, permitindo acompanhar a qualidade do projeto de forma verificável.

## 2. Estado atual

Em 10/11/2026 foram registradas uma execução local de `mvn clean verify` e a primeira execução bem-sucedida da CI no GitHub Actions (run #27). Os resultados informados estão discriminados abaixo. Os relatórios HTML e XML do JaCoCo são gerados em `target/site/jacoco/`; na execução de CI, o relatório foi publicado como artefato `relatorio-jacoco`.

Em 13/11/2026 foram registradas novas execuções locais de `mvn test` e `mvn clean verify` na branch `feat/revisao-relacionamentos-banco`, com PostgreSQL local e as seis classes de testes de integração com PostgreSQL. Os resultados e a associação dos testes aos itens validados na task L07 estão na seção 4.

Em 16/11/2026, após a revisão do PR #38, foram adicionados cinco testes de integração com PostgreSQL e registrada nova execução local de `mvn clean verify`, com 131 testes aprovados (seção 4.11). Também foram registradas a falha da integração contínua nas execuções #35 a #37 (EV15), sua correção e as execuções #38 e #39 aprovadas.

Em 18/11/2026, na task L02 (Issue #31), os testes de integração de treinos, pagamentos e histórico de atividades foram conferidos contra a checklist da issue, e seis testes com PostgreSQL foram adicionados para os itens que ainda não estavam comprovados no banco. As execuções locais de `mvn test` e `mvn clean verify` registraram 145 testes aprovados (EV18 e EV19).

Em 20/11/2026, após a revisão do PR #41, o teste `servicoRejeitaStatusInvalidoSemPersistirPagamento` passou a usar um membro criado no próprio teste, e a tabela da seção 3 foi reorganizada para reunir as evidências EV01 a EV19 em ordem, com as subseções de detalhe da EV17 e da EV18 abaixo dela. Também foi registrada a execução #52 da CI, aprovada com 145 testes (EV20).

Em 21/11/2026, na branch `feat/d05-interface-pagamentos`, foi executado `./mvnw clean verify` após a implementação das telas de registro e consulta de pagamentos. A suíte completa registrou 238 testes, sem falhas, erros ou ignorados. O JaCoCo analisou 21 classes e apresentou 83% de cobertura de instruções e 64% de branches, com aprovação do gate de instruções de 75%. Os resultados locais e as verificações manuais da D05 estão registrados nas evidências EV21 a EV24. A execução da CI correspondente a esta entrega ainda não foi registrada.

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
| EV11 | Build do projeto | Execução local `mvn clean verify`, branch `feat/revisao-relacionamentos-banco`, 16/11/2026 | `BUILD SUCCESS`; verificação `jacoco:check` aprovada (`All coverage checks have been met.`) | Executado com sucesso |
| EV12 | Testes automatizados | Execução local `mvn clean verify`, branch `feat/revisao-relacionamentos-banco`, 16/11/2026 | 131 executados em 15 classes; 0 falhas; 0 erros; 0 ignorados | Executado com sucesso |
| EV13 | Testes de integração com PostgreSQL | Seis classes `*PostgresIntegrationTest`, incluídas no EV12, com os cinco testes adicionados em 16/11/2026 | 35 executados; 0 falhas; 0 erros; 0 ignorados. Detalhamento na seção 4.11. Não substitui a execução individual dos CT funcionais. | Executado com sucesso |
| EV14 | Cobertura de código | Medição local: `mvn clean verify`, JaCoCo 0.8.16, 16/11/2026 | Instruções 81% (977/1.192); branches 68% (92/134); linhas 274/322; métodos 88/104; classes 15/15 | Meta de instruções (75%) atendida |
| EV15 | Integração contínua | Workflow `CI - VIGOR`: execução #35 (PR #37, commit `c65a23d`) e execuções #36 e #37 (branch `feat/revisao-relacionamentos-banco`, PR #38, commit `226e577`), 13/11/2026 | Falha no passo "Executar testes, relatório e quality gate": `Tests run: 126, Failures: 0, Errors: 27`, causada por `Schema validation: missing table [funcionarios]` na imagem `ubuntu-26.04`, em que testes de integração com Flyway desligado passaram a rodar antes da criação das tabelas. A execução #34 (`develop`, commit `2eb72a0`, 10/11/2026) concluiu com sucesso, e os dois PRs alteram apenas documentação em relação a esse commit. | Causa identificada e corrigida em 16/11/2026 |
| EV16 | Integração contínua | Workflow `CI - VIGOR`, execuções #38 (push) e #39 (PR #38), commit `b5a4bb3`, branch `feat/revisao-relacionamentos-banco`, 16/11/2026, imagem `ubuntu-26.04`, PostgreSQL 18.7 | Flyway aplicou V1 a V5 no banco vazio; 131 executados; 0 falhas; 0 erros; 0 ignorados; `All coverage checks have been met.`; `BUILD SUCCESS`; relatório JaCoCo publicado como artefato `relatorio-jacoco` | Executado com sucesso |
| EV17 | Cadastro HTTP e persistência de CPF | CT01, CT03 e CT04 nos cenários abaixo | Oito cenários em `MembroCpfHttpPostgresIntegrationTest` | Aprovado localmente e na CI |
| EV18 | Testes automatizados | Execuções locais `mvn test` e `mvn clean verify`, branch `test/validacao-persistencia-modulos`, 18/11/2026 | 145 executados em 16 classes; 0 falhas; 0 erros; 0 ignorados. Inclui 49 testes de integração com PostgreSQL: 41 em classes do pacote `repository` e 8 em `MembroCpfHttpPostgresIntegrationTest`. Detalhamento abaixo. | Executado com sucesso |
| EV19 | Cobertura de código | Medição local: `mvn clean verify`, JaCoCo 0.8.16, 18/11/2026 | Instruções 83% (1.001/1.192); branches 68% (92/134); linhas 281/322; métodos 92/104; classes 15/15 | Meta de instruções (75%) atendida |
| EV20 | Integração contínua | Workflow `CI - VIGOR`, execução #52 (PR #41), commit `2097438`, branch `test/validacao-persistencia-modulos`, 18/11/2026, imagem `ubuntu-26.04`, PostgreSQL 18.7 | Flyway aplicou V1 a V5 no banco vazio; 145 executados; 0 falhas; 0 erros; 0 ignorados; `All coverage checks have been met.`; `BUILD SUCCESS`; relatório JaCoCo publicado como artefato `relatorio-jacoco` | Executado com sucesso |
| EV21 | Build e suíte automatizada da D05 | Execução local de `./mvnw clean verify`, branch `feat/d05-interface-pagamentos`, 21/11/2026, concluída às 18:54:44 (-03:00) | 238 testes; 0 falhas; 0 erros; 0 ignorados; `BUILD SUCCESS`. O total corresponde ao projeto inteiro. | Executado com sucesso |
| EV22 | Cobertura de código após a D05 | Relatório local JaCoCo 0.8.16 da execução EV21 | Instruções 83% (1.779/2.118); branches 64% (133/206); linhas 478/562; métodos 132/145; classes 21/21. Mensagem `All coverage checks have been met.` | Gate de instruções de 75% aprovado |
| EV23 | Controller, templates e persistência de pagamentos | Testes da D05 e testes existentes reexecutados na suíte EV21; detalhamento abaixo | Validação do formulário, atribuição de `PAGO` pelo controller, consulta por membro, renderização HTML, escape de conteúdo e ordenação da listagem no PostgreSQL. Execução específica de `PagamentoPostgresIntegrationTest`: 10 testes, sem falhas, erros ou ignorados. | Executado com sucesso |
| EV24 | Verificações manuais de pagamentos | Registro e listagem; filtro por membro; consulta após reiniciar a aplicação; uso do formulário com janela reduzida | Funcionamento confirmado pela responsável em ambiente local. Registro descritivo abaixo; não representa aprovação integral dos CT/VN nem avaliação completa de usabilidade. | Execução manual confirmada pela responsável |

### EV17 — Validação de CPF pelo fluxo HTTP

Execução local em 16/11/2026 com Java 21 e PostgreSQL 18.3 em uma instância temporária. O comando `mvnw.cmd --batch-mode --no-transfer-progress clean verify` foi executado com `DB_PASSWORD` e `SPRING_DATASOURCE_URL` configuradas para essa instância. O teste inicializou a aplicação Spring Boot com Tomcat em porta aleatória, enviou formulários HTTP para `POST /membros` e consultou a tabela `membros` por JDBC após cada cenário.

| Caso do plano | Método em `MembroCpfHttpPostgresIntegrationTest` | Resultado observado |
|---|---|---|
| CT01 e CT04 | `cadastraCpfValidoEPersisteSomenteDigitos` | Dois cadastros com CPFs válidos distintos, um com pontuação e outro sem, retornaram HTTP 302 para `/membros`. Em ambos os casos o CPF foi gravado com 11 dígitos. |
| CT03 | `rejeitaCpfInvalidoSemPersistir` | Quatro envios retornaram HTTP 200 com o formulário e a mensagem de CPF inválido. O banco ficou sem registro para as tentativas com dígito verificador incorreto, com e sem pontuação, e para as sequências `00000000000` e `11111111111`. |
| CT04 | `rejeitaCpfDuplicadoAposNormalizacaoEPreservaOriginal` | Dois envios duplicados com pontuação diferente do cadastro inicial, um em cada sentido, retornaram HTTP 200 com a mensagem de CPF já cadastrado. Em cada cenário, o banco manteve apenas o membro original com CPF normalizado. |

O relatório Surefire `target/surefire-reports/br.edu.unisul.vigor.controller.MembroCpfHttpPostgresIntegrationTest.txt` registrou 8 testes aprovados, sem falhas, erros ou ignorados. A suíte completa registrou 134 testes, sem falhas, erros ou ignorados, e o JaCoCo aprovou o mínimo de 75% de cobertura de instruções.

Em 16/11/2026, a execução #42 da CI executou `mvn --batch-mode --no-transfer-progress clean verify` com PostgreSQL 18 e aprovou os 134 testes e a verificação de cobertura JaCoCo. [Execução #42](https://github.com/Miaunisul/Vigor/actions/runs/43932828095)

Uma execução posterior encontrou uma dependência da ordem da suíte. Cinco classes de repositório desativavam o Flyway e falhavam quando iniciavam com o banco vazio. Essas classes passaram a usar as migrações padrão da aplicação. A revalidação local em banco vazio com `mvnw.cmd --batch-mode --no-transfer-progress -Dsurefire.runOrder=reversealphabetical clean verify` aplicou as cinco migrações no primeiro teste de repositório e aprovou os 134 testes e a verificação do JaCoCo. [Execução que identificou a falha](https://github.com/Miaunisul/Vigor/actions/runs/43933437066) · [Verificações do PR #39](https://github.com/Miaunisul/Vigor/pull/39/checks)

### EV18 — Validação de persistência dos módulos (task L02)

- **Responsável:** Luan Milian Martins
- **Task:** L02 — Validar persistência e consultas de treinos, pagamentos e histórico (Issue #31)
- **Branch:** `test/validacao-persistencia-modulos`, criada a partir da `develop` (commit `70b2d00`)

Os testes de integração de treinos, pagamentos e histórico de atividades foram conferidos item a item contra a checklist da Issue #31. Os itens que ainda não estavam comprovados no PostgreSQL foram complementados com seis testes, indicados como "(novo)" na tabela. Não houve alteração em migrations, na estrutura do banco ou no código de produção, e os testes rodam em transação desfeita ao final.

| Módulo | Item da checklist | Testes que comprovam |
|---|---|---|
| Treinos | Membro válido para relacionar ao treino | Métodos auxiliares `membroParaTeste` e `criarMembroTransitorio` |
| Treinos | Treino salvo no banco | `persisteTreinoVinculadoEConsultaPeloMembro` |
| Treinos | Consulta pelo ID | `consultaTreinoPorIdRetornaOsDadosPersistidos`; `consultaTreinoPorIdInexistenteRetornaVazio` |
| Treinos | Consulta por membro retorna somente os treinos dele | `consultaPorMembroNaoRetornaTreinosDeOutroMembro` |
| Treinos | Membro inexistente rejeitado | `chaveEstrangeiraRejeitaMembroInexistente` |
| Treinos | Duração positiva | `servicoRejeitaDuracaoInvalidaSemPersistirTreino`; `checkDoBancoRejeitaDuracaoNaoPositiva` |
| Treinos | Campos obrigatórios rejeitados quando inválidos | `checkDoBancoRejeitaTipoVazio`; `checkDoBancoRejeitaDescricaoVazia` (novo); `bancoExigeDataDeInicio` (novo) |
| Pagamentos | Salvo com valor positivo | `persistePagamentoEConsultaVinculoComOMembro`; `consultaPagamentoPersistidoRetornaValorDataEStatusGravados` (novo) |
| Pagamentos | Data de pagamento persistida | `consultaPagamentoPersistidoRetornaValorDataEStatusGravados` (novo) |
| Pagamentos | Status salvo é `PAGO` | `consultaPagamentoPersistidoRetornaValorDataEStatusGravados` (novo); `consultaPorMembroRetornaSomenteOsPagamentosDoMembro` |
| Pagamentos | Consulta por membro retorna os pagamentos corretos | `consultaPorMembroRetornaSomenteOsPagamentosDoMembro` |
| Pagamentos | Membro inexistente rejeitado | `chaveEstrangeiraRejeitaMembroInexistente` |
| Pagamentos | Valores zero ou negativos rejeitados | `servicoRejeitaValorInvalidoSemPersistirPagamento`; `constraintDoBancoRejeitaValorNaoPositivo`; `constraintDoBancoRejeitaValorNegativo` (novo) |
| Pagamentos | Banco impede status diferente de `PAGO` | `constraintDoBancoRejeitaStatusDiferenteDePago` |
| Histórico | Vinculado ao membro correto | `persisteAtividadeComMembroEFuncionarioEConsultaPorAmbos`; `consultaPorMembroNaoRetornaAtividadesDeOutroMembro` |
| Histórico | Funcionário responsável obrigatório | `chaveEstrangeiraTambemExigeFuncionarioResponsavel` |
| Histórico | Atividade salva corretamente | `consultaAtividadePersistidaRetornaDescricaoDataETempoGravados` (novo) |
| Histórico | Data e tempo de execução persistidos | `consultaAtividadePersistidaRetornaDescricaoDataETempoGravados` (novo) |
| Histórico | Consulta por membro retorna somente o histórico dele | `consultaPorMembroNaoRetornaAtividadesDeOutroMembro` |
| Histórico | Membro inexistente rejeitado | `chaveEstrangeiraRejeitaMembroInexistente` |
| Histórico | Funcionário inexistente rejeitado | `chaveEstrangeiraRejeitaFuncionarioInexistente` |
| Histórico | Tempo de execução zero ou negativo rejeitado | `servicoRejeitaDadosInvalidosSemPersistirAtividade`; `checkDoBancoRejeitaTempoNaoPositivo`; `checkDoBancoRejeitaTempoNegativo` (novo) |
| Isolamento | Registros de dois membros; a consulta de um não retorna os registros do outro | `TreinoPostgresIntegrationTest.consultaPorMembroNaoRetornaTreinosDeOutroMembro`; `PagamentoPostgresIntegrationTest.consultaPorMembroRetornaSomenteOsPagamentosDoMembro`; `HistoricoAtividadePostgresIntegrationTest.consultaPorMembroNaoRetornaAtividadesDeOutroMembro` |

Os testes de cada módulo estão em `TreinoPostgresIntegrationTest`, `PagamentoPostgresIntegrationTest` e `HistoricoAtividadePostgresIntegrationTest`, no pacote `src/test/java/br/edu/unisul/vigor/repository/`.

Execuções locais em 18/11/2026, com Windows 11, JDK 21.0.13 e PostgreSQL 18 local: `mvn test` e `mvn clean verify` concluíram com 145 testes, 0 falhas, 0 erros e 0 ignorados, `All coverage checks have been met.` e `BUILD SUCCESS`. Os relatórios Surefire registraram 10 testes em `TreinoPostgresIntegrationTest`, 9 em `PagamentoPostgresIntegrationTest` e 10 em `HistoricoAtividadePostgresIntegrationTest`.

Observação para a equipe, sem alteração nesta task: `servicoRejeitaStatusInvalidoSemPersistirPagamento` confere que o membro obtido por `membroParaTeste()` não possui pagamentos. Como esse método reutiliza o primeiro membro existente no banco, o teste falharia, sem defeito real, em um banco em que esse membro já tenha pagamentos.

Ajuste após a revisão do PR #41, em 20/11/2026: o teste passou a usar um membro exclusivo, criado com `criarMembroTransitorio()`, e a verificação de que nenhum pagamento foi registrado deixou de depender dos dados existentes no banco. Após o ajuste, `mvn test` e `mvn clean verify` concluíram com 145 testes, 0 falhas, 0 erros e 0 ignorados, e `BUILD SUCCESS`.

### EV21 a EV24 — Interface de pagamentos (D05)

- **Responsável:** Maria Eduarda Souza dos Santos Ferreira.
- **Task:** D05 — Implementar interface de registro e consulta de pagamentos.
- **Branch:** `feat/d05-interface-pagamentos`.
- **Data:** 21/11/2026.
- **Ambiente:** Windows, Git Bash e PostgreSQL local, com `DB_PASSWORD` definida no terminal.
- **Aplicação:** Spring Boot 4.1.2.
- **Cobertura:** JaCoCo 0.8.16.

#### Execução automatizada

Comando da suíte completa:

```bash
./mvnw clean verify
```

Resultado observado:

```text
Tests run: 238, Failures: 0, Errors: 0, Skipped: 0
Analyzed bundle 'vigor' with 21 classes
All coverage checks have been met.
BUILD SUCCESS
```

A execução terminou em 21/11/2026 às 18:54:44 (-03:00), com duração total de 1 minuto e 8 segundos.

Os 238 testes pertencem à suíte completa do projeto, incluindo testes anteriores à D05. As 21 classes informadas pelo JaCoCo são classes de produção analisadas, não classes de teste.

Também foi executado:

```bash
./mvnw -Dtest=PagamentoPostgresIntegrationTest test
```

Essa execução terminou às 18:23:39 (-03:00), com 10 testes, nenhuma falha, nenhum erro e nenhum teste ignorado.

#### Testes relacionados à entrega

| Arquivo | Contribuição e comportamentos verificados |
|---|---|
| [PagamentoControllerTest.java](../../src/test/java/br/edu/unisul/vigor/controller/PagamentoControllerTest.java) | Criado na D05. Verifica abertura do formulário, listagem, consulta por membro, filtros inválidos, campos obrigatórios, valores e datas inválidos, membro inexistente, preservação de dados e atribuição de `PAGO` pelo controller, inclusive quando o formulário tenta enviar outro status. |
| [PagamentoServiceTest.java](../../src/test/java/br/edu/unisul/vigor/service/PagamentoServiceTest.java) | Testes existentes preservados e complementados na D05 para listagem geral, resultados vazios, consulta sem membro, limite de dígitos inteiros do valor e status nulo. |
| [PagamentoTemplateRenderingTest.java](../../src/test/java/br/edu/unisul/vigor/controller/PagamentoTemplateRenderingTest.java) | Criado na D05, com oito testes de HTML renderizado: formulário, status somente leitura, ausência de membros, lista vazia, formatação de valor e data, seleção do filtro, mensagens e escape do nome do membro. |
| [PagamentoPostgresIntegrationTest.java](../../src/test/java/br/edu/unisul/vigor/repository/PagamentoPostgresIntegrationTest.java) | Testes anteriores de persistência e integridade preservados. Na D05 foi acrescentado `listagemGeralOrdenaPorDataDecrescenteEPorIdNoDesempate`, que verifica a ordenação por data e ID decrescentes e o carregamento do membro associado. |

Os testes do controller utilizam serviços simulados. Os testes de templates verificam o HTML gerado pelo Thymeleaf, sem avaliar visualmente o CSS. Os testes PostgreSQL verificam os serviços e repositórios com banco real, em transações desfeitas ao final.

Essas verificações são complementares e não devem ser apresentadas como um único teste automatizado ponta a ponta do formulário até o PostgreSQL.

#### Cobertura da suíte completa

| Métrica | Resultado |
|---|---|
| Instruções | 83% exibidos no relatório; 1.779 cobertas de 2.118 |
| Branches | 64% exibidos no relatório; 133 cobertos de 206 |
| Linhas | 478 cobertas de 562 |
| Métodos | 132 cobertos de 145 |
| Classes | 21 cobertas de 21 |
| Gate JaCoCo | Aprovado para o mínimo de 75% de instruções |

Os percentuais correspondem ao projeto inteiro. Não representam cobertura exclusiva do módulo de pagamentos. Não há mínimo de cobertura de branches configurado no gate apresentado.

Relatórios locais da execução:

- `target/surefire-reports/`: resultados dos testes.
- `target/site/jacoco/index.html`: relatório HTML de cobertura.
- `target/site/jacoco/jacoco.xml`: relatório XML de cobertura.

Os arquivos de `target/` são gerados pelo build e não são versionados. Logs e captura do relatório foram conferidos para elaborar este registro; os caminhos acima indicam os artefatos locais.

#### Verificações manuais

Verificações realizadas no navegador e confirmadas pela responsável:

| Cenário | Procedimento | Resultado informado |
|---|---|---|
| Registro e listagem | Registrar um pagamento para um membro cadastrado e consultar a listagem. | Pagamento salvo e apresentado na listagem. |
| Consulta por membro | Selecionar um membro no filtro e consultar seus pagamentos. | A listagem apresentou somente os pagamentos do membro selecionado. |
| Persistência após reinício | Encerrar e iniciar novamente a aplicação, depois consultar os pagamentos. | O pagamento continuou disponível. |
| Formulário em janela reduzida | Diminuir a largura da janela e utilizar o formulário. | O formulário permaneceu utilizável. |

A verificação de janela reduzida não equivale a testes em todos os dispositivos ou navegadores. Não foram registradas dimensões exatas da janela nem uma avaliação completa de usabilidade.

O cenário de reinício evidencia a permanência do pagamento consultado; não declara aprovação integral do CA25 para todos os módulos.

#### Ajustes durante a validação

- Uma primeira execução dos testes PostgreSQL ignorou os dez testes porque `DB_PASSWORD` não estava disponível. Após configurar a variável, os dez testes foram executados e aprovados.
- As verificações de templates inicialmente utilizavam XPath, que interpretava o HTML como XML e gerava erros de análise. As verificações foram ajustadas para o HTML produzido pelo Thymeleaf.
- A verificação de escape foi ajustada para aceitar representações equivalentes de entidades HTML, preservando a exigência de exibir o nome como texto, sem transformá-lo em uma tag executável.
- Após os ajustes, a suíte completa foi aprovada conforme EV21.

#### Limites e acompanhamento

- A aprovação local não comprova a aprovação da CI desta branch. O link e o resultado da execução do GitHub Actions devem ser acrescentados após sua realização.
- Os testes descritos não comprovam autenticação nem proteção das rotas de pagamentos. Essa integração deve ser verificada com o módulo de login.
- O vínculo detalhado com os CT/VN deve ser atualizado no plano de testes e na matriz de rastreabilidade, preservando os identificadores existentes.
- As evidências anteriores foram preservadas. A D05 complementa testes existentes e não altera a autoria das contribuições anteriores.

## 4. Validação de persistência PostgreSQL — Task L07

- **Responsável:** Luan Milian Martins
- **Task:** L07 — Validar persistência e consultas de treinos, pagamentos e histórico de atividades.
- **Data da execução:** 13/11/2026
- **Branch:** `feat/revisao-relacionamentos-banco`
- **Relatório detalhado da conferência:** [relatorio-validacao-persistencia.md](relatorio-validacao-persistencia.md)

As subseções 4.1 a 4.10 registram a execução de 13/11/2026. Os itens pendentes nessa data foram complementados em 16/11/2026, conforme a subseção 4.11.

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
- `MembroRepository.findByCpf` não é utilizado no código de produção; a verificação de duplicidade usa `existsByCpf`. O método é usado apenas em `MembroCpfPostgresIntegrationTest`. *(Corrigido em 16/11/2026: o registro original afirmava que o método não era utilizado no projeto.)*

Problemas de ambiente local, resolvidos e sem relação com o código:

| Mensagem | Causa | Solução |
|---|---|---|
| `No compiler is provided in this environment. Perhaps you are running on a JRE rather than a JDK?` | Maven executado com Java 8 JRE | Instalação do JDK 21 e configuração de `JAVA_HOME` |
| `java.net.ConnectException: Connection refused: getsockopt` | PostgreSQL não instalado na máquina | Instalação do PostgreSQL e criação do banco `vigor` |

### 4.10. Conclusão

A persistência e as consultas de treinos, pagamentos e histórico de atividades foram validadas por testes automatizados executados contra PostgreSQL local, com 126 testes executados sem falhas, erros ou testes ignorados. Os relacionamentos com membro e funcionário, as validações de valor, status e tempo de execução e o bloqueio de exclusão de membros com registros vinculados estão aprovados.

Permanecem pendentes de evidência específica a consulta de treino por ID, a consulta de pagamentos por membro no PostgreSQL e o isolamento dos dados entre dois membros. A conclusão da task depende da avaliação da equipe sobre essas pendências.

**Pull Request:** [#38](https://github.com/Miaunisul/Vigor/pull/38)

### 4.11. Complementação de 16/11/2026

Após a revisão do PR #38, foram adicionados cinco testes de integração com PostgreSQL, sem alteração nas migrations, na estrutura do banco ou no código de produção. Todos rodam em transação desfeita ao final e leem os dados do PostgreSQL após limpar o contexto de persistência do Hibernate.

| Item pendente em 13/11/2026 | Novo resultado | Testes adicionados |
|---|---|---|
| Consulta de treino por ID | Aprovado | `TreinoPostgresIntegrationTest.consultaTreinoPorIdRetornaOsDadosPersistidos`; `TreinoPostgresIntegrationTest.consultaTreinoPorIdInexistenteRetornaVazio` |
| Consulta de pagamentos por membro no PostgreSQL | Aprovado | `PagamentoPostgresIntegrationTest.consultaPorMembroRetornaSomenteOsPagamentosDoMembro` |
| Testes com dois membros diferentes | Aprovado | `TreinoPostgresIntegrationTest.consultaPorMembroNaoRetornaTreinosDeOutroMembro`; `PagamentoPostgresIntegrationTest.consultaPorMembroRetornaSomenteOsPagamentosDoMembro`; `HistoricoAtividadePostgresIntegrationTest.consultaPorMembroNaoRetornaAtividadesDeOutroMembro` |
| Consultas retornaram somente os registros do membro solicitado | Aprovado | Os mesmos três testes |

**Comando:** `mvn clean verify` (executado com `.\mvnw.cmd clean verify`), 16/11/2026, concluído às 12:58

- Testes executados: 131 (126 anteriores e 5 novos)
- Falhas: 0
- Erros: 0
- Ignorados: 0
- Cobertura: instruções 81% (977/1.192); branches 68% (92/134); linhas 274/322; métodos 88/104; classes 15/15; `All coverage checks have been met.`
- Resultado: `BUILD SUCCESS`

| Classe | Testes em 13/11/2026 | Testes em 16/11/2026 |
|---|---|---|
| `TreinoPostgresIntegrationTest` | 5 | 8 |
| `PagamentoPostgresIntegrationTest` | 6 | 7 |
| `HistoricoAtividadePostgresIntegrationTest` | 7 | 8 |
| Demais 12 classes | 108 | 108 |
| **Total** | **126** | **131** |

Integração contínua: as execuções #35, #36 e #37 falharam (EV15) porque cinco classes de testes de integração desligavam o Flyway e dependiam de outra classe ter criado as tabelas. Com a nova imagem `ubuntu-26.04` da CI, a ordem dos testes mudou e essas classes passaram a rodar primeiro no banco vazio. Em 16/11/2026, a configuração `spring.flyway.enabled=false` foi removida dessas cinco classes, sem criar ou alterar migrations. O detalhamento está na seção 6.5 do [relatório](relatorio-validacao-persistencia.md). Após a correção, as execuções #38 e #39 da CI concluíram com sucesso, com 131 testes aprovados.

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

Na medição local de 16/11/2026 (EV14), a cobertura de instruções foi de 81% e a de branches permaneceu em 68%.

Na medição local de 18/11/2026 (EV19), a cobertura de instruções foi de 83% e a de branches permaneceu em 68%.

Na medição local de 21/11/2026, após a implementação da D05 (EV22), o relatório apresentou 83% de cobertura de instruções e 64% de branches, considerando 21 classes de produção. O gate de instruções de 75% foi aprovado. Essa medição corresponde à suíte completa do projeto.

## 8. Histórico

| Data | Alteração | Responsável |
|---|---|---|
| 26/10/2026 | Criação do modelo inicial de registro de evidências | Millena |
| 10/11/2026 | Registro dos resultados de `mvn clean verify` e JaCoCo 0.8.16 | Execução informada pela equipe |
| 10/11/2026 | Registro da primeira execução bem-sucedida do GitHub Actions, run #27, com PostgreSQL, Flyway, gate JaCoCo e artefato de cobertura | Execução informada pela equipe |
| 13/11/2026 | Registro das execuções locais de `mvn test` e `mvn clean verify` (126 testes), da cobertura JaCoCo e da validação de persistência PostgreSQL da task L07 (EV07 a EV10 e seção 4) | Luan Milian Martins |
| 16/11/2026 | Complementação da task L07 após a revisão do PR #38: cinco testes de integração, nova execução de `mvn clean verify` (131 testes), cobertura JaCoCo, registro da falha da CI e correção sobre `findByCpf` (EV11 a EV15 e seção 4.11); diagnóstico e correção da falha da CI; registro das execuções #38 e #39 da CI aprovadas | Luan Milian Martins |
| 16/11/2026 | Registro da validação HTTP de CPF com PostgreSQL e dos resultados da suíte local e da CI | Execução da issue #28 |
| 18/11/2026 | Registro da validação de persistência dos módulos da task L02 (Issue #31): seis testes de integração com PostgreSQL, execuções locais com 145 testes e cobertura JaCoCo (EV18 e EV19) | Luan Milian Martins |
| 20/11/2026 | Ajustes da revisão do PR #41: reorganização da tabela de evidências da seção 3, sem alteração do conteúdo das evidências; membro exclusivo no teste de status do pagamento; registro da execução #52 da CI (EV20) | Luan Milian Martins |
| 21/11/2026 | Registro da D05: testes de controller e templates, complementação dos testes de serviço e PostgreSQL, suíte completa com 238 testes aprovados, cobertura de instruções de 83% e verificações manuais de pagamentos (EV21 a EV24). | Maria Eduarda Souza dos Santos Ferreira |

Atualizar este histórico conforme novas evidências forem produzidas.

## 9. Situação do documento

Os resultados do build, dos testes automatizados, da medição JaCoCo, da primeira execução da CI e da validação de persistência PostgreSQL da task L07, incluindo a complementação de 16/11/2026, estão registrados. A falha da CI nas execuções #35 a #37 está registrada com a causa identificada; após a correção, as execuções #38 e #39 concluíram com sucesso. A seção 4 associa os testes automatizados aos CT relacionados, sem substituir a execução individual dos CT. A EV17 registra os cenários de cadastro e validação de CPF executados por HTTP. As demais verificações funcionais e de usabilidade seguem pendentes até a execução individual.

A EV18 registra a validação de persistência e consultas de treinos, pagamentos e histórico de atividades da task L02.

A EV20 registra a execução da CI do PR #41, da task L02.

As evidências EV21 a EV24 registram a validação local da interface de pagamentos na D05, incluindo testes automatizados, cobertura e verificações manuais específicas. Essas verificações atualizam parcialmente as pendências funcionais mencionadas acima, sem declarar aprovação de todos os CT/VN ou da avaliação completa de usabilidade. A CI e a integração com autenticação desta entrega ainda precisam de evidências próprias.
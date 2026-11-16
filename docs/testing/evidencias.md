# VIGOR — Registro de Evidências

## 1. Objetivo

Registrar as evidências de execução, testes, cobertura de código e integração contínua do VIGOR, permitindo acompanhar a qualidade do projeto de forma verificável.

## 2. Estado atual

Em 10/11/2026 foram registradas uma execução local de `mvn clean verify` e a primeira execução bem-sucedida da CI no GitHub Actions (run #27). Os resultados informados estão discriminados abaixo. Os relatórios HTML e XML do JaCoCo são gerados em `target/site/jacoco/`; na execução de CI, o relatório foi publicado como artefato `relatorio-jacoco`.

## 3. Evidências de execução

| ID | Verificação | CT/VN e cenário a registrar | Evidência esperada | Situação |
|---|---|---|---|---|
| EV01 | Build do projeto | Execução local `mvn clean verify` e GitHub Actions — CI - VIGOR, run #27, branch `feat/postgres-persistencia`, 10/11/2026 | `BUILD SUCCESS` nas execuções registradas | Executado com sucesso |
| EV02 | Testes automatizados | GitHub Actions — CI - VIGOR, run #27, branch `feat/postgres-persistencia`, 10/11/2026 | 37 executados; 0 falhas; 0 erros; 0 ignorados | Executado com sucesso |
| EV03 | Teste de contexto Spring e banco | `VigorApplicationTests.contextLoads`, incluído no run #27 | Contexto iniciado; PostgreSQL 18.6 conectado e Flyway executou V1. Isso não comprova testes funcionais de persistência CRUD. | Executado com sucesso |
| EV04 | Testes funcionais | CT/VN e cenário/variante efetivamente executados; pendente até execução | Capturas ou registro do teste de aceitação | Pendente |
| EV05 | Cobertura de código | Medição local: `mvn clean verify`, JaCoCo 0.8.16, 10/11/2026; CI: run #27 | Medição local: instruções 78%; branches 73%; linhas 98/117; métodos 24/35; classes 6/6. Na CI, JaCoCo analisou 6 classes e o gate de instruções de 75% passou (`All coverage checks have been met.`); o percentual exato da CI não foi informado. Relatório publicado como artefato `relatorio-jacoco`. | Gate de instruções aprovado na CI; percentual exato da CI não disponível |
| EV06 | Integração contínua | Workflow `CI - VIGOR`, execução #27, branch `feat/postgres-persistencia`, 10/11/2026 | `BUILD SUCCESS`; relatório JaCoCo publicado como artefato `relatorio-jacoco` | Executado com sucesso |

| EV07 | Cadastro HTTP e persistência de CPF | CT01, CT03 e CT04 nos cenários abaixo | Oito cenários em `MembroCpfHttpPostgresIntegrationTest` | Aprovado localmente |

### EV07 — Validação de CPF pelo fluxo HTTP

Execução local em 16/11/2026 com Java 21 e PostgreSQL 18.3 em uma instância temporária. O comando `mvnw.cmd --batch-mode --no-transfer-progress clean verify` foi executado com `DB_PASSWORD` e `SPRING_DATASOURCE_URL` configuradas para essa instância. O teste inicializou a aplicação Spring Boot com Tomcat em porta aleatória, enviou formulários HTTP para `POST /membros` e consultou a tabela `membros` por JDBC após cada cenário.

| Caso do plano | Método em `MembroCpfHttpPostgresIntegrationTest` | Resultado observado |
|---|---|---|
| CT01 e CT04 | `cadastraCpfValidoEPersisteSomenteDigitos` | Dois cadastros com CPFs válidos distintos, um com pontuação e outro sem, retornaram HTTP 302 para `/membros`. Em ambos os casos o CPF foi gravado com 11 dígitos. |
| CT03 | `rejeitaCpfInvalidoSemPersistir` | Quatro envios retornaram HTTP 200 com o formulário e a mensagem de CPF inválido. O banco ficou sem registro para as tentativas com dígito verificador incorreto, com e sem pontuação, e para as sequências `00000000000` e `11111111111`. |
| CT04 | `rejeitaCpfDuplicadoAposNormalizacaoEPreservaOriginal` | Dois envios duplicados com pontuação diferente do cadastro inicial, um em cada sentido, retornaram HTTP 200 com a mensagem de CPF já cadastrado. Em cada cenário, o banco manteve apenas o membro original com CPF normalizado. |

O relatório Surefire `target/surefire-reports/br.edu.unisul.vigor.controller.MembroCpfHttpPostgresIntegrationTest.txt` registrou 8 testes aprovados, sem falhas, erros ou ignorados. A suíte completa registrou 134 testes, sem falhas, erros ou ignorados, e o JaCoCo aprovou o mínimo de 75% de cobertura de instruções.

## 4. Como registrar uma evidência

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

## 5. Organização dos arquivos

Quando existirem evidências reais, organizar os arquivos de forma consistente. Exemplos:

- Capturas de tela dos testes de aceitação.
- Relatórios de testes automatizados.
- Relatório HTML de cobertura.
- Links para execuções do GitHub Actions e artefatos publicados; para o run #27, o artefato foi identificado como `relatorio-jacoco`.
- Registros de defeitos e respectivas verificações.

Não adicionar capturas fictícias nem declarar resultados que ainda não foram verificados.

## 6. Critério de cobertura

A meta definida para o projeto é de pelo menos 75% de cobertura de instruções. A medição local registrada foi 78% de instruções e 73% de branches. Separadamente, o quality gate JaCoCo da CI exige 75% de instruções e passou no run #27; o percentual exato reportado nessa execução não foi informado. Não se deve inferir que branches atingiu 75% na CI.

## 7. Histórico

| Data | Alteração | Responsável |
|---|---|---|
| 26/10/2026 | Criação do modelo inicial de registro de evidências | Millena |
| 10/11/2026 | Registro dos resultados de `mvn clean verify` e JaCoCo 0.8.16 | Execução informada pela equipe |
| 10/11/2026 | Registro da primeira execução bem-sucedida do GitHub Actions, run #27, com PostgreSQL, Flyway, gate JaCoCo e artefato de cobertura | Execução informada pela equipe |

| 16/11/2026 | Registro da validação HTTP de CPF com PostgreSQL e dos resultados da suíte local | Execução da issue #28 |

Atualizar este histórico conforme novas evidências forem produzidas.

## 8. Situação do documento

Os resultados do build, dos testes automatizados, da medição JaCoCo e da primeira execução da CI estão registrados. A EV07 registra os cenários de cadastro e validação de CPF executados por HTTP. As demais verificações funcionais e de usabilidade seguem pendentes até a execução individual.

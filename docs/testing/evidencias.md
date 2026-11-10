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

Atualizar este histórico conforme novas evidências forem produzidas.

## 8. Situação do documento

Os resultados do build, dos testes automatizados, da medição JaCoCo e da primeira execução da CI estão registrados. As evidências de execução dos CT/VN funcionais e de usabilidade continuam pendentes até que cada verificação seja executada e registrada individualmente.

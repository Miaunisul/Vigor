# VIGOR — Registro de Evidências

## 1. Objetivo

Registrar as evidências de execução, testes, cobertura de código e integração contínua do VIGOR, permitindo acompanhar a qualidade do projeto de forma verificável.

## 2. Estado atual

Em 10/11/2026 foi registrada uma execução real de `mvn clean verify`. Os resultados informados estão discriminados abaixo. Os relatórios HTML e XML do JaCoCo são artefatos gerados localmente em `target/site/jacoco/` e não são evidência versionada por este documento.

## 3. Evidências de execução

| ID | Verificação | CT/VN e cenário a registrar | Evidência esperada | Situação |
|---|---|---|---|---|
| EV01 | Build do projeto | `mvn clean verify`, 10/11/2026 | Build `SUCCESS` | Executado com sucesso |
| EV02 | Testes automatizados | `mvn clean verify`, 10/11/2026 | 37 executados; 0 falhas; 0 erros; 0 ignorados | Executado com sucesso |
| EV03 | Teste de contexto Spring | `VigorApplicationTests.contextLoads`, incluído na execução de 10/11/2026 | Contexto da aplicação iniciado; isso não comprova testes funcionais de persistência CRUD | Executado com sucesso |
| EV04 | Testes funcionais | CT/VN e cenário/variante efetivamente executados; pendente até execução | Capturas ou registro do teste de aceitação | Pendente |
| EV05 | Cobertura de código | `mvn clean verify`, JaCoCo 0.8.16, 10/11/2026 | Instruções: 78%; branches: 73%; linhas: 98/117; métodos: 24/35; classes: 6/6. Relatórios: `target/site/jacoco/index.html` e `target/site/jacoco/jacoco.xml` | Relatório gerado; instruções acima de 75%, branches abaixo de 75% |
| EV06 | Integração contínua | VN06 — execução do workflow; pendente até execução | Link da execução do GitHub Actions | Pendente |

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
- Links para execuções do GitHub Actions.
- Registros de defeitos e respectivas verificações.

Não adicionar capturas fictícias nem declarar resultados que ainda não foram verificados.

## 6. Critério de cobertura

A meta definida para o projeto é de pelo menos 75% de cobertura. Na execução registrada, a cobertura de instruções foi 78% e a de branches foi 73%; portanto, não se deve afirmar que todas as métricas atingiram 75%. O POM configura geração de relatórios, mas não configura um limite ou quality gate JaCoCo.

## 7. Histórico

| Data | Alteração | Responsável |
|---|---|---|
| 26/10/2026 | Criação do modelo inicial de registro de evidências | Millena |
| 10/11/2026 | Registro dos resultados de `mvn clean verify` e JaCoCo 0.8.16 | Execução informada pela equipe |

Atualizar este histórico conforme novas evidências forem produzidas.

## 8. Situação do documento

Os resultados do build, dos testes automatizados e da medição JaCoCo estão registrados. As evidências de execução dos CT/VN funcionais, usabilidade e CI continuam pendentes até que cada verificação seja executada e registrada individualmente.

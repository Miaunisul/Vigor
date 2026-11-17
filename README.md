# VIGOR — Sistema de gestão de academia

Aplicação acadêmica da A3 de Gestão e Qualidade de Software da UNISUL. O VIGOR organiza o cadastro de membros de uma academia e prevê, para a entrega final, autenticação de funcionários, gestão de treinos, registro de pagamentos recebidos e consulta do histórico de atividades.

Este README distingue requisitos aprovados, implementação disponível na interface e componentes existentes apenas nas camadas de serviço e persistência. A presença de um requisito ou de um serviço não significa que o fluxo correspondente esteja disponível ao usuário pela web.

## Objetivo e atores

O sistema atende à necessidade de manter dados de membros e informações relacionadas à rotina da academia em uma aplicação web com persistência PostgreSQL.

- **Funcionário da academia:** ator previsto para usar as funcionalidades internas. A interface atualmente disponível ainda não implementa login ou restrição de acesso.
- **Membro:** pessoa cadastrada e associada a treinos, pagamentos e atividades. Não acessa diretamente o sistema nesta entrega.

## Estado atual das funcionalidades

### Disponíveis na interface web

- Cadastro de membros pela página de formulário.
- Listagem dos membros cadastrados, com informações cadastrais.
- Exclusão de membro sem registros vinculados; a interface apresenta o resultado e o serviço bloqueia a operação quando há treinos, pagamentos ou atividades associados.

As rotas implementadas são GET /membros, GET /membros/novo, POST /membros e POST /membros/{id}/excluir. A rota / não está mapeada. Os templates existentes são membros/lista.html e membros/formulario.html.

### Implementadas em serviço e persistência, sem fluxo web

- **Membros:** cadastro, listagem, busca por identificador, validação e normalização de CPF, prevenção de duplicidade e exclusão segura.
- **Funcionários:** serviço que procura login normalizado e compara senha com hash BCrypt. Não há tela de login, controle de sessão/autorização das rotas nem operação de cadastro no serviço.
- **Treinos:** serviço para cadastrar treino associado a membro existente e consultar treinos por membro.
- **Pagamentos:** serviço para registrar e consultar pagamentos associados a membro; o status aceito é PAGO.
- **Histórico de atividades:** serviço para registrar e consultar atividades por membro ou funcionário responsável. O registro pela interface está fora do escopo da A3; os requisitos preveem dados disponibilizados por carga técnica documentada.

Os cinco modelos JPA, seus repositórios e as migrations V1 a V5 estão presentes. Serviços e entidades não equivalem a telas ou fluxos ponta a ponta.

### Ainda não implementadas na interface ou pendentes

- Autenticação web de funcionários e proteção das rotas internas.
- Atualização de membros (RF04).
- Telas e endpoints web para cadastro e consulta de treinos, registro de pagamentos e consulta do histórico.
- Procedimento documentado e automatizado para provisionar funcionários e disponibilizar dados iniciais do histórico.
- Testes de aceitação e usabilidade executados individualmente com evidências registradas; os CT/VN documentados continuam planejados até haver evidência específica.

## Requisitos funcionais e regras de negócio

Os requisitos oficiais estão em [requisitos.md](docs/requirements/requisitos.md), com regras em [regras-de-negocio.md](docs/requirements/regras-de-negocio.md) e critérios em [criterios-de-aceite.md](docs/requirements/criterios-de-aceite.md). Todos os RF01–RF09 fazem parte do escopo final aprovado nos documentos; o status da implementação web é o descrito acima.

| ID | Requisito funcional oficial | Regras relacionadas | Situação resumida |
|---|---|---|---|
| RF01 | Autenticar funcionários | RN18–RN20 | Verificação de credenciais BCrypt no serviço; login web e controle de acesso pendentes. |
| RF02 | Cadastrar membros | RN01–RN04 | Disponível na interface e persistido no PostgreSQL. |
| RF03 | Consultar membros | — | Listagem disponível na interface; busca por ID disponível no serviço. |
| RF04 | Atualizar membros | RN01–RN05 | Pendente; não há operação de atualização no controller ou serviço. |
| RF05 | Excluir membros | RN06 | Exclusão disponível na interface; bloqueio por vínculos no serviço e no banco. |
| RF06 | Cadastrar treinos | RN07–RN09 | Serviço e persistência implementados; interface pendente. |
| RF07 | Consultar treinos | RN07–RN09 | Consulta por membro implementada no serviço; interface pendente. |
| RF08 | Registrar pagamentos | RN10–RN13 | Serviço e persistência implementados para pagamentos recebidos (PAGO); interface pendente. |
| RF09 | Consultar histórico de atividades | RN14–RN17 | Serviço e persistência implementados; interface pendente. |

As regras gerais também incluem RN21, que impede persistir dados inválidos ou referências inexistentes e exige preservar dados válidos em operações rejeitadas. A regra de CPF inclui normalização, dígitos verificadores válidos, rejeição de sequências repetidas e unicidade após normalização. Pagamentos representam somente valores recebidos: não há contas a receber nem status pendentes.

## Requisitos não funcionais e qualidade

| ID | Critério oficial | Estado e verificação configurada |
|---|---|---|
| RNF01 | Segurança: acesso a funcionários autenticados e senhas com hash apropriado. | BCrypt e comparação de credenciais existem no serviço; autenticação e autorização web permanecem pendentes. |
| RNF02 | Desempenho sem travamentos/timeouts no ambiente de teste, com tempos observados. | Não há limite numérico definido; medições de desempenho ainda precisam ser registradas. |
| RNF03 | Usabilidade e mensagens claras. | Formulário de membro possui validações; avaliação de usabilidade permanece pendente. |
| RNF04 | Persistência PostgreSQL e integridade dos dados. | PostgreSQL, Flyway, validações de aplicação e constraints estão implementados; consulte as evidências e limitações em [evidencias.md](docs/testing/evidencias.md). |
| RNF05 | Manutenibilidade por separação de responsabilidades e análise estática. | Código organizado em pacotes de controller, service, repository, model e validation. Não foi identificada ferramenta de análise estática configurada no pom.xml; seu uso e resultado não estão comprovados. |
| RNF06 | Testes Maven, cobertura JaCoCo e CI. | Testes, relatório JaCoCo, gate de instruções e workflow GitHub Actions configurados. |

A avaliação exige cobertura mínima de **75% de instruções**. O quality gate JaCoCo aplica esse limite ao bundle do projeto durante verify; não existe limite configurado para branches.

## Arquitetura e tecnologias

O código segue separação em responsabilidades compatível com a decisão DEC03: controller recebe solicitações web, service concentra operações e regras, repository acessa dados, model contém entidades e templates renderizam páginas. A interface MVC atual cobre somente membros. Não se declara um padrão adicional além da estrutura observada.

| Tecnologia | Versão/configuração verificada | Responsabilidade |
|---|---|---|
| Java | 21 | Linguagem e runtime alvo. |
| Spring Boot | 4.1.2 | Framework e gerenciamento das dependências. |
| Maven | Wrapper configurado para 3.10.0 | Build e execução dos testes. |
| Spring MVC | Dependência spring-boot-starter-webmvc gerenciada pelo Spring Boot | Controllers web. |
| Thymeleaf | Starter Spring Boot gerenciado | Templates HTML. |
| Spring Data JPA / Hibernate | Dependências gerenciadas pelo Spring Boot; ddl-auto=validate | Repositórios, mapeamento e validação do schema. |
| PostgreSQL | Driver JDBC no projeto; serviço de CI usa imagem postgres:18 | Banco relacional. A versão do PostgreSQL local não é fixada pelo projeto. |
| Flyway | spring-boot-starter-flyway e módulo PostgreSQL | Versionamento e aplicação das migrations. |
| Bean Validation | Starter Spring Boot | Validação declarativa dos dados. |
| Spring Security Crypto | Dependência declarada | BCrypt para verificar hashes de senha; não fornece, por si só, login ou autorização web. |
| JUnit 5, Mockito e testes Spring | Dependências de teste gerenciadas pelo Spring Boot | Testes unitários e de integração. |
| JaCoCo | 0.8.16 | Relatórios HTML/XML e gate de cobertura de instruções. |
| GitHub Actions | Workflow CI - VIGOR | Build, testes, quality gate e publicação do relatório. |

No application.properties, a conexão padrão usa jdbc:postgresql://localhost:5432/vigor, usuário postgres e senha obtida de DB_PASSWORD. Hibernate valida o schema com spring.jpa.hibernate.ddl-auto=validate; Flyway aplica as migrations.

## Banco de dados e relacionamentos

Todas as tabelas usam BIGINT GENERATED BY DEFAULT AS IDENTITY como chave primária e os campos descritos como obrigatórios são NOT NULL nas migrations.

| Tabela | Colunas principais | Constraints e índices |
|---|---|---|
| membros | nome VARCHAR(150), cpf VARCHAR(11), telefone VARCHAR(20), endereco VARCHAR(255), data_cadastro DATE | uk_membros_cpf garante CPF único. A validação de formato, dígitos e normalização ocorre na aplicação. |
| funcionarios | nome VARCHAR(150), cargo TEXT, login TEXT, senha_hash TEXT | Checks para conteúdo não vazio; índice único ux_funcionarios_login_normalizado em lower(btrim(login)). BCrypt é responsabilidade da aplicação/provisionamento, não uma validação da migration. |
| treinos | membro_id BIGINT, tipo TEXT, descricao TEXT, duracao_minutos INTEGER, data_inicio DATE | FK para membro; checks de tipo/descrição não vazios e duração positiva; índice idx_treinos_membro_id. |
| pagamentos | membro_id BIGINT, valor NUMERIC(12,2), data_pagamento DATE, status VARCHAR(4) | FK para membro; valor maior que zero; status exclusivamente PAGO; índice idx_pagamentos_membro_id. |
| historico_atividades | membro_id BIGINT, funcionario_responsavel_id BIGINT, atividade TEXT, data_atividade DATE, tempo_execucao_minutos INTEGER | FKs para membro e funcionário; atividade não vazia e tempo positivo; índice idx_historico_atividades_membro_id. |

Relacionamentos: cada treino e pagamento pertence a um membro; cada registro de histórico pertence a um membro e a um funcionário responsável. Um membro pode ter zero ou vários treinos, pagamentos ou atividades; um funcionário pode responder por zero ou várias atividades. As quatro FKs usam ON DELETE RESTRICT, sem cascade. O serviço também bloqueia a exclusão de membros com vínculos. Detalhes estão em [modelo-de-dados.md](docs/architecture/database/modelo-de-dados.md) e [relacionamentos.md](docs/architecture/database/relacionamentos.md).

## Migrations Flyway

| Versão | Arquivo | Finalidade |
|---|---|---|
| V1 | V1__cria_tabela_membros.sql | Cria membros e a constraint única uk_membros_cpf. |
| V2 | V2__cria_funcionarios.sql | Cria funcionários, checks de campos não vazios e índice único de login normalizado. |
| V3 | V3__cria_treinos.sql | Cria treinos, FK restritiva para membros, checks e índice por membro. |
| V4 | V4__cria_pagamentos.sql | Cria pagamentos recebidos, FK restritiva, valor positivo, status PAGO e índice por membro. |
| V5 | V5__cria_historico_atividades.sql | Cria histórico com FKs restritivas para membro e funcionário responsável, checks e índice por membro. |

Os scripts ficam em src/main/resources/db/migration/ e são executados em ordem pelo Flyway. Migrations aplicadas não devem ser reescritas; alterações futuras recebem nova versão. A documentação registra validação das cinco migrations em banco vazio e validação do mapeamento pelo Hibernate. Consulte [migrations.md](docs/architecture/database/migrations.md) para o procedimento e as evidências disponíveis; a situação de cada banco deve ser conferida pelo histórico Flyway.

## Pré-requisitos e configuração

- Java 21.
- PostgreSQL instalado e em execução, com banco vigor acessível pelo usuário postgres.
- Variável de ambiente DB_PASSWORD disponível no terminal que executará a aplicação ou os testes.
- Use o Maven Wrapper (mvnw / mvnw.cmd); sua distribuição configurada é Maven 3.10.0.

A senha não deve ser escrita no README, no código ou em arquivos versionados. Os comandos abaixo usam um marcador local que deve ser substituído somente no terminal. As migrations pendentes podem ser aplicadas durante a inicialização; antes de usar um banco com dados relevantes, confira [ambientes.md](docs/architecture/database/ambientes.md) e o histórico Flyway.

## Executar a aplicação

Na raiz do repositório:

**Windows PowerShell:**

    $env:DB_PASSWORD = "<senha-local>"
    .\mvnw.cmd spring-boot:run

**Linux ou macOS (Bash):**

    export DB_PASSWORD="<senha-local>"
    ./mvnw spring-boot:run

Após iniciar, acesse http://localhost:8080/membros. Para abrir o formulário, acesse http://localhost:8080/membros/novo. A raiz / não possui rota.

## Testes e cobertura

Os testes automatizados podem ser executados com:

**Windows PowerShell:**

    $env:DB_PASSWORD = "<senha-local>"
    .\mvnw.cmd clean verify

**Linux ou macOS (Bash):**

    export DB_PASSWORD="<senha-local>"
    ./mvnw clean verify

verify executa os testes, gera os relatórios JaCoCo e avalia o quality gate. Relatórios: target/site/jacoco/index.html (HTML) e target/site/jacoco/jacoco.xml (XML). Testes de integração PostgreSQL precisam de um banco acessível e da configuração DB_PASSWORD; alguns testes são habilitados condicionalmente por essa variável.

### Testes presentes

- Unitários de serviços: MembroServiceTest, FuncionarioServiceTest, TreinoServiceTest, PagamentoServiceTest e HistoricoAtividadeServiceTest.
- Validação de entidade e utilitário: FuncionarioValidationTest e CpfUtilsTest.
- Controller web: MembroControllerTest.
- PostgreSQL/contexto: VigorApplicationTests, FuncionarioPostgresIntegrationTest, TreinoPostgresIntegrationTest, PagamentoPostgresIntegrationTest, HistoricoAtividadePostgresIntegrationTest, MembroCpfPostgresIntegrationTest e MembroExclusaoPostgresIntegrationTest.

A suíte automatizada informada para 10/11/2026 executou **126 testes**, com **0 falhas, 0 erros e 0 ignorados**; o build terminou com sucesso, o JaCoCo analisou **15 classes** e o quality gate foi aprovado. Não informamos percentual total de cobertura aqui porque não foi confirmado um percentual correspondente ao relatório desta execução. Esses resultados globais não comprovam, por si sós, que cada critério de aceitação ou teste manual esteja individualmente aprovado. O histórico detalhado está em [evidencias.md](docs/testing/evidencias.md).

## Integração contínua

O workflow [ci.yml](.github/workflows/ci.yml), chamado CI - VIGOR, roda em push para feat/**, develop e main, e em Pull Requests destinados a develop ou main. Usa Ubuntu, Java 21 (Temurin), serviço PostgreSQL postgres:18, credencial CI_DB_PASSWORD armazenada como secret e executa mvn --batch-mode --no-transfer-progress clean verify. O JaCoCo exige pelo menos 75% de instruções; o relatório é publicado como artefato relatorio-jacoco.

A evidência histórica registrada para o run #27, em 10/11/2026, relata 37 testes, PostgreSQL 18.6, execução da V1 e aprovação do gate. Essa execução antecede a suíte atual de 126 testes e não deve ser confundida com ela. O percentual exato da CI não está registrado.

## Estrutura do repositório

    Vigor/
    ├── .github/workflows/ci.yml
    ├── .mvn/wrapper/
    ├── docs/
    │   ├── architecture/
    │   │   ├── database/
    │   │   ├── arquitetura-geral.md
    │   │   ├── decisoes.md
    │   │   └── README.md
    │   ├── design/prototipo.md
    │   ├── diagrams/
    │   ├── planning/
    │   ├── requirements/
    │   └── testing/
    ├── src/
    │   ├── main/
    │   │   ├── java/br/edu/unisul/vigor/
    │   │   │   ├── config/
    │   │   │   ├── controller/
    │   │   │   ├── model/
    │   │   │   ├── repository/
    │   │   │   ├── service/
    │   │   │   └── validation/
    │   │   └── resources/
    │   │       ├── db/migration/
    │   │       └── templates/membros/
    │   └── test/java/br/edu/unisul/vigor/
    │       ├── controller/
    │       ├── model/
    │       ├── repository/
    │       ├── service/
    │       └── validation/
    ├── mvnw
    ├── mvnw.cmd
    ├── pom.xml
    ├── CONTRIBUTING.md
    ├── HELP.md
    └── README.md

## Equipe e responsabilidades planejadas

Estas são frentes de trabalho planejadas; não atribuem autoria individual de código, testes, commits ou evidências.

| Integrante | Responsabilidades planejadas |
|---|---|
| Millena Ferreira Rodrigues | Interface web, protótipo Figma e integração dos fluxos de cadastro e consulta. |
| Maria Eduarda Souza dos Santos Ferreira | Requisitos, regras de negócio e validações. |
| Luan Milian Martins | Modelo de domínio, persistência e consultas. |
| Emely Santos da Silveira | Testes de integração, CI, cobertura e evidências de qualidade. |

## Documentação complementar

- [Requisitos funcionais e não funcionais](docs/requirements/requisitos.md)
- [Regras de negócio](docs/requirements/regras-de-negocio.md)
- [Critérios de aceitação](docs/requirements/criterios-de-aceite.md)
- [Plano de testes](docs/testing/plano-de-testes.md) e [evidências](docs/testing/evidencias.md)
- [Matriz de rastreabilidade](docs/planning/matriz-de-rastreabilidade.md) e [plano de tasks](docs/planning/plano-de-tasks.md)
- [Arquitetura geral](docs/architecture/arquitetura-geral.md), [decisões](docs/architecture/decisoes.md) e [índice de arquitetura](docs/architecture/README.md)
- Banco de dados: [modelo de dados](docs/architecture/database/modelo-de-dados.md), [relacionamentos](docs/architecture/database/relacionamentos.md), [migrations](docs/architecture/database/migrations.md) e [ambientes](docs/architecture/database/ambientes.md)
- [Protótipo](docs/design/prototipo.md) e [diagramas](docs/diagrams/)

## Licença

Não há arquivo LICENSE nem licença definida nos metadados do pom.xml. A definição depende da confirmação do enunciado da A3 e da decisão da equipe; nenhuma licença é presumida neste README.

## Referências de qualidade

- [Relatório de evidências de testes e CI](docs/testing/evidencias.md)
- [Workflow da integração contínua](.github/workflows/ci.yml)

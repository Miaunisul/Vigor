# VIGOR — Sistema de gestão de academia

Aplicação acadêmica da A3 de Gestão e Qualidade de Software da UNISUL. O VIGOR reúne gestão de membros, treinos e pagamentos, autenticação de funcionários e consulta do histórico de atividades em uma aplicação web com persistência PostgreSQL.

Esta branch inclui a implementação da Issue #35 (M08): login de funcionários com Spring Security e BCrypt, proteção das rotas administrativas e consulta filtrada do histórico de atividades.

## Objetivo e atores

O sistema atende à necessidade de manter dados de membros e informações relacionadas à rotina da academia em uma aplicação web com persistência PostgreSQL.

- **Funcionário da academia:** acessa as funcionalidades internas após autenticar com login e senha provisionados administrativamente.
- **Membro:** pessoa cadastrada e associada a treinos, pagamentos e atividades. Não acessa diretamente o sistema nesta entrega.

## Estado atual das funcionalidades

### Disponíveis na interface web

- **Membros:** listagem, busca, cadastro, detalhes, edição e exclusão sujeita aos vínculos existentes.
- **Treinos:** listagem com busca por nome ou CPF, filtro por membro, paginação, cadastro, detalhes, edição e exclusão.
- **Pagamentos:** cadastro e consulta de pagamentos vinculados aos membros.
- **Login de funcionários:** autenticação por sessão, identificação do funcionário na navegação e logout.
- **Atividades:** consulta do histórico por membro, busca por nome ou CPF e filtros por data inicial e final. Os resultados são ordenados do mais recente para o mais antigo.

As páginas internas exigem autenticação. GET /login e os recursos estáticos do login são públicos; POST /login e POST /logout continuam protegidos por CSRF. GET /atividades é somente consulta: a Issue #35 não permite cadastro manual de atividades, e não existe endpoint público para cadastrar funcionários.

### Serviços e persistência

- **Membros:** cadastro, listagem, busca por identificador, validação e normalização de CPF, prevenção de duplicidade e exclusão segura.
- **Funcionários:** serviço que procura login normalizado e compara a senha com BCrypt. O hash não faz parte da identidade da sessão nem é enviado às páginas.
- **Pagamentos:** serviço para registrar e consultar pagamentos associados a membro; o status aceito é PAGO.
- **Histórico de atividades:** serviço para consultar por membro ou funcionário responsável, filtrar por membro, nome/CPF e período, e ordenar os resultados. O método técnico de registro existente permanece sem tela ou endpoint de cadastro manual nesta issue.

Os cinco modelos JPA, seus repositórios e as migrations V1 a V5 estão presentes. A M08 reutiliza esses componentes e não acrescenta migrations.

### Validações ainda dependentes de evidência específica

- Testes de aceitação e usabilidade executados individualmente com evidências registradas; os CT/VN documentados continuam planejados até haver evidência específica.

## Requisitos funcionais e regras de negócio

Os requisitos oficiais estão em [requisitos.md](docs/requirements/requisitos.md), com regras em [regras-de-negocio.md](docs/requirements/regras-de-negocio.md) e critérios em [criterios-de-aceite.md](docs/requirements/criterios-de-aceite.md). Todos os RF01–RF09 fazem parte do escopo final aprovado nos documentos; o status da implementação web é o descrito acima.

| ID | Requisito funcional oficial | Regras relacionadas | Situação resumida |
|---|---|---|---|
| RF01 | Autenticar funcionários | RN18–RN20 | Login web com Spring Security, BCrypt, sessão e proteção das rotas internas. |
| RF02 | Cadastrar membros | RN01–RN04 | Disponível na interface e persistido no PostgreSQL. |
| RF03 | Consultar membros | — | Listagem disponível na interface; busca por ID disponível no serviço. |
| RF04 | Atualizar membros | RN01–RN05 | Disponível na interface e persistido no PostgreSQL. |
| RF05 | Excluir membros | RN06 | Exclusão disponível na interface; bloqueio por vínculos no serviço e no banco. |
| RF06 | Cadastrar treinos | RN07–RN09 | Formulário web vincula o treino a membro existente; validações da entidade e do serviço são reaproveitadas. |
| RF07 | Consultar treinos | RN07–RN09 | Listagem web consulta somente os treinos do membro selecionado. |
| RF08 | Registrar pagamentos | RN10–RN13 | Cadastro e consulta disponíveis na interface para pagamentos recebidos (PAGO). |
| RF09 | Consultar histórico de atividades | RN14–RN17 | Tela disponível com filtros por membro, busca por nome/CPF e período; sem cadastro manual de atividades. |

As regras gerais também incluem RN21, que impede persistir dados inválidos ou referências inexistentes e exige preservar dados válidos em operações rejeitadas. A regra de CPF inclui normalização, dígitos verificadores válidos, rejeição de sequências repetidas e unicidade após normalização. Pagamentos representam somente valores recebidos: não há contas a receber nem status pendentes.

## Requisitos não funcionais e qualidade

| ID | Critério oficial | Estado e verificação configurada |
|---|---|---|
| RNF01 | Segurança: acesso a funcionários autenticados e senhas com hash apropriado. | Spring Security protege as rotas internas; BCrypt valida as credenciais; CSRF permanece habilitado nos formulários POST. |
| RNF02 | Desempenho sem travamentos/timeouts no ambiente de teste, com tempos observados. | Não há limite numérico definido; medições de desempenho ainda precisam ser registradas. |
| RNF03 | Usabilidade e mensagens claras. | Formulário de membro possui validações; avaliação de usabilidade permanece pendente. |
| RNF04 | Persistência PostgreSQL e integridade dos dados. | PostgreSQL, Flyway, validações de aplicação e constraints estão implementados; consulte as evidências e limitações em [evidencias.md](docs/testing/evidencias.md). |
| RNF05 | Manutenibilidade por separação de responsabilidades e análise estática. | Código organizado em pacotes de controller, service, repository, model e validation. Não foi identificada ferramenta de análise estática configurada no pom.xml; seu uso e resultado não estão comprovados. |
| RNF06 | Testes Maven, cobertura JaCoCo e CI. | Testes, relatório JaCoCo, gate de instruções e workflow GitHub Actions configurados. |

A avaliação exige cobertura mínima de **75% de instruções**. O quality gate JaCoCo aplica esse limite ao bundle do projeto durante verify; não existe limite configurado para branches.

## Arquitetura e tecnologias

O código segue separação em responsabilidades compatível com a decisão DEC03: controller recebe solicitações web, service concentra operações e regras, repository acessa dados, model contém entidades e templates renderizam páginas. Spring Security autentica funcionários e protege os fluxos internos.

| Tecnologia | Versão/configuração verificada | Responsabilidade |
|---|---|---|
| Java | 21 | Linguagem e runtime alvo. |
| Spring Boot | 4.1.2 | Framework e gerenciamento das dependências. |
| Maven | Wrapper configurado para 3.10.0 | Build e execução dos testes. |
| Spring MVC | Dependência spring-boot-starter-webmvc gerenciada pelo Spring Boot | Controllers web. |
| Thymeleaf | Starter Spring Boot gerenciado | Templates HTML. |
| HTML e CSS | Templates Thymeleaf e folhas de estilo responsivas do projeto | Interface dos módulos, login e consulta do histórico. |
| Spring Data JPA / Hibernate | Dependências gerenciadas pelo Spring Boot; ddl-auto=validate | Repositórios, mapeamento e validação do schema. |
| PostgreSQL | Driver JDBC no projeto; serviço de CI usa imagem postgres:18 | Banco relacional. A versão do PostgreSQL local não é fixada pelo projeto. |
| Flyway | spring-boot-starter-flyway e módulo PostgreSQL | Versionamento e aplicação das migrations. |
| Bean Validation | Starter Spring Boot | Validação declarativa dos dados. |
| Spring Security | Starter Spring Boot e spring-security-test | Formulário de login, sessão, autorização das rotas, logout e testes de segurança. |
| BCrypt | BCryptPasswordEncoder do Spring Security | Hash e verificação das senhas de funcionários. |
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

A variável DB_PASSWORD deve existir somente no ambiente do terminal que inicia a aplicação ou os testes. Não a coloque em argumentos, no README, no código ou em arquivos versionados. Os exemplos abaixo pedem a senha sem exibi-la e removem a variável do ambiente ao terminar. As migrations pendentes podem ser aplicadas durante a inicialização; antes de usar um banco com dados relevantes, confira [ambientes.md](docs/architecture/database/ambientes.md) e o histórico Flyway.

## Executar a aplicação

Na raiz do repositório:

**Windows PowerShell:**

~~~powershell
$secure = Read-Host "DB_PASSWORD" -AsSecureString
$bstr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure)
try {
    $env:DB_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($bstr)
    .\mvnw.cmd spring-boot:run
} finally {
    $env:DB_PASSWORD = $null
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bstr)
    $secure.Dispose()
}
~~~

**Linux ou macOS (Bash):**

~~~bash
read -rsp "DB_PASSWORD: " DB_PASSWORD
printf '\n'
export DB_PASSWORD
./mvnw spring-boot:run
status=$?
unset DB_PASSWORD
exit "$status"
~~~

Após iniciar, acesse http://localhost:8080/login. Entre com o login e a senha de um funcionário provisionado. O login válido redireciona para /membros.

## Acesso administrativo inicial

O funcionário administrador inicial provisionado no banco local é:

| Campo | Valor |
|---|---|
| Nome | Funcionário 1 |
| Cargo | Administrador |
| Login | funcionariovigor@gmail.com |
| ID no banco local | 182 |

A senha foi definida localmente durante o provisionamento e deve ser mantida em segredo. Ela não está neste README nem deve ser incluída no código, em arquivos de configuração ou nos commits.

### Provisionar outros funcionários administradores

O sistema não oferece cadastro público de funcionários. O provisionamento é uma operação administrativa, feita somente por uma pessoa autorizada com acesso ao PostgreSQL:

1. Solicite a senha em um terminal com entrada oculta, confirme-a e mantenha-a apenas em memória.
2. Gere o hash usando o PasswordEncoder configurado pela aplicação (BCryptPasswordEncoder).
3. Em uma transação, confirme a unicidade de lower(btrim(login)) e faça um INSERT parametrizado somente nas colunas nome, cargo, login e senha_hash da tabela funcionarios.
4. Confirme a transação apenas após o INSERT bem-sucedido; em caso de erro, faça rollback.

Nunca grave a senha em texto puro ou o hash em um arquivo, argumento de processo, histórico do shell, log ou commit. O hash BCrypt é persistido apenas no banco para a autenticação.

## Testes e cobertura

Os testes automatizados podem ser executados com:

**Windows PowerShell:**

~~~powershell
$secure = Read-Host "DB_PASSWORD" -AsSecureString
$bstr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure)
try {
    $env:DB_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($bstr)
    .\mvnw.cmd clean verify
} finally {
    $env:DB_PASSWORD = $null
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bstr)
    $secure.Dispose()
}
~~~

**Linux ou macOS (Bash):**

~~~bash
read -rsp "DB_PASSWORD: " DB_PASSWORD
printf '\n'
export DB_PASSWORD
./mvnw clean verify
status=$?
unset DB_PASSWORD
exit "$status"
~~~

verify executa os testes, gera os relatórios JaCoCo e avalia o quality gate de pelo menos 75% de cobertura de instruções. Relatórios: target/site/jacoco/index.html (HTML) e target/site/jacoco/jacoco.xml (XML). Os testes de integração PostgreSQL precisam de um banco acessível e de DB_PASSWORD; os que usam a anotação EnabledIfEnvironmentVariable são habilitados por essa variável.

### Testes presentes

- Unitários de serviços: MembroServiceTest, FuncionarioServiceTest, TreinoServiceTest, PagamentoServiceTest e HistoricoAtividadeServiceTest.
- Validação de entidade e utilitário: FuncionarioValidationTest e CpfUtilsTest.
- Controller e segurança web: MembroControllerTest, MembroTemplateRenderingTest, TreinoControllerTest, TreinoTemplateRenderingTest, FuncionarioSecurityFlowTest e AtividadeTemplateRenderingTest.
- PostgreSQL/contexto: VigorApplicationTests, MembroCpfHttpPostgresIntegrationTest, TreinoHttpPostgresIntegrationTest, FuncionarioPostgresIntegrationTest, TreinoPostgresIntegrationTest, PagamentoPostgresIntegrationTest, HistoricoAtividadePostgresIntegrationTest, MembroCpfPostgresIntegrationTest, MembroAtualizacaoPostgresIntegrationTest e MembroExclusaoPostgresIntegrationTest.

Consulte o resultado da execução local do Maven e o relatório JaCoCo correspondente antes de registrar qualquer percentual. Os resultados globais não comprovam, por si sós, que cada critério de aceitação ou teste manual esteja individualmente aprovado. O histórico de evidências está em [evidencias.md](docs/testing/evidencias.md).

Os testes e a cobertura da implementação M07 estão registrados em [relatorio-m07.md](docs/testing/relatorio-m07.md).

## Integração contínua

O workflow [ci.yml](.github/workflows/ci.yml), chamado CI - VIGOR, roda em push para feat/**, develop e main, e em Pull Requests destinados a develop ou main. Usa Ubuntu, Java 21 (Temurin), serviço PostgreSQL postgres:18, credencial CI_DB_PASSWORD armazenada como secret e executa mvn --batch-mode --no-transfer-progress clean verify. O JaCoCo exige pelo menos 75% de instruções; o relatório é publicado como artefato relatorio-jacoco.

A evidência histórica registrada para o run # 27, em 10/11/2026, relata 37 testes, PostgreSQL 18.6, execução da V1 e aprovação do gate. Essa execução antecede a implementação descrita neste README e não representa o resultado de validação desta branch.

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
    │   │   │   ├── security/
    │   │   │   ├── service/
    │   │   │   └── validation/
    │   │   └── resources/
    │   │       ├── db/migration/
    │   │       ├── static/css/
    │   │       ├── static/js/
    │   │       └── templates/
    │   │           ├── membros/
    │   │           ├── atividades/
    │   │           ├── pagamentos/
    │   │           ├── login.html
    │   │           └── treinos/
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


## Equipe e responsabilidades

A distribuição de tarefas e responsabilidades é atualizada continuamente conforme as demandas do projeto, a criação e atribuição de issues e a realização das entregas. Por isso, pode diferir da divisão inicialmente registrada no plano de testes piloto, que representa o planejamento daquela etapa.

As tabelas desta seção serão atualizadas à medida que novas tarefas forem atribuídas e novas contribuições forem realizadas, incluindo os links dos PRs, das implementações e dos testes correspondentes. As atividades planejadas ou em andamento serão diferenciadas das entregas concluídas, preservando o registro da contribuição efetiva de cada integrante.

| Integrante | Responsabilidades | Issues relacionadas |
|---|---|---|
| Millena Ferreira Rodrigues | Documentação de arquitetura e banco de dados, consolidação do README, persistência PostgreSQL e implementação das interfaces de membros, treinos, login e histórico. | M01 (#13), M02 (#14), M03 (auditoria dos casos de teste, compartilhada com Maria Eduarda), M04 (#23), M05 (#24), M06 (#30), M07 (#32), e M08 (#35). |
| Maria Eduarda Souza dos Santos Ferreira | Requisitos, regras de negócio, critérios de aceitação, protótipo Figma, auditoria documental dos casos de teste e implementação da interface de registro e consulta de pagamentos, com testes e documentação correspondentes. | D01 (#11), D02 (#12), D03 (#27), D04 (#33), D05 (#34) e participação na M03. |
| Emely Santos da Silveira | Validação integrada de CPF pelo fluxo HTTP e PostgreSQL; revisão final dos testes, evidências, cobertura e documentação consolidada por Maria Eduarda. | E01 (#28) e E02 (#36). |
| Luan Milian Martins | Revisão dos relacionamentos e consultas do banco; validação da persistência de treinos, pagamentos e histórico. | L01 (#29) e L02 (#31). |

### Acompanhamento por integrante, funcionalidade e testes

| Aluno | Funcionalidade ou contribuição sob sua responsabilidade | Links dos testes e contribuições |
|---|---|---|
| Millena Ferreira Rodrigues | Persistência PostgreSQL e interfaces de membros, treinos, login e histórico. | [README v1](https://github.com/Miaunisul/Vigor/pull/2), [Organiza diagramas e modelos de dados](https://github.com/Miaunisul/Vigor/pull/19), [Consolida arquitetura e BD](https://github.com/Miaunisul/Vigor/pull/22) e [Persistência PostgreSQL](https://github.com/Miaunisul/Vigor/pull/26). |
| Maria Eduarda Souza dos Santos Ferreira | Requisitos, protótipo, regras de CPF e revisão dos critérios e casos de teste. Na D05, implementação das telas de registro, listagem e consulta de pagamentos por membro, com validações, testes e evidências. | [RFs, RNFs, RNs e CAs](https://github.com/Miaunisul/Vigor/pull/16), [Protótipo](https://github.com/Miaunisul/Vigor/pull/17), [Alinhamento de regras e validação de CPF](https://github.com/Miaunisul/Vigor/pull/37), [Revisão documental D04](https://github.com/Miaunisul/Vigor/pull/48), [Plano de testes](docs/testing/plano-de-testes.md), [Critérios de aceitação](docs/requirements/criterios-de-aceite.md) e [CI da D05](https://github.com/Miaunisul/Vigor/actions/runs/44972713889). Implementação e testes de pagamentos no [PR da D05 #49](https://github.com/Miaunisul/Vigor/pull/49). |
| Emely Santos da Silveira | Validação de CPF pelo fluxo HTTP integrado ao PostgreSQL, consolidação e revisão final da qualidade. | [Validação de CPF pelo fluxo HTTP](https://github.com/Miaunisul/Vigor/pull/39). |
| Luan Milian Martins | Relacionamentos, consultas e persistência de treinos, pagamentos e histórico. | [Validação de relacionamentos e consultas](https://github.com/Miaunisul/Vigor/pull/38). |

### Testes automatizados e contribuições registradas

A tabela abaixo relaciona as contribuições de cada integrante aos arquivos de teste e commits correspondentes. Um mesmo arquivo pode ter contribuições de mais de uma pessoa. Os links mostram o trabalho registrado; os resultados de execução dos testes são apresentados nas evidências e no GitHub Actions.

| Integrante | Contribuição registrada | Arquivos de teste | Commits e PRs relacionados |
|---|---|---|---|
| Millena Ferreira Rodrigues | Testes unitários do serviço de membros e da validação e normalização de CPF. | [MembroServiceTest](src/test/java/br/edu/unisul/vigor/service/MembroServiceTest.java) e [CpfUtilsTest](src/test/java/br/edu/unisul/vigor/validation/CpfUtilsTest.java) | [Testes do serviço](https://github.com/Miaunisul/Vigor/commit/8af9db4), [normalização](https://github.com/Miaunisul/Vigor/commit/ca2d1b7), [dígitos verificadores](https://github.com/Miaunisul/Vigor/commit/7c84452) e [validação customizada](https://github.com/Miaunisul/Vigor/commit/cbb26cb). |
| Maria Eduarda Souza dos Santos Ferreira | Revisão documental das regras e critérios. Na D05, criação dos testes de controller e renderização das telas de pagamentos; complementação dos testes existentes do serviço e da integração PostgreSQL para listagem, validações e ordenação, preservando as contribuições anteriores. | [PagamentoControllerTest](src/test/java/br/edu/unisul/vigor/controller/PagamentoControllerTest.java), [PagamentoTemplateRenderingTest](src/test/java/br/edu/unisul/vigor/controller/PagamentoTemplateRenderingTest.java), [PagamentoServiceTest](src/test/java/br/edu/unisul/vigor/service/PagamentoServiceTest.java) e [PagamentoPostgresIntegrationTest](src/test/java/br/edu/unisul/vigor/repository/PagamentoPostgresIntegrationTest.java). | [PR de alinhamento documental do CPF](https://github.com/Miaunisul/Vigor/pull/37). [Implementação e testes de pagamentos — PR #49](https://github.com/Miaunisul/Vigor/pull/49). |
| Emely Santos da Silveira | Teste integrado de CPF pelo fluxo HTTP com PostgreSQL e ajustes na aplicação das migrations na suíte de integração. | [MembroCpfHttpPostgresIntegrationTest](src/test/java/br/edu/unisul/vigor/controller/MembroCpfHttpPostgresIntegrationTest.java) e [MembroCpfPostgresIntegrationTest](src/test/java/br/edu/unisul/vigor/repository/MembroCpfPostgresIntegrationTest.java). | [Teste HTTP integrado](https://github.com/Miaunisul/Vigor/commit/fd84999) e [ajuste das migrations](https://github.com/Miaunisul/Vigor/commit/dbb477e). |
| Luan Milian Martins | Complementação dos testes de consultas por identificador, por membro e de isolamento entre membros; ajuste da configuração Flyway nos testes de integração. | [HistoricoAtividadePostgresIntegrationTest](src/test/java/br/edu/unisul/vigor/repository/HistoricoAtividadePostgresIntegrationTest.java), [PagamentoPostgresIntegrationTest](src/test/java/br/edu/unisul/vigor/repository/PagamentoPostgresIntegrationTest.java) e [TreinoPostgresIntegrationTest](src/test/java/br/edu/unisul/vigor/repository/TreinoPostgresIntegrationTest.java). | [Complementação das consultas](https://github.com/Miaunisul/Vigor/commit/70910f6) e [configuração Flyway](https://github.com/Miaunisul/Vigor/commit/b5a4bb3). |

Os resultados das verificações estão registrados em [Evidências de testes](docs/testing/evidencias.md).

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

Este projeto está licenciado sob a Licença MIT. Consulte o arquivo [LICENSE](LICENSE) para ver o texto completo da licença.

## Referências de qualidade

- [Relatório de evidências de testes e CI](docs/testing/evidencias.md)
- [Workflow da integração contínua](.github/workflows/ci.yml)

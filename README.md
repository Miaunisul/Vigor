# VIGOR — Sistema de Cadastro de Academia

Projeto acadêmico da A3 de Gestão e Qualidade de Software (GQS) da UNISUL.

> **Situação:** o código contém um fluxo inicial de cadastro e listagem de membros. A execução local com PostgreSQL e a persistência funcional ainda precisam de comprovação. As demais funcionalidades permanecem previstas no escopo final e serão implementadas por etapas.

## Problema e objetivo

Academias precisam manter informações cadastrais organizadas e consistentes. Dados incompletos, CPFs inválidos ou duplicados podem comprometer a qualidade dos registros e dificultar consultas.

O VIGOR é uma aplicação web para apoiar o cadastro e o gerenciamento de membros, treinos, pagamentos recebidos e histórico de atividades, com acesso interno por funcionários autenticados.

## Atores

- **Funcionário:** usuário interno da aplicação; deve autenticar-se para acessar as funcionalidades.
- **Membro:** pessoa cadastrada e associada a treinos, pagamentos e atividades; não acessa diretamente o sistema nesta entrega.
- **Equipe de desenvolvimento:** implementa e mantém o sistema. A divisão de trabalho planejada está em [Plano de tasks](docs/planning/plano-de-tasks.md).

Não há perfil de gestor com permissões próprias no escopo atual.

## Escopo funcional aprovado

Todos os RF01–RF09 integram o escopo final da entrega. A ordem de desenvolvimento não remove funcionalidades do escopo. Os IDs e suas definições são os dos [Requisitos oficiais](docs/requirements/requisitos.md).

| ID | Requisito |
|---|---|
| RF01 | Autenticar funcionários previamente cadastrados mediante login e senha e restringir funcionalidades internas a usuários autenticados. |
| RF02 | Cadastrar membros com nome, CPF, telefone e endereço, gerando automaticamente o identificador e a data de cadastro. |
| RF03 | Listar os membros cadastrados e consultar suas informações. |
| RF04 | Atualizar nome, CPF, telefone e endereço de um membro, validando os novos dados antes de salvá-los. |
| RF05 | Excluir membros sem registros vinculados e bloquear a exclusão quando houver treinos, pagamentos ou atividades associados. |
| RF06 | Cadastrar treino associado a membro existente, informando tipo, descrição, duração em minutos e data de início. |
| RF07 | Consultar os treinos associados a um membro. |
| RF08 | Registrar pagamento recebido de membro existente, informando valor e data e atribuindo status Pago. |
| RF09 | Consultar atividades registradas para um membro, apresentando atividade, data e tempo de execução. |

Funcionários serão previamente cadastrados por procedimento técnico. Os registros iniciais de atividades serão disponibilizados por carga técnica documentada; não haverá tela para registrar atividades nesta entrega. Contas a receber, pagamentos pendentes, atualização e exclusão de treinos, acesso de membros e gerenciamento de funcionários estão fora do escopo desta entrega. Consulte as [Regras de negócio](docs/requirements/regras-de-negocio.md) para os detalhes.

## Estado da implementação

| Área | Situação observada |
|---|---|
| Cadastro e listagem de membros | Há controller, serviço, repositório, entidade JPA e templates para formulário e listagem. O cadastro verifica duplicidade textual de CPF e a listagem consulta os registros. A execução local e a persistência real no PostgreSQL ainda não foram comprovadas. |
| Validação de membros | Parcial. O código exige nome e CPF e restringe o CPF ao padrão de 11 dígitos. Não valida os dígitos verificadores, não rejeita sequências repetidas e não normaliza pontuação. Telefone e endereço não estão marcados como obrigatórios na entidade, apesar de RN01 exigir ambos. |
| Autenticação, atualização e exclusão de membros | Planejadas; não há implementação correspondente identificada. |
| Treinos, pagamentos e histórico de atividades | Planejados; não há entidades ou fluxos correspondentes identificados. |

A presença de requisitos ou código não comprova que o comportamento foi executado ou aprovado. O estado das verificações deve ser acompanhado em [Evidências de testes](docs/testing/evidencias.md).

## Requisitos de qualidade

Os requisitos oficiais são detalhados em [requisitos.md](docs/requirements/requisitos.md). Seu estado deve ser comprovado por testes e evidências.

| ID | Requisito |
|---|---|
| RNF01 | Restringir o acesso às funcionalidades internas a funcionários autenticados e armazenar senhas por hash apropriado, sem persistência em texto puro. |
| RNF02 | Concluir os fluxos previstos sem travamentos ou erros de tempo limite no ambiente de testes documentado e registrar os tempos observados nas consultas. |
| RNF03 | Apresentar campos identificados, indicação de obrigatoriedade e mensagens claras de validação, sucesso e ausência de registros; avaliar a usabilidade dos fluxos principais. |
| RNF04 | Persistir dados válidos no PostgreSQL, mantê-los disponíveis após reiniciar a aplicação e impedir gravações que violem regras de validação e relacionamentos. |
| RNF05 | Separar responsabilidades de apresentação, regras de negócio e persistência, permitindo localizar e alterar cada responsabilidade e executar análise estática. |
| RNF06 | Permitir testes automatizados com Maven para cenários válidos e inválidos, gerar relatório JaCoCo com cobertura mínima de 75% e executar testes na integração contínua. |

A cobertura mínima de 75% é requisito da avaliação. Não há relatório JaCoCo nem percentual medido neste repositório. A aprovação em testes, isoladamente, não comprova cobertura ou quality gate.

## Fluxo principal e critérios de aceitação

O fluxo inicial implementado no código é:

1. O funcionário abre o formulário de novo membro.
2. Informa nome, CPF, telefone e endereço.
3. O controller aplica as validações declaradas disponíveis e, se não houver erro, chama o serviço.
4. O serviço consulta se já existe um CPF exatamente igual ao recebido e, se não houver duplicidade, solicita o salvamento ao repositório.
5. Após o cadastro, a aplicação redireciona à listagem de membros.

A validação existente não implementa ainda todas as regras de CPF nem a obrigatoriedade completa dos campos. Para o fluxo de cadastro e consulta, os critérios oficiais são [CA01 — Cadastro válido, CA02 — Campos obrigatórios não preenchidos, CA03 — CPF inválido, CA04 — CPF duplicado, CA05 — Consulta de membros cadastrados e CA06 — Consulta sem resultados](docs/requirements/criterios-de-aceite.md). Também se aplicam [CA24 — Identificação e data de cadastro](docs/requirements/criterios-de-aceite.md) e [CA26 — Normalização e validação do CPF](docs/requirements/criterios-de-aceite.md). Os demais critérios permanecem aplicáveis conforme cada funcionalidade for desenvolvida.

## Tecnologias e configuração

Tecnologias encontradas em pom.xml e na configuração atual:

- **Java 21** — versão definida no projeto.
- **Spring Boot 4.1.2** — framework principal.
- **Spring Web MVC** — rotas e controllers web.
- **Thymeleaf** — templates HTML renderizados no servidor.
- **Spring Data JPA** — acesso a dados por JPA.
- **PostgreSQL JDBC** — driver de conexão com o PostgreSQL.
- **Jakarta Bean Validation** — validação declarativa de dados.
- **Maven e Maven Wrapper** — build e gerenciamento de dependências.
- **JUnit 5 e Mockito** — testes; as dependências de testes estão declaradas no pom.xml.
- **GitHub Actions** — workflow de CI em .github/workflows/ci.yml.

Bootstrap, H2 e JaCoCo não estão configurados no pom.xml ou nos recursos consultados. Não há Docker/Compose ou ferramenta de migrations configurada.

## Pré-requisitos e execução local

- JDK 21.
- PostgreSQL local disponível.
- Banco chamado vigor, usuário postgres e permissões de conexão.
- Variável de ambiente DB_PASSWORD definida no processo que inicia a aplicação.

A configuração em src/main/resources/application.properties usa jdbc:postgresql://localhost:5432/vigor, usuário postgres, driver org.postgresql.Driver e a senha fornecida por DB_PASSWORD. Não inclua senhas reais no repositório.

O Hibernate está configurado com spring.jpa.hibernate.ddl-auto=update, que pode atualizar o schema a partir das entidades ao iniciar. A configuração da conexão, por si só, não comprova leitura ou gravação funcional no banco.

Os comandos do Maven Wrapper estão documentados abaixo. A aplicação e os testes não foram executados localmente nesta atualização: o teste de contexto inicializa a configuração JPA e o ddl-auto atual pode alterar o schema do banco local. Assim, o resultado dos comandos e a persistência local ainda precisam ser verificados em ambiente preparado para testes.

### Iniciar a aplicação

Windows PowerShell, na raiz do repositório:

    $env:DB_PASSWORD = "<senha-local>"
    .\mvnw.cmd spring-boot:run

Linux ou macOS:

    export DB_PASSWORD="<senha-local>"
    ./mvnw spring-boot:run

Após confirmar a inicialização, a aplicação Spring Boot usa a porta padrão 8080, salvo configuração diferente.

### Executar os testes

Windows PowerShell:

    $env:DB_PASSWORD = "<senha-local>"
    .\mvnw.cmd clean test

Linux ou macOS:

    export DB_PASSWORD="<senha-local>"
    ./mvnw clean test

Os testes de contexto precisam de PostgreSQL acessível com a configuração correspondente. A CI fornece um PostgreSQL de teste e define DB_PASSWORD para o job.

## Testes automatizados existentes

Os arquivos atuais em src/test/java contêm dois testes unitários do serviço e um teste de inicialização do contexto Spring:

| Tipo | Arquivo e teste | O que verifica |
|---|---|---|
| Teste de contexto Spring | [VigorApplicationTests.java](src/test/java/br/edu/unisul/vigor/VigorApplicationTests.java) — contextLoads | Inicializa o contexto com @SpringBootTest. Usa a configuração JPA e datasource da aplicação; requer PostgreSQL acessível e DB_PASSWORD conforme application.properties. Não testa cadastro, consulta nem persistência após reinício. |
| Teste unitário com Mockito | [MembroServiceTest.java](src/test/java/br/edu/unisul/vigor/service/MembroServiceTest.java) — deveCadastrarMembroQuandoCpfAindaNaoExiste | Simula repositório sem CPF duplicado, verifica que o serviço retorna o membro e chama save. Não comprova validação completa do CPF nem gravação no PostgreSQL. |
| Teste unitário com Mockito | [MembroServiceTest.java](src/test/java/br/edu/unisul/vigor/service/MembroServiceTest.java) — deveRejeitarCadastroQuandoCpfJaExiste | Simula repositório com CPF existente, verifica a exceção e que save não é chamado. Testa a rejeição por duplicidade textual no serviço. |

Esses testes existem; isso não significa que foram executados nesta atualização. Testes para CPF válido/inválido conforme RN03, campos obrigatórios conforme RN01, controller, persistência real e os módulos planejados ainda precisam ser definidos e/ou implementados. O [Plano de testes](docs/testing/plano-de-testes.md) descreve cenários planejados; os resultados reais devem ser registrados em [evidencias.md](docs/testing/evidencias.md).

## Integração contínua, cobertura e quality gate

O workflow [CI - VIGOR](.github/workflows/ci.yml):

- executa em push para main e develop;
- executa em pull requests destinados a main e develop;
- configura Java 21 e um serviço PostgreSQL para testes;
- executa Maven clean test.

O workflow executa testes, mas não configura JaCoCo, não publica relatório de cobertura e não contém configuração de análise estática ou quality gate. Portanto, ele não comprova a cobertura mínima exigida de 75% nem a aprovação em quality gate. O relatório e as evidências continuam pendentes.

## Estrutura do repositório

Estrutura resumida dos arquivos existentes:

    Vigor/
    ├── .github/
    │   └── workflows/
    │       └── ci.yml
    ├── .mvn/
    │   └── wrapper/
    ├── docs/
    │   ├── architecture/
    │   │   ├── decisoes.md
    │   │   └── database/
    │   │       └── modelo-de-dados.md
    │   ├── design/
    │   │   └── prototipo.md
    │   ├── planning/
    │   │   ├── matriz-de-rastreabilidade.md
    │   │   └── plano-de-tasks.md
    │   ├── requirements/
    │   │   ├── criterios-de-aceite.md
    │   │   ├── regras-de-negocio.md
    │   │   └── requisitos.md
    │   └── testing/
    │       ├── evidencias.md
    │       └── plano-de-testes.md
    ├── src/
    │   ├── main/
    │   │   ├── java/br/edu/unisul/vigor/
    │   │   │   ├── controller/
    │   │   │   ├── model/
    │   │   │   ├── repository/
    │   │   │   └── service/
    │   │   └── resources/
    │   │       └── templates/membros/
    │   └── test/java/br/edu/unisul/vigor/
    ├── CONTRIBUTING.md
    ├── HELP.md
    ├── mvnw
    ├── mvnw.cmd
    ├── pom.xml
    └── README.md

Na branch consultada, docs/architecture/database/ contém somente modelo-de-dados.md. Os arquivos ambientes.md e migrations.md não existem nesta estrutura e, por isso, não são listados nem linkados.

## Equipe e responsabilidades

As frentes abaixo são as planejadas no [plano de tasks](docs/planning/plano-de-tasks.md), não uma afirmação de autoria de código ou testes.

| Integrante | Responsabilidade planejada |
|---|---|
| Millena Ferreira Rodrigues | Interface web, protótipo Figma e integração dos fluxos de cadastro e consulta. |
| Maria Eduarda Souza dos Santos Ferreira | Requisitos, regras de negócio e validações do cadastro. |
| Luan Milian Martins | Modelo de domínio, persistência e consultas. |
| Emely Santos da Silveira | Testes de integração, CI, cobertura e evidências de qualidade. |

**Observação:** As responsabilidades apresentadas são planejadas e não comprovam, por si só, a autoria individual de cada entrega.

Os arquivos de teste existentes estão ligados acima, mas não há vínculo verificável no repositório que permita atribuí-los individualmente aos integrantes. Não associamos commits ou PRs a pessoas sem essa evidência. O histórico do Git e os pull requests podem ser usados pela equipe para completar essa rastreabilidade antes de responder à [Issue #1](https://github.com/Miaunisul/Vigor/issues/1).

## Documentação, evidências e licença

- [Requisitos funcionais e não funcionais](docs/requirements/requisitos.md)
- [Regras de negócio](docs/requirements/regras-de-negocio.md)
- [Critérios de aceitação](docs/requirements/criterios-de-aceite.md)
- [Plano de testes](docs/testing/plano-de-testes.md)
- [Registro de evidências](docs/testing/evidencias.md)
- [Matriz de rastreabilidade](docs/planning/matriz-de-rastreabilidade.md)
- [Plano de tasks e responsabilidades](docs/planning/plano-de-tasks.md)
- [Decisões técnicas de arquitetura](docs/architecture/decisoes.md)
- [Modelo de dados](docs/architecture/database/modelo-de-dados.md)
- [Protótipo](docs/design/prototipo.md)
- [Workflow de CI](.github/workflows/ci.yml)

O projeto ainda não possui arquivo LICENSE. A licença depende da confirmação do enunciado oficial da A3 e da decisão da equipe; nenhuma licença é declarada neste README.

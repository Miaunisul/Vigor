# VIGOR — Sistema de Cadastro de Academia

Projeto acadêmico desenvolvido para a A3 da disciplina de Gestão e Qualidade de Software (GQS), da UNISUL.

O VIGOR é uma aplicação web voltada ao cadastro e à consulta de membros de uma academia, com foco na organização das informações, na validação dos dados e na integridade dos registros.

> **Verificações automatizadas em 10/11/2026:** a execução local de `mvn clean verify` executou 37 testes sem falhas, erros ou ignorados; a medição local JaCoCo foi 78% de instruções e 73% de branches. A primeira CI bem-sucedida foi o run #27 do workflow `CI - VIGOR`, na branch `feat/postgres-persistencia`: 37 testes passaram, PostgreSQL 18.6 conectou, Flyway executou V1 e o gate de 75% de instruções foi aprovado. O percentual exato reportado pela CI não foi informado. A execução não comprova, por si só, a cobertura dos cenários funcionais planejados.

## 1. Problema e objetivo

A gestão de uma academia exige que os dados dos membros sejam armazenados de forma organizada e consistente. Informações inválidas ou registros duplicados podem dificultar consultas e comprometer a qualidade dos dados.

O objetivo do VIGOR é desenvolver uma aplicação web que permita cadastrar e consultar membros, aplicando regras de validação e persistindo os dados em um banco de dados relacional.

## 2. Atores do sistema

- **Atendente ou funcionário:** realiza o cadastro e consulta os membros.
- **Responsável pela gestão:** consulta os registros para apoiar a organização da academia.
- **Equipe de desenvolvimento:** implementa, testa, documenta e mantém o sistema.

As permissões específicas de cada perfil devem seguir os requisitos confirmados pela equipe.

## 3. Escopo do MVP

O primeiro incremento funcional prioriza:

1. Cadastro de membros.
2. Consulta e listagem de membros cadastrados.
3. Validação dos campos obrigatórios.
4. Validação do CPF.
5. Prevenção de cadastros duplicados pelo CPF.
6. Mensagens claras de sucesso e erro.
7. Persistência dos dados no banco de dados.

Funcionalidades como gerenciamento de planos de treino, pagamentos, histórico de atividades e autenticação de funcionários permanecem sujeitas à priorização e à confirmação dos requisitos da A3.

**Regra central:** um cadastro inválido ou com CPF duplicado não deve ser persistido.

## 4. Requisitos funcionais

| ID | Requisito |
|---|---|
| RF01 | Permitir o cadastro de membros com os campos obrigatórios preenchidos. |
| RF02 | Rejeitar CPFs inválidos, sem persistir o cadastro. |
| RF03 | Impedir o cadastro de membros com CPF já registrado. |
| RF04 | Permitir a consulta e a listagem dos membros cadastrados. |
| RF05 | Informar o resultado das operações por meio de mensagens compreensíveis. |

Os requisitos detalhados e as regras de negócio estão documentados em [`docs/requirements/`](docs/requirements/).

## 5. Requisitos de qualidade

| ID | Requisito |
|---|---|
| RQ01 | Implementar testes automatizados para as regras de negócio. |
| RQ02 | Atingir a cobertura mínima de 75% definida para o projeto, medida e comprovada por relatório do JaCoCo. |
| RQ03 | Configurar a integração contínua para compilar e executar os testes em push e pull request, conforme o fluxo adotado pela equipe. |
| RQ04 | Separar as responsabilidades de apresentação, lógica de negócio e persistência. |
| RQ05 | Documentar os pré-requisitos e os procedimentos de execução. |
| RQ06 | Validar os dados no servidor, sem depender exclusivamente das validações do navegador. |

Esses requisitos representam objetivos de qualidade. Seu atendimento deve ser comprovado por testes, configuração e evidências da versão avaliada.

## 6. Fluxo principal

1. O atendente acessa o formulário de cadastro.
2. Preenche os dados solicitados.
3. O sistema valida os campos obrigatórios e o CPF.
4. O sistema verifica se o CPF já está registrado.
5. Se os dados forem válidos, o cadastro é persistido e uma mensagem de sucesso é apresentada.
6. O atendente acessa a listagem e verifica o membro cadastrado.

### Critérios de aceitação

- **CA01 — Cadastro válido:** o membro é salvo e aparece na listagem.
- **CA02 — CPF inválido:** o sistema rejeita o cadastro e não persiste o registro.
- **CA03 — CPF duplicado:** o sistema rejeita a operação e preserva o registro original.
- **CA04 — Consulta:** um membro previamente cadastrado aparece na listagem.
- **CA05 — Campo obrigatório:** o sistema impede a persistência quando um campo obrigatório está vazio e apresenta uma validação adequada.

## 7. Tecnologias

As tecnologias identificadas na configuração atual do projeto incluem:

- **Java 21** — linguagem de programação.
- **Spring Boot** — estrutura principal da aplicação, com versão definida no `pom.xml`.
- **Spring MVC** — suporte à camada web.
- **Thymeleaf** — renderização de páginas HTML no servidor.
- **Spring Data JPA** — persistência e acesso aos dados.
- **PostgreSQL** — banco de dados relacional previsto para a aplicação.
- **Maven** — gerenciamento de dependências e build.
- **Spring Boot Test** — suporte aos testes.
- **JUnit e Mockito** — ferramentas de teste disponibilizadas pela configuração de dependências.
- **JaCoCo 0.8.16** — instrumentação e geração dos relatórios HTML e XML de cobertura.

H2 e Bootstrap não estão configurados como tecnologias da execução atual. O workflow do GitHub Actions está definido em [`.github/workflows/ci.yml`](.github/workflows/ci.yml) e utiliza o segredo `CI_DB_PASSWORD`, que precisa ser cadastrado nas configurações de Actions do repositório. A primeira execução bem-sucedida foi o run #27, em 10/11/2026; o relatório JaCoCo foi publicado como artefato `relatorio-jacoco`.

## 8. Pré-requisitos

Para preparar o ambiente de desenvolvimento:

- JDK 21 instalado.
- Git instalado.
- Acesso ao repositório do VIGOR.
- Acesso a um servidor PostgreSQL configurado para a aplicação.
- Terminal compatível com o sistema operacional utilizado.

O projeto contém o Maven Wrapper (`mvnw` e `mvnw.cmd`), que deve ser priorizado para executar os comandos Maven sem exigir uma instalação global do Maven.

## 9. Como executar

Os comandos abaixo pressupõem que a configuração do banco de dados esteja correta. A inicialização completa deve ser validada no ambiente local antes da entrega.

### 9.1 Clonar o repositório

```bash
git clone https://github.com/Miaunisul/Vigor.git
cd Vigor
```

### 9.2 Configurar o banco de dados

Configure o PostgreSQL conforme as propriedades utilizadas pela aplicação.

A configuração atual em `src/main/resources/application.properties` utiliza:

- Banco: `vigor`.
- URL: `jdbc:postgresql://localhost:5432/vigor`.
- Usuário: `postgres`.
- Senha: variável de ambiente `DB_PASSWORD`.

O banco e o servidor PostgreSQL devem estar disponíveis antes da inicialização. A aplicação integra Flyway; a inicialização pode aplicar migrations pendentes. Consulte [Ambientes](docs/architecture/database/ambientes.md) e [Migrations](docs/architecture/database/migrations.md) antes de usar um banco com dados importantes.

Antes de executar o projeto, confira:

- endereço e porta do servidor;
- nome do banco;
- usuário e senha;
- propriedades de conexão configuradas no projeto;
- existência do banco e permissões de acesso.

Não versione credenciais reais, senhas pessoais ou outros segredos.

Não há configuração de Docker Compose documentada. As instruções complementares do banco estão em [Ambientes](docs/architecture/database/ambientes.md) e [Migrations](docs/architecture/database/migrations.md).

### 9.3 Executar a aplicação

Na raiz do projeto, defina `DB_PASSWORD` com a senha local do PostgreSQL e inicie a aplicação.

**Windows PowerShell:**

```powershell
$env:DB_PASSWORD = "<senha-local>"
.\mvnw.cmd spring-boot:run
```

**Linux ou macOS (Bash):**

```bash
export DB_PASSWORD="<senha-local>"
./mvnw spring-boot:run
```

Substitua o marcador localmente; não registre senhas reais no README, no código ou no histórico do Git. Após a inicialização, acesse `http://localhost:8080/membros`. A rota `/` não está mapeada atualmente.

### 9.4 Executar os testes e gerar cobertura

O comando `clean verify` executa os testes e, na fase `verify`, gera os relatórios JaCoCo. O teste de contexto Spring usa a configuração PostgreSQL da aplicação; forneça `DB_PASSWORD` no mesmo terminal e utilize um banco de desenvolvimento cuja situação de migrations seja conhecida.

**Windows PowerShell:**

```powershell
$env:DB_PASSWORD = "<senha-local>"
.\mvnw.cmd clean verify
```

**Linux ou macOS (Bash):**

```bash
export DB_PASSWORD="<senha-local>"
./mvnw clean verify
```

Uma execução aprovada deve terminar sem falhas de build ou testes. Os relatórios são gerados em `target/site/jacoco/index.html` (HTML) e `target/site/jacoco/jacoco.xml` (XML).

### 9.5 Resultado de cobertura registrado

Na medição local registrada em 10/11/2026, JaCoCo 0.8.16 analisou 6 classes e reportou:

- Instruções: **78%**.
- Branches: **73%**.
- Linhas: **98 de 117**.
- Métodos: **24 de 35**.
- Classes: **6 de 6**.

Na medição local, a cobertura de instruções ultrapassou a meta de 75%, enquanto branches ficou abaixo desse valor. O POM configura um quality gate de pelo menos 75% de instruções, sem limite de branches. No GitHub Actions, o run #27 aprovou esse gate; a evidência disponível não informa o percentual exato da CI. Os resultados são detalhados em [Evidências de testes](docs/testing/evidencias.md).

## 10. Estrutura do repositório

Estrutura de referência dos diretórios atualmente utilizados:

```text
Vigor/
├── .mvn/
├── docs/
│   ├── architecture/
│   ├── planning/
│   ├── requirements/
│   └── testing/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
│       └── java/
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
├── HELP.md
└── README.md
```

A estrutura acima resume os diretórios principais. Os pacotes Java, templates, arquivos de configuração e demais componentes devem ser conferidos diretamente no repositório.

## 11. Estratégia de testes

Os testes devem verificar tanto o comportamento esperado quanto a rejeição de entradas inválidas.

| Cenário | Resultado esperado |
|---|---|
| Cadastro válido | O membro é persistido. |
| CPF inválido | O cadastro é rejeitado. |
| CPF duplicado | O registro duplicado é rejeitado. |
| Consulta de membros | Os membros persistidos são retornados. |
| Campo obrigatório vazio | A validação impede o cadastro. |

Os cenários devem ser relacionados aos requisitos e aos critérios de aceitação.

Os resultados precisam registrar o comando executado, a versão testada, o resultado observado e as falhas encontradas. Testes planejados não devem ser apresentados como testes já executados.

## 12. Documentação do projeto

A documentação complementar está organizada nos seguintes diretórios:

- [`docs/requirements/`](docs/requirements/) — requisitos, regras de negócio e critérios de aceitação.
- [`docs/architecture/`](docs/architecture/) — decisões técnicas e documentação da arquitetura.
- [`docs/planning/`](docs/planning/) — planejamento e organização das atividades.
- [`docs/testing/`](docs/testing/) — planejamento de testes e evidências.

Os nomes exatos dos arquivos devem ser conferidos nos respectivos diretórios antes de serem adicionados como links individuais.

## 13. Equipe e responsabilidades

A divisão abaixo representa uma proposta de organização do trabalho. As contribuições efetivamente realizadas devem ser comprovadas pelo histórico do Git, pelos arquivos e pelos testes correspondentes.

| Integrante | Frente de trabalho | Evidências esperadas |
|---|---|---|
| Millena Ferreira Rodrigues | Aplicação web, persistência, integração e estrutura técnica, conforme as contribuições efetivamente realizadas. | Commits, pull requests, arquivos e testes próprios. |
| Emely Santos da Silveira | Serviços, regras de negócio, testes automatizados e qualidade da integração. | Commits, testes, relatórios e execuções da CI, quando configurada. |
| Maria Eduarda Souza dos Santos Ferreira | Revisão dos requisitos e regras existentes, testes de aceitação e validação funcional. | Revisões documentadas, cenários executados e evidências. |
| Luan Milian Martins | Diagramas, rastreabilidade, documentação técnica e reprodução do ambiente. | Diagramas, matriz, documentação e evidências de reprodução. |

A responsabilidade planejada não comprova autoria. Cada integrante deve registrar suas contribuições reais, e as atribuições históricas devem ser verificadas antes da entrega.

## 14. Próximas etapas

Antes da entrega final, a equipe deve:

1. Confirmar o escopo definitivo do MVP com base nos requisitos da A3.
2. Executar a aplicação em um ambiente local reproduzível.
3. Validar o fluxo de cadastro e consulta.
4. Executar e revisar os testes automatizados.
5. Acompanhar as próximas execuções da integração contínua e registrar suas evidências.
6. Revisar o relatório JaCoCo gerado, incluindo os indicadores que ficaram abaixo de 75%.
7. Revisar requisitos, regras de negócio, diagramas e rastreabilidade.
8. Consolidar as evidências da versão candidata à entrega.

## 15. Licença

A licença do projeto deve seguir as orientações do enunciado oficial da A3. Até essa confirmação, não se deve declarar uma licença que ainda não tenha sido definida pela equipe.

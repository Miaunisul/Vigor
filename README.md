# VIGOR — Sistema de Cadastro de Academia

Projeto acadêmico da A3 de Gestão e Qualidade de Software (GQS).

> **Estado inicial do pacote:** base de implementação e documentação preparada para revisão da equipe. A aplicação precisa ser executada localmente e os testes/CI precisam passar no GitHub antes de declarar a entrega funcional. Não declarar a cobertura de 75% como atingida sem consultar o relatório real.

## 1. Problema e objetivo

A academia precisa manter os dados de seus membros organizados, reduzir erros de preenchimento e permitir que funcionários consultem os cadastros com segurança e consistência. O VIGOR propõe uma aplicação web simples para cadastrar e consultar membros.

## 2. Atores

- **Atendente/funcionário:** registra novos membros e consulta cadastros.
- **Responsável pela gestão:** consulta os registros para apoiar a organização da academia.
- **Equipe de desenvolvimento:** mantém a aplicação, os testes e a documentação.

## 3. Recorte do MVP

A primeira versão funcional contempla:
1. cadastro de membros;
2. consulta/listagem de membros;
3. validação de CPF;
4. rejeição de CPF duplicado;
5. feedback claro de sucesso ou erro.

**Regra de negócio escolhida:** o CPF deve ser válido pelos dígitos verificadores e não pode estar associado a outro membro. Um cadastro rejeitado não pode ser persistido.

Planos de treino, pagamentos e histórico de atividades ficam fora do primeiro incremento funcional até que o professor confirme se precisam ser implementados ou apenas modelados/documentados.

## 4. Requisitos funcionais

| ID | Requisito |
|---|---|
| RF01 | O sistema deve permitir cadastrar membro com os campos obrigatórios preenchidos. |
| RF02 | O sistema deve rejeitar CPF inválido e não persistir o membro. |
| RF03 | O sistema deve impedir dois membros com o mesmo CPF. |
| RF04 | O sistema deve listar membros já cadastrados. |
| RF05 | O sistema deve informar o resultado da operação com mensagem compreensível. |

## 5. Requisitos de qualidade

| ID | Requisito |
|---|---|
| RQ01 | As regras de negócio devem ter testes automatizados. |
| RQ02 | A entrega final deve atingir cobertura mínima de 75%, medida por relatório real do JaCoCo. |
| RQ03 | A CI deve compilar e executar os testes em push e pull request. |
| RQ04 | O código deve separar apresentação, regra de negócio e persistência. |
| RQ05 | Os comandos e pré-requisitos de execução devem estar documentados. |
| RQ06 | O sistema deve validar os dados no servidor, não apenas no navegador. |

## 6. Fluxo principal e critérios de aceitação

1. O atendente abre o formulário de cadastro.
2. Informa nome, CPF, e-mail e telefone.
3. O sistema valida os campos e os dígitos verificadores do CPF.
4. O sistema verifica se o CPF já está cadastrado.
5. Se os dados forem válidos e o CPF for único, o membro é salvo e uma mensagem de sucesso é exibida.
6. O atendente consulta a lista e encontra o membro cadastrado.

### Critérios de aceitação

- **CA01 — Cadastro válido:** dado nome e CPF válidos e CPF ainda não utilizado, ao enviar o formulário, o membro é salvo e aparece na consulta.
- **CA02 — CPF inválido:** dado um CPF com dígitos verificadores inválidos, ao enviar o formulário, o sistema rejeita o cadastro e não salva o membro.
- **CA03 — CPF duplicado:** dado um CPF já cadastrado, ao tentar cadastrar outro membro com esse CPF, o sistema rejeita a operação e preserva o registro original.
- **CA04 — Consulta:** dado um membro salvo, ao abrir a listagem, o membro aparece nos resultados.
- **CA05 — Campo obrigatório:** dado um campo obrigatório vazio, o sistema não persiste o cadastro e apresenta validação.

## 7. Tecnologias

- Java 21
- Spring Boot 3
- Spring MVC + Thymeleaf + Bootstrap
- Spring Data JPA
- PostgreSQL para desenvolvimento local
- H2 para testes de integração isolados
- Maven
- JUnit 5, Mockito e Spring Boot Test
- JaCoCo
- GitHub Actions

## 8. Pré-requisitos

- JDK 21
- Maven 3.9+ (ou adicionar o Maven Wrapper ao repositório)
- Docker Desktop, se for usar o PostgreSQL pelo `compose.yaml`

## 9. Como executar

### 9.1 Subir o banco PostgreSQL

Na raiz do projeto:

```bash
docker compose up -d db
```

Configuração local padrão do compose:
- banco: `vigor`
- usuário: `vigor`
- senha: `vigor_local_only`
- porta: `5432`

Essas credenciais são apenas para desenvolvimento local. Não reutilizar em ambiente real.

### 9.2 Executar a aplicação

```bash
mvn spring-boot:run
```

Acesse `http://localhost:8080`.

Se preferir configurar o banco manualmente, defina `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` no ambiente antes de executar. Nunca coloque credenciais reais no Git.

### 9.3 Executar testes

```bash
mvn test
```

### 9.4 Gerar relatório de cobertura

```bash
mvn clean verify
```

Relatório JaCoCo esperado: `target/site/jacoco/index.html`.

**Atenção:** o caminho só existirá depois de a execução terminar com sucesso. Registre o percentual real observado, não um valor estimado.

## 10. Estrutura

```text
.
├── .github/workflows/ci.yml
├── docs/
│   ├── planejamento/
│   ├── requisitos/
│   └── testes/
├── src/
│   ├── main/java/br/edu/unisul/vigor/
│   ├── main/resources/templates/
│   └── test/java/br/edu/unisul/vigor/
├── compose.yaml
├── pom.xml
└── README.md
```

## 11. Testes iniciais previstos

- Cadastro válido: deve salvar.
- CPF inválido: deve rejeitar e não salvar.
- CPF duplicado: deve rejeitar e preservar o cadastro existente.
- Consulta: deve devolver os membros cadastrados.
- Campos obrigatórios: devem ser validados.

## 12. Responsabilidades e rastreabilidade

| Integrante | Responsabilidade planejada | Evidência a registrar |
|---|---|---|
| Millena Ferreira Rodrigues | Aplicação web, integração com PostgreSQL, persistência e estrutura técnica | Links dos próprios commits/PRs e testes relacionados |
| Emely Santos da Silveira | Backend em conjunto com Millena, testes automatizados, JaCoCo e CI | Links dos próprios commits/PRs, testes e execução da CI |
| Maria Eduarda Souza dos Santos Ferreira | Requisitos, regras de negócio, critérios de aceitação e validação funcional | Requisitos revisados, cenários de aceite e evidências de validação |
| Luan Milian Martins | Diagramas, documentação técnica, matriz de rastreabilidade e instruções de reprodução | Links dos documentos/commits e evidências organizadas |

A tabela descreve responsabilidades planejadas. Atualizar os links conforme existirem commits, PRs e testes reais. Cada integrante deve registrar suas próprias contribuições de código e testes.

## 13. Próximas etapas

Veja:
- `docs/planejamento/PLANO_DE_ACAO.md`
- `docs/planejamento/TASKS.md`
- `docs/planejamento/RESPOSTA_ISSUE_1.md`

## 14. Licença

A licença exigida deve ser confirmada no enunciado oficial da A3. Até essa confirmação, não assumir uma licença. O arquivo `LICENSE_PENDENTE.md` é apenas um lembrete e deve ser substituído pelo arquivo de licença correto antes da entrega.

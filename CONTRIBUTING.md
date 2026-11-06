# Guia de Contribuição — VIGOR

## 1. Sobre o projeto

O VIGOR é um sistema de cadastro e gestão de membros de uma academia, desenvolvido como projeto acadêmico da A3 da UNISUL.

## 2. Tecnologias utilizadas

- Java 21
- Spring Boot
- Maven
- Spring Data JPA
- Thymeleaf
- PostgreSQL
- JUnit e Mockito para testes

## 3. Preparação do ambiente

Antes de executar o projeto, instale:

- JDK 21
- PostgreSQL
- Git
- Uma IDE compatível com Java, como IntelliJ IDEA ou VS Code.

Clone o repositório e entre na pasta do projeto.

Configure o banco PostgreSQL e a variável de ambiente `DB_PASSWORD` com a senha do usuário do banco. Não compartilhe nem versione senhas.

## 4. Executar o projeto

No Windows, configure a variável de ambiente no terminal atual:

```cmd
set DB_PASSWORD=sua_senha
```

Substitua `sua_senha` pela senha local do PostgreSQL.

Execute os testes:

```cmd
mvnw.cmd clean test
```

Inicie a aplicação:

```cmd
mvnw.cmd spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`.

## 5. Organização das branches

- `main`: versão estável do projeto.
- `develop`: integração do trabalho da equipe.
- Branches de funcionalidade: usadas para desenvolver tarefas específicas.

Crie branches a partir de `develop`, preferencialmente usando nomes descritivos, como `feat/cadastro-membros` ou `fix/validacao-cpf`.

## 6. Commits

Faça commits pequenos e com mensagens claras.

Exemplos:

- `feat: adiciona cadastro de membros`
- `fix: corrige validacao de CPF`
- `test: adiciona testes do servico`
- `docs: atualiza instrucoes do projeto`

Evite incluir alterações não relacionadas no mesmo commit.

## 7. Pull requests

Ao concluir uma tarefa:

1. Envie sua branch para o GitHub.
2. Abra um pull request para `develop`.
3. Descreva o que foi alterado.
4. Informe os testes executados.
5. Resolva os comentários de revisão antes da integração.

A `main` deve receber alterações integradas e revisadas conforme o fluxo definido pela equipe.

## 8. Testes e qualidade

Execute os testes antes de abrir um pull request. Novas regras de negócio devem ter testes compatíveis com seu comportamento esperado.

Não envie código com erros conhecidos de compilação ou testes.

## 9. Segurança

- Não versione senhas, tokens ou credenciais.
- Não envie arquivos `.env` com valores reais.
- Não inclua dados pessoais reais de membros em testes ou evidências.
- Utilize dados fictícios durante o desenvolvimento.

## 10. Trabalho em equipe

Respeite as responsabilidades definidas no planejamento da A3. Antes de alterar arquivos compartilhados, converse com os integrantes envolvidos para evitar conflitos.

Atualize a documentação relacionada sempre que uma mudança alterar requisitos, regras de negócio, arquitetura ou procedimentos de execução.

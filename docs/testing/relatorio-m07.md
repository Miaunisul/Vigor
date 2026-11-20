# Relatório de testes — M07: gerenciamento de treinos

**Data:** 20/11/2026

## Rotas implementadas

- `GET /treinos`: mostra a seleção de membro e, quando há um membro selecionado, somente os treinos associados a ele.
- `GET /treinos/novo`: apresenta o formulário e carrega membros cadastrados para seleção.
- `POST /treinos`: valida os campos, chama `TreinoService.cadastrar` e redireciona para a consulta do membro após sucesso.

Os templates de listagem e formulário usam a folha de estilos compartilhada, adaptam-se a telas menores e mostram erros junto aos campos. A consulta usa `TreinoService.consultarPorMembro`; o controller não consulta treinos de outros membros.

## Regras atendidas

- O cadastro exige membro existente, tipo, descrição, duração inteira positiva e data de início.
- Campos inválidos retornam ao formulário com os valores informados preservados e mensagens em português.
- A entidade, o repository, o service e a migration V3 existentes foram reutilizados. Nenhuma migration ou regra de outro módulo foi alterada.

## Testes específicos

O comando direcionado executado foi:

```text
mvn -Dtest=TreinoControllerTest,TreinoTemplateRenderingTest,TreinoHttpPostgresIntegrationTest,TreinoServiceTest,TreinoPostgresIntegrationTest test
```

Resultado: 44 testes contabilizados, 0 falhas, 0 erros e 11 ignorados. `TreinoControllerTest` (16), `TreinoTemplateRenderingTest` (4) e `TreinoServiceTest` (13) passaram. Os oito testes de `TreinoPostgresIntegrationTest` e os três testes de `TreinoHttpPostgresIntegrationTest` foram ignorados porque `DB_PASSWORD` não estava disponível no processo Maven.

O teste HTTP/PostgreSQL está preparado para cadastrar um treino pela rota, consultar os campos persistidos por JDBC, carregar membros no formulário e garantir isolamento entre as listagens de dois membros. Esse fluxo ainda precisa ser executado em um ambiente com PostgreSQL e `DB_PASSWORD` configurado.

## Suíte completa e cobertura

`mvn clean verify` terminou com `BUILD SUCCESS`: 178 testes contabilizados, 0 falhas, 0 erros e 50 ignorados. Dos ignorados, 49 testes são condicionados ao PostgreSQL e um é o teste de contexto da aplicação, também condicionado à configuração do banco.

JaCoCo cobriu 1.497 de 1.846 instruções (**81,09%**). O quality gate de 75% foi aprovado. Como os testes PostgreSQL foram ignorados neste ambiente, este resultado não certifica a execução da persistência real do fluxo HTTP M07.

## Execução manual local

Configure `DB_PASSWORD` no terminal e inicie a aplicação:

**Windows PowerShell:**

```powershell
$env:DB_PASSWORD = "<senha-local>"
.\mvnw.cmd spring-boot:run
```

Em seguida, acesse `http://localhost:8080/treinos` para consultar treinos ou `http://localhost:8080/treinos/novo` para cadastrar. A tela de membros também apresenta o acesso a Treinos.

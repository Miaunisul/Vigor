# Relatório de testes — M06: edição de membros

**Data:** 20/11/2026

## Escopo verificado

- `GET /membros/{id}/editar` carrega os dados atuais; IDs inexistentes retornam à listagem com mensagem.
- `POST /membros/{id}/editar` atualiza nome, CPF, telefone e endereço e redireciona à listagem após sucesso.
- CPF próprio é permitido. CPF inválido e CPF pertencente a outro membro são rejeitados; o CPF é normalizado antes da comparação.
- O serviço valida um candidato antes de modificar a entidade persistida. ID e data de cadastro não são vinculáveis pelo formulário e são preservados pelo serviço.
- A listagem apresenta o endereço e oferece a ação de edição.

## Execuções

| Comando | Resultado |
|---|---|
| `mvn test` | BUILD SUCCESS — 152 testes contabilizados, 0 falhas, 0 erros e 47 ignorados. |
| `mvn clean verify` | BUILD SUCCESS — 152 testes contabilizados, 0 falhas, 0 erros e 47 ignorados; JaCoCo aprovou o gate. |
| Relatório JaCoCo | 80,32% das instruções cobertas (1.163 de 1.448); mínimo configurado: 75%. |

Os testes unitários `MembroControllerTest` (13) e `MembroServiceTest` (28) passaram. Os testes de integração PostgreSQL, inclusive `MembroAtualizacaoPostgresIntegrationTest` e `MembroCpfHttpPostgresIntegrationTest`, foram ignorados porque `DB_PASSWORD` não está configurada neste ambiente. A conexão local recusou autenticação para o usuário `postgres` quando os testes HTTP foram tentados sem a credencial. Portanto, a persistência real do fluxo de edição está coberta por testes preparados para PostgreSQL, mas não foi executada nesta máquina.

Os testes de integração mantêm o Flyway ativo, usam CPFs e nomes exclusivos e consultam os registros persistidos. Para executá-los, configure `DB_PASSWORD` para o PostgreSQL local e rode os dois comandos da tabela.

## Banco de dados

Nenhuma migration existente foi alterada e nenhuma migration nova foi criada. A constraint `uk_membros_cpf` foi mantida.

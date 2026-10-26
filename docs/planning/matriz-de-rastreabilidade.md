# VIGOR — Matriz de Rastreabilidade

## 1. Objetivo

Relacionar os requisitos funcionais do VIGOR às regras de negócio, aos critérios de aceite e aos testes previstos, facilitando a verificação de que os requisitos foram implementados e testados.

## 2. Matriz

| Requisito | Regra relacionada | Critério de aceite | Teste previsto | Situação |
|---|---|---|---|---|
| RF01 — Cadastrar aluno | RN01, RN04 | CA01, CA02 | Cadastro válido e campo obrigatório ausente | A implementar |
| RF02 — Validar dados | RN01, RN02 | CA02, CA03 | Campos obrigatórios e CPF inválido | A implementar |
| RF03 — Impedir duplicidade | RN03 | CA04 | Tentativa de cadastrar CPF repetido | A implementar |
| RF04 — Consultar alunos | RN05, RN06 | CA05 | Consulta de aluno existente | A implementar |
| RF05 — Persistir registros | RN04, RN06 | CA01, CA06 | Cadastro e consulta posterior | A implementar |

## 3. Como atualizar a matriz

- **A implementar:** funcionalidade ou teste ainda não implementado.
- **Implementado:** código desenvolvido, mas ainda sem validação completa.
- **Aprovado:** critério executado com resultado esperado e evidência registrada.
- **Reprovado:** critério executado e o resultado não correspondeu ao esperado.
- **Bloqueado:** não foi possível executar devido a uma dependência ou impedimento registrado.

## 4. Evidências esperadas

Para cada linha, registrar quando disponíveis:

- Identificação do teste automatizado ou caso de teste manual.
- Resultado obtido.
- Referência ao commit ou à issue relacionada.
- Link ou caminho da evidência de execução.

## 5. Observações

A matriz deve ser atualizada conforme os requisitos, as regras e os testes evoluírem. Nenhum requisito deve ser marcado como aprovado sem verificação efetiva.

## 6. Situação do documento

Matriz inicial de planejamento. Os testes e os resultados serão preenchidos conforme a implementação e a validação do VIGOR.
# Matriz de Rastreabilidade — VIGOR

## 1. Objetivo

Relacionar os requisitos funcionais do VIGOR às regras de negócio, aos critérios de aceite e aos testes previstos, facilitando o acompanhamento do desenvolvimento e da validação do sistema.

## 2. Matriz de rastreabilidade

| Requisito | Regra de negócio | Critério de aceite | Teste previsto | Prioridade |
|---|---|---|---|---|
| RF02 — Cadastrar membros | RN01, RN02, RN03, RN04, RN21 | CA01, CA02, CA03, CA04, CA24, CA26 | CT01, CT02, CT03, CT04 | Alta |
| RF03 — Consultar membros | Não se aplica regra específica | CA05, CA06 | CT05, CT06 | Alta |
| RF02 — Cadastrar membros; RF04 — Atualizar membros (validação do CPF) | RN03, RN05, RN21 | CA03, CA17, CA26 | CT03 | Alta |
| RF02 — Cadastrar membros; RF04 — Atualizar membros (unicidade do CPF) | RN04, RN05, RN21 | CA04, CA17, CA18, CA26 | CT04 | Alta |
| RF04 — Atualizar membros | RN01, RN02, RN03, RN04, RN05, RN21 | CA07, CA17, CA18, CA24, CA26 | CT07 | Média |
| RF05 — Excluir membros | RN06, RN21 | CA08, CA19 | CT08 | Média |
| RF06 — Cadastrar treinos; RF07 — Consultar treinos | RN07, RN08, RN09, RN21 | CA09, CA10, CA20, CA21 | CT09, CT10 | A definir |
| RF08 — Registrar pagamentos | RN10, RN11, RN12, RN13, RN21 | CA11, CA12, CA27 | CT11, CT12 | A definir |
| RF09 — Consultar histórico de atividades | RN14, RN15, RN16, RN17 | CA14, CA22 | CT13, CT14 | A definir |
| RF01 — Autenticar funcionários | RN18, RN19, RN20 | CA15, CA16, CA23 | CT15, CT16 | A definir |

## 3. Requisitos não funcionais

| Requisito | Forma de verificação | Situação |
|---|---|---|
| RNF01 — Segurança | Verificação das proteções de dados e credenciais implementadas. | A verificar |
| RNF02 — Desempenho | Avaliação do tempo de resposta das operações relevantes. | A verificar |
| RNF03 — Usabilidade | Testes de usabilidade com usuários ou participantes definidos pela equipe. | A verificar |
| RNF04 — Integridade e persistência | Testes das validações, restrições e relacionamentos entre registros. | A verificar |
| RNF05 — Manutenibilidade | Revisão da organização do código e da separação de responsabilidades. | A verificar |
| RNF06 — Testabilidade | Execução dos testes automatizados e verificação dos resultados. | A verificar |

## 4. Prioridade de implementação

A primeira etapa do MVP deverá priorizar:

1. Cadastro de membros.
2. Validação dos campos obrigatórios.
3. Validação e unicidade do CPF.
4. Consulta de membros.
5. Persistência dos registros.
6. Testes automatizados dessas funcionalidades.

As demais funcionalidades deverão ser planejadas conforme a divisão de responsabilidades da equipe e a evolução do projeto.

## 5. Situação dos testes

Os códigos CT01 a CT16 identificam testes previstos, não resultados já obtidos.

A equipe deverá criar ou ajustar os casos de teste correspondentes durante a implementação. Os resultados e as evidências deverão ser registrados em `docs/testing/evidencias.md`.

A matriz deverá ser atualizada sempre que um requisito, uma regra, um critério de aceite ou um teste for alterado.

## 6. Pendências

- Confirmar as prioridades das funcionalidades além do MVP.
- Criar os casos de teste correspondentes aos identificadores CT01 a CT16.
- Relacionar os testes aos arquivos de implementação e de teste efetivamente criados.
- Registrar os resultados das execuções.
- Conferir a rastreabilidade dos requisitos não funcionais.
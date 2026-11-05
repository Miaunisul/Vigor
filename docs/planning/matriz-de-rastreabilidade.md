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

As regras RN14 a RN16 se aplicam à preparação dos dados do histórico, enquanto a RN17 orienta sua consulta. As regras RN18 e RN19 também se aplicam à preparação dos funcionários utilizados na autenticação.

Os códigos da coluna “Teste previsto” foram preservados da matriz anterior. Como os casos CT01 a CT16 ainda não possuem definições individuais disponíveis para esta revisão, suas associações são provisórias. A presença de um código na linha não comprova que ele cubra todos os critérios relacionados.

Pontos que deverão ser conferidos pelo responsável pelos testes:

- CT01 a CT04: definir os casos e conferir a cobertura de cadastro, campos obrigatórios, validade e unicidade do CPF, incluindo dígitos verificadores, sequências repetidas e normalização.
- CT05 e CT06: definir os casos de consulta de membros e conferir os cenários com e sem registros.
- CT07: definir os casos de atualização e conferir os cenários de alteração válida, rejeição de dados inválidos e manutenção do CPF do próprio membro.
- CT08: definir os casos de exclusão e conferir os cenários sem vínculos e com bloqueio por registros vinculados.
- CT09 e CT10: esclarecer a distribuição dos casos. A matriz anterior os associava aos CA09 e CA10, referentes a cadastro de treino e rejeição de membro inexistente. O comentário do PR também solicita consulta de treinos; a cobertura do CA20 precisa ser definida, sem presumir que já exista.
- CT11 e CT12: definir os casos de registro de pagamentos e validação de dados. A consulta de pagamentos mencionada no comentário do PR não consta dos RF atuais e não foi incorporada ao escopo nesta revisão.
- CT13 e CT14: revisar suas associações com o histórico. O CT13 foi mantido na tabela por preservação dos identificadores, mas seu vínculo anterior com o CA13 precisa ser revisto, pois o registro de atividades pela interface está fora desta entrega. A consulta permanece obrigatória pelos CA14 e CA22.
- CT15 e CT16: definir os casos de autenticação e conferir os cenários de credenciais válidas e inválidas, identificando também a verificação de acesso sem autenticação prevista no CA23.
- CA17 a CA27: confirmar os casos correspondentes, sem presumir que os CT existentes cubram todos esses critérios.
- CA25: identificar uma verificação que inclua a gravação dos dados, o reinício da aplicação e a consulta posterior, conforme RNF04.

## 3. Requisitos não funcionais

| Requisito | Forma de verificação | Situação |
|---|---|---|
| RNF01 — Segurança | Verificar acesso com credenciais válidas e inválidas, bloqueio de acesso sem autenticação e armazenamento de senhas por hash apropriado, sem expor credenciais nas evidências. Referências: CA15, CA16 e CA23. | A verificar |
| RNF02 — Desempenho | Executar os fluxos previstos e registrar ambiente, volume de dados, tempos de consulta e ocorrência de travamentos ou erros de tempo limite. Não presumir uma meta numérica de tempo de resposta não definida. | A verificar |
| RNF03 — Usabilidade | Realizar testes de usabilidade com os participantes definidos pela equipe, avaliando identificação dos campos, indicação de obrigatoriedade, mensagens e navegação, com registro das dificuldades encontradas. | A verificar |
| RNF04 — Integridade e persistência | Verificar rejeição de dados inválidos, integridade dos vínculos e preservação dos dados anteriores em operações rejeitadas. Para CA25, salvar dados, reiniciar a aplicação e confirmar sua permanência por consulta. | A verificar |
| RNF05 — Manutenibilidade | Revisar a organização do código e a separação de responsabilidades, além de registrar os resultados da análise estática e do quality gate. | A verificar |
| RNF06 — Testabilidade | Verificar a execução dos testes com Maven e na integração contínua, além da geração do relatório JaCoCo com cobertura mínima de 75%. | A verificar |

## 4. Prioridade de implementação

A primeira etapa do MVP deverá priorizar:

1. Cadastro de membros.
2. Validação dos campos obrigatórios.
3. Validação e unicidade do CPF.
4. Consulta de membros.
5. Atualização de membros.
6. Persistência dos registros.
7. Testes automatizados dessas funcionalidades.

Os demais requisitos funcionais serão implementados nas etapas seguintes, conforme a divisão de responsabilidades da equipe.

Todos os RF01 a RF09 integram a entrega final da A3. A divisão em etapas indica somente a ordem de desenvolvimento e não retira autenticação, exclusão de membros, treinos, pagamentos ou consulta do histórico do escopo final.

As funcionalidades fora do escopo final estão identificadas na seção 7 de `docs/requirements/requisitos.md`. O CA13 se refere ao registro de atividades pela interface, não à consulta obrigatória do histórico.

## 5. Situação dos testes

Os códigos CT01 a CT16 identificam testes previstos, não resultados já obtidos. Suas definições individuais e seus vínculos definitivos serão estabelecidos pela responsável pelos testes.

O acompanhamento deverá distinguir:

- Planejado: cenário previsto, ainda sem implementação confirmada.
- Implementado: teste existente, com referência ao arquivo e método correspondente.
- Executado: teste realizado, com resultado e evidência registrados.

Um teste executado pode ser aprovado ou reprovado. A aprovação somente deve ser registrada quando o resultado observado atender ao comportamento esperado.

Nesta revisão documental, não foi comprovada a implementação ou execução dos CT. As associações preservadas na tabela não devem ser interpretadas como confirmação de cobertura dos critérios.

A equipe deverá criar ou ajustar os casos de teste correspondentes durante a implementação. Os resultados e as evidências deverão ser registrados em `docs/testing/evidencias.md`.

A matriz deverá ser atualizada sempre que um requisito, uma regra, um critério de aceite ou um teste for alterado.

## 6. Pendências

- Confirmar as prioridades das funcionalidades das etapas seguintes da entrega final da A3.
- Definir os casos de teste correspondentes aos identificadores CT01 a CT16 e revisar suas associações.
- Esclarecer as diferenças entre as associações anteriores e as orientações do comentário de revisão do PR.
- Revisar o tratamento do CT13 considerando que o CA13 está fora da entrega.
- O responsável pelos testes deverá definir os casos CT que verificarão os CA17 a CA27, cujo conteúdo e vínculos com os requisitos e regras de negócio foram revisados na D01.
- Identificar a verificação de persistência após reiniciar a aplicação, conforme CA25.
- Relacionar os testes aos arquivos de implementação e de teste efetivamente criados.
- Registrar os resultados das execuções.
- Completar a rastreabilidade dos requisitos não funcionais com as verificações efetivamente realizadas e suas evidências.
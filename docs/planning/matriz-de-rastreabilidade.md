# Matriz de Rastreabilidade — VIGOR

## 1. Objetivo

Relacionar os requisitos funcionais e não funcionais do VIGOR às regras de negócio, aos critérios de aceite e às verificações previstas, facilitando o acompanhamento do desenvolvimento e da validação do sistema.

Esta matriz registra relações de rastreabilidade. Não substitui o plano de testes nem define os casos de teste, cuja elaboração cabe à pessoa responsável por essa atividade.

## 2. Requisitos funcionais

| Requisito | Regra de negócio | Critério de aceite | Vínculo proposto com testes | Prioridade |
|---|---|---|---|---|
| RF02 — Cadastrar membros | RN01, RN02, RN03, RN04, RN21 | CA01, CA02, CA03, CA04, CA24, CA26 | CT01, CT02, CT03, CT04 | Alta |
| RF03 — Consultar membros | Não se aplica regra específica | CA05, CA06 | CT05, CT06 | Alta |
| RF04 — Atualizar membros | RN01, RN02, RN03, RN04, RN05, RN21 | CA07, CA17, CA18, CA24, CA26 | CT07; possíveis vínculos complementares com CT03 e CT04, a confirmar após sua definição | Média |
| RF05 — Excluir membros | RN06, RN21 | CA08, CA19 | CT08 | Média |
| RF06 — Cadastrar treinos | RN07, RN08, RN09, RN21 | CA09, CA10, CA21 | CT09 | A definir |
| RF07 — Consultar treinos | RN07 como referência para o vínculo dos dados consultados | CA20 | CT10 | A definir |
| RF08 — Registrar pagamentos | RN10, RN11, RN12, RN13, RN21 | CA11, CA12, CA27 | CT11, CT12 | A definir |
| RF09 — Consultar histórico de atividades | RN17; RN14, RN15 e RN16 na preparação dos dados | CA14, CA22 | CT13, CT14 | A definir |
| RF01 — Autenticar funcionários | RN20; RN18 e RN19 na preparação dos funcionários | CA15, CA16, CA23 | CT15, CT16 | A definir |

Todos os vínculos com testes apresentados nesta matriz estão planejados e são provisórios, pois os CT ainda não possuem definições individuais no plano de testes disponível.

### 2.1. Organização dos vínculos com os testes

As associações acima incorporam as seguintes orientações de rastreabilidade:

- Separação entre cadastro de treinos, associado provisoriamente ao CT09, e consulta de treinos, associada provisoriamente ao CT10 e ao CA20.
- Associação de CT13 e CT14 aos critérios de consulta do histórico, CA14 e CA22. A distribuição definitiva entre esses casos deverá ser confirmada pela pessoa responsável pelos testes.
- Ausência de vínculo entre CT13 ou CT14 e CA13, pois o CA13 delimita que o registro de atividades pela interface está fora desta entrega.
- Manutenção de CT11 e CT12 vinculados ao registro de pagamentos e às suas validações. A consulta financeira de pagamentos não foi acrescentada aos requisitos atuais.
- Registro da sugestão de CT17 para o CA25, sujeito à confirmação e definição pela pessoa responsável pelos testes.

As regras RN14 a RN16 se aplicam aos dados previamente carregados no histórico, enquanto a RN17 orienta sua consulta. As regras RN18 e RN19 também se aplicam à preparação dos funcionários utilizados na autenticação.

A presença de um CT na tabela não comprova que ele cubra todos os critérios relacionados. Essa cobertura deverá ser conferida após a definição dos casos.

### 2.2. Vínculos dos CA17 a CA27

| Critério de aceite | Requisito relacionado | Regras relacionadas | Vínculo proposto com testes |
|---|---|---|---|
| CA17 — Rejeição de atualização inválida | RF04 | RN01, RN03, RN04, RN05, RN21 | CT07; vínculos complementares com CT03 e CT04 dependem de sua definição. |
| CA18 — Manutenção do CPF do próprio membro | RF04 | RN04, RN05 | CT07; possível vínculo com CT04, a confirmar. |
| CA19 — Bloqueio de exclusão com registros vinculados | RF05 | RN06, RN21 | CT08. |
| CA20 — Consulta de treinos por membro e ausência de registros | RF07 | RN07 como referência para o vínculo dos dados consultados | CT10, com cobertura a confirmar após sua definição. |
| CA21 — Rejeição de treino inválido | RF06 | RN07, RN08, RN09, RN21 | CT09, sujeito à confirmação de cobertura após sua definição. |
| CA22 — Histórico sem atividades | RF09 | RN17 | CT13 e CT14 associados ao histórico; distribuição definitiva pendente. |
| CA23 — Restrição de acesso sem autenticação | RF01, RNF01 | Restrição definida diretamente nos requisitos | CT15 e CT16 associados à autenticação; cobertura específica pendente de definição. |
| CA24 — Geração e preservação do identificador e da data de cadastro | RF02, RF04 | RN02 | CT01 e CT07, sujeitos à confirmação após sua definição. |
| CA25 — Persistência após reiniciar a aplicação | RNF04 | Persistência definida diretamente no requisito não funcional | CT17 proposto; inclusão e definição pendentes. |
| CA26 — Validação e normalização do CPF | RF02, RF04 | RN03, RN04, RN05, RN21 | CT03, CT04 e CT07, com distribuição e cobertura a confirmar. |
| CA27 — Rejeição de pagamento para membro inexistente | RF08 | RN10, RN21 | CT12, sujeito à confirmação após sua definição. |

Os vínculos entre critérios, requisitos e regras documentam o comportamento esperado. Os vínculos com CT permanecem provisórios até o detalhamento dos casos pela pessoa responsável pelos testes.

Para atender ao CA25, a verificação deverá contemplar a permanência dos dados após reiniciar efetivamente a aplicação. Uma consulta realizada somente após salvar, sem reinício, não comprova esse critério. O procedimento de teste deverá ser definido na atividade de testes.

## 3. Requisitos não funcionais

| Requisito | Forma de verificação prevista | Situação |
|---|---|---|
| RNF01 — Segurança | Verificação do acesso com credenciais válidas e inválidas, do bloqueio de acesso sem autenticação e do armazenamento de senhas por hash apropriado, sem persistência em texto puro. Referências: CA15, CA16 e CA23. As evidências não devem expor credenciais. | Planejado |
| RNF02 — Desempenho | Avaliação dos fluxos previstos, com registro do ambiente, volume de dados, tempos de consulta e ocorrência de travamentos ou erros de tempo limite. Não presumir uma meta numérica de tempo de resposta não definida. | Planejado |
| RNF03 — Usabilidade | Avaliação com os participantes definidos pela equipe, considerando identificação dos campos, indicação de obrigatoriedade, mensagens e navegação, com registro das dificuldades encontradas. | Planejado |
| RNF04 — Integridade e persistência | Verificação das validações, dos relacionamentos, da preservação dos dados em operações rejeitadas e da permanência dos dados após reiniciar a aplicação. Para CA25, há sugestão de CT17, ainda pendente de definição. | Planejado |
| RNF05 — Manutenibilidade | Revisão da organização do código e da separação de responsabilidades, acompanhada dos resultados da análise estática e do quality gate adotado pelo projeto. | Planejado |
| RNF06 — Testabilidade | Verificação da execução dos testes com Maven e na integração contínua, além da geração do relatório JaCoCo com cobertura mínima de 75%. | Planejado |

As formas de verificação indicam como os requisitos poderão ser avaliados, sem constituir casos de teste detalhados ou evidências de atendimento.

Nem toda verificação exige um teste automatizado. Revisão de código, inspeção de configuração, avaliação de usabilidade e análise de relatórios também podem compor as evidências.

## 4. Prioridade de implementação

A primeira etapa de desenvolvimento deverá priorizar:

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

## 5. Situação dos testes e responsabilidades

Os identificadores CT01 a CT16 representam testes previstos, cujas definições individuais ainda estão pendentes. O CT17 é proposto para atender ao CA25, mas sua inclusão e definição dependem da pessoa responsável pelos testes.

A issue D01 organiza os vínculos entre requisitos, regras de negócio, critérios de aceite e testes previstos. O detalhamento dos casos, com precondições, dados, passos e resultados esperados, pertence à atividade de planejamento de testes.

O acompanhamento deverá distinguir:

- Planejado: teste ou verificação previsto, ainda sem implementação confirmada.
- Implementado: teste existente, com referência ao arquivo e método correspondente, quando automatizado.
- Executado: teste ou verificação realizado, com resultado e evidência registrados.

Um teste executado pode ser aprovado ou reprovado. A aprovação somente deve ser registrada quando o resultado observado atender ao comportamento esperado.

Nesta revisão documental, não foi comprovada a implementação ou execução dos CT. Os vínculos permanecem planejados e não representam confirmação de cobertura.

O detalhamento dos casos deverá ser registrado em `docs/testing/plano-de-testes.md` pela pessoa responsável. Os testes automatizados deverão permanecer em `src/test/java`, e os resultados e as referências às evidências deverão ser registrados em `docs/testing/evidencias.md`.

A matriz deverá ser atualizada conforme os casos forem definidos, implementados e executados, ou sempre que houver alterações nos requisitos, nas regras ou nos critérios de aceite.

## 6. Pendências de acompanhamento

- Confirmar as prioridades das funcionalidades das etapas seguintes da entrega final da A3.
- Aguardar a definição dos CT01 a CT16 pela pessoa responsável pelos testes e conferir os vínculos propostos.
- Confirmar a inclusão e a definição do CT17 para o CA25.
- Confirmar a distribuição dos CA14 e CA22 entre CT13 e CT14.
- Conferir a cobertura dos CA17 a CA27 após o detalhamento dos casos.
- Relacionar os testes aos arquivos e métodos efetivamente implementados.
- Registrar os resultados e as evidências das execuções.
- Atualizar a situação das verificações dos requisitos não funcionais conforme sua realização.


# Matriz de Rastreabilidade — VIGOR

## 1. Objetivo e situação

Relacionar os requisitos funcionais e não funcionais do VIGOR às regras de negócio, aos critérios de aceite e aos casos ou verificações definidos em `docs/testing/plano-de-testes.md`.

Os CT01 a CT22 e VN01 a VN06 possuem situações registradas no plano de testes. Os resultados globais não comprovam automaticamente a execução integral de cada cenário. A D05 possui verificações locais documentadas em EV21–EV24, incluindo testes automatizados e verificações manuais parciais. O M09 tem execução focada parcial documentada em EV25. A aprovação integral de um CT depende da evidência correspondente a todos os seus passos e pré-condições.

## 2. Requisitos funcionais

Os nomes dos requisitos abaixo reproduzem os definidos em `docs/requirements/requisitos.md`.

| Requisito funcional | Regras de negócio relacionadas | Critérios de aceite relacionados | Casos de teste relacionados | Situação |
|---|---|---|---|---|
| RF01 — Autenticar funcionários | RN18 — Dados obrigatórios do funcionário; RN19 — Login único; RN20 — Validação das credenciais | CA15, CA16, CA23 | CT15, CT16 | Fluxo integrado planejado; existência de BCrypt e autenticação no serviço não comprova proteção das rotas. |
| RF02 — Cadastrar membros | RN01 — Campos obrigatórios do membro; RN02 — Identificação e data de cadastro; RN03 — Validade do CPF; RN04 — CPF único; RN21 — Não persistência de dados inválidos | CA01 — Cadastro válido; CA02 — Campos obrigatórios não preenchidos; CA03 — CPF inválido; CA04 — CPF duplicado; CA24 — Identificação e data de cadastro; CA26 — Normalização e validação do CPF | CT01, CT02, CT03, CT04; CT17 (persistência após cadastro) | Planejado |
| RF03 — Consultar membros | Não se aplica regra de negócio específica | CA05 — Consulta de membros cadastrados; CA06 — Consulta sem resultados | CT05, CT06 | Planejado |
| RF04 — Atualizar membros | RN01 — Campos obrigatórios do membro; RN02 — Identificação e data de cadastro; RN03 — Validade do CPF; RN04 — CPF único; RN05 — Atualização válida; RN21 — Não persistência de dados inválidos | CA07 — Atualização válida; CA17 — Atualização inválida; CA18 — Manutenção do CPF do próprio membro; CA24 — Identificação e data de cadastro; CA26 — Normalização e validação do CPF | CT07; CT17 (persistência após atualização) | Planejado |
| RF05 — Excluir membros | RN06 — Exclusão de membros; RN21 — Não persistência de dados inválidos | CA08 — Exclusão de membro sem vínculos; CA19 — Exclusão bloqueada por vínculos | CT08 | Planejado |
| RF06 — Cadastrar treinos | RN07 — Vínculo do treino; RN08 — Dados obrigatórios do treino; RN09 — Duração e data do treino; RN21 — Não persistência de dados inválidos | CA09 — Cadastro de treino; CA10 — Treino sem membro ou associado a membro inexistente; CA21 — Dados de treino inválidos | CT09 | Planejado |
| RF07 — Consultar treinos | RN07 — Vínculo do treino e isolamento da associação consultada | CA20 — Consulta de treinos; CA30 — Seleção e referência de membro | CT10, CT22 | Parcialmente verificado em M09; testes PostgreSQL pendentes. |
| RF08 — Registrar pagamentos | RN10, RN11, RN12, RN13, RN21 | CA11, CA12, CA27; CA25 quanto à persistência após reinício | CT11, CT12, CT21 | Parcialmente verificado — EV21–EV24; limites descritos no plano. |
| RF09 — Consultar histórico de atividades | RN14, RN15, RN16, RN17, RN21 | CA14, CA22, CA30; CA28 e CA29 para preparação técnica | CT13, CT14, CT22; CA28 e CA29 permanecem pendentes | Parcialmente verificado em M09; testes PostgreSQL pendentes. A carga técnica permanece no L03. |
| RF10 — Consultar pagamentos | RN10 quanto à associação dos registros consultados | CA31, CA32, CA33, CA34; CA25 quanto à persistência após reinício | CT18, CT19, CT20, CT21 | Parcialmente verificado — EV23 e EV24; limites descritos no plano. |

## 3. Requisitos não funcionais

Os nomes reproduzem `docs/requirements/requisitos.md`. Resultados observados e verificações pendentes devem ser distinguidos. Evidências locais não substituem a conferência da integração contínua nem a execução integral dos cenários.

| Requisito não funcional | Forma de verificação e vínculos | Evidências esperadas ou registradas | Verificação / CT | Situação |
|---|---|---|---|---|
| RNF01 — Segurança | Verificar restrição de acesso a funcionários autenticados e armazenamento de senhas por hash apropriado, sem texto puro. Relaciona-se a RF01 e aos CA15, CA16 e CA23. | Registro dos resultados dos logins e do acesso sem autenticação; registro da inspeção da credencial armazenada sem expor senha. | VN01; CT15, CT16 | Planejado |
| RNF02 — Desempenho | Avaliar os fluxos no ambiente de testes e registrar tempos observados nas consultas e ocorrência de travamentos ou erros de tempo limite. Não há limite numérico de resposta definido. | Ambiente, volume de dados, tempos observados e registro de travamentos/timeouts. | VN02 | Planejado |
| RNF03 — Usabilidade | Avaliar os fluxos principais quanto à identificação de campos, indicação de obrigatoriedade e clareza das mensagens de validação, sucesso e ausência de registros. | Registro da avaliação, dificuldades observadas e correções necessárias. | VN03 | Planejado |
| RNF04 — Integridade e persistência | Verificar persistência, rejeição de dados inválidos e integridade. CT17 trata de membros após reinício; CT21 trata de pagamentos após reinício. As demais entidades não ficam cobertas automaticamente. | Testes de integração e verificações manuais relacionados em `docs/testing/evidencias.md`, com limites por cenário. | VN04; CT08–CT13; CT17; CT21 | Parcialmente verificado; aprovação por cenário depende das evidências específicas. |
| RNF05 — Manutenibilidade | Revisar a separação entre apresentação, regras de negócio e persistência e a execução de análise estática. | Registro da revisão, ferramenta/configuração de análise e resultado do quality gate adotado. | VN05 | Planejado |
| RNF06 — Testabilidade | Verificar execução Maven, JaCoCo e CI. A execução local da D05 de 21/11/2026 registrou 238 testes sem falhas, erros ou ignorados; cobertura de instruções de 83% e branches de 64%; limite de 75% de instruções aprovado. | EV21–EV23; relatório e saída da execução local. CI da D05 verificada na execução 44972713889: 238 testes sem falhas, erros ou ignorados, limite JaCoCo aprovado e relatório publicado. Referência em evidencias.md. | VN06 | Parcialmente verificado. |

Os resultados da D05 indicados nesta seção correspondem à execução anterior à atualização da branch com a D04. Uma execução posterior deve ter seu próprio resultado registrado.

## 4. Cobertura dos critérios de aceite

Os nomes dos critérios reproduzem `docs/requirements/criterios-de-aceite.md`. A coluna CT segue a definição individual em `docs/testing/plano-de-testes.md`.

| Critério de aceite | Requisito(s) | Regra(s) relacionada(s) | Caso(s) relacionado(s) | Cobertura |
|---|---|---|---|---|
| CA01 — Cadastro válido | RF02 — Cadastrar membros | RN01, RN03, RN04, RN21 | CT01 | Planejado |
| CA02 — Campos obrigatórios não preenchidos | RF02 — Cadastrar membros | RN01, RN21 | CT02 | Planejado |
| CA03 — CPF inválido | RF02 — Cadastrar membros | RN03, RN21 | CT03 | Planejado |
| CA04 — CPF duplicado | RF02 — Cadastrar membros | RN04, RN21 | CT04 | Planejado |
| CA05 — Consulta de membros cadastrados | RF03 — Consultar membros | Sem regra específica | CT05 | Planejado |
| CA06 — Consulta sem resultados | RF03 — Consultar membros | Sem regra específica | CT06 | Planejado |
| CA07 — Atualização válida | RF04 — Atualizar membros | RN01, RN03, RN04, RN05 | CT07 | Planejado |
| CA08 — Exclusão de membro sem vínculos | RF05 — Excluir membros | RN06 | CT08 | Planejado |
| CA09 — Cadastro de treino | RF06 — Cadastrar treinos | RN07, RN08, RN09 | CT09 | Planejado |
| CA10 — Treino sem membro ou associado a membro inexistente | RF06 — Cadastrar treinos | RN07, RN21 | CT09 | Planejado |
| CA11 — Registro de pagamento válido | RF08 — Registrar pagamentos | RN10, RN11, RN12, RN13 | CT11 | Parcialmente verificado — EV23 e EV24; limites em CT11. |
| CA12 — Dados de pagamento inválidos | RF08 — Registrar pagamentos | RN10, RN11, RN12, RN13, RN21 | CT12 | Parcialmente verificado — EV23; limites em CT12. |
| CA13 — Registro de atividade — Futuro | Fora do escopo desta entrega; sem requisito funcional de registro pela interface | Não aplicável a um fluxo de registro por interface nesta entrega | Nenhum; sem CT por escopo | Não aplicável |
| CA14 — Consulta de histórico | RF09 — Consultar histórico de atividades | RN14, RN15, RN16 (verificação dos registros em CT13); RN17 (consulta) | CT13 | Parcialmente verificado — EV25; PostgreSQL pendente. |
| CA15 — Login válido | RF01 — Autenticar funcionários | RN18, RN19 (verificações de dados/hash em CT15); RN20 | CT15 | Planejado |
| CA16 — Login inválido | RF01 — Autenticar funcionários | RN20 | CT16 | Planejado |
| CA17 — Atualização inválida | RF04 — Atualizar membros | RN01, RN03, RN04, RN05, RN21 | CT07 | Planejado |
| CA18 — Manutenção do CPF do próprio membro | RF04 — Atualizar membros | RN04, RN05 | CT07 | Planejado |
| CA19 — Exclusão bloqueada por vínculos | RF05 — Excluir membros | RN06, RN21 | CT08 | Planejado |
| CA20 — Consulta de treinos | RF07 — Consultar treinos | RN07 (vínculo dos registros consultados) | CT10 | Parcialmente verificado — EV25; PostgreSQL pendente. |
| CA21 — Dados de treino inválidos | RF06 — Cadastrar treinos | RN07, RN08, RN09, RN21 | CT09 | Planejado |
| CA22 — Histórico sem atividades | RF09 — Consultar histórico de atividades | RN17 | CT14 | Parcialmente verificado — EV25; PostgreSQL pendente. |
| CA23 — Acesso sem autenticação | RF01 — Autenticar funcionários; RNF01 — Segurança | Restrição de acesso definida em RF01/RNF01 | CT16 | Planejado |
| CA24 — Identificação e data de cadastro | RF02 — Cadastrar membros; RF04 — Atualizar membros | RN02 | CT01, CT07 | Planejado |
| CA25 — Persistência dos dados | RF02, RF04, RF08, RF10; RNF04 | Persistência após reinício | CT17 para membros; CT21 para pagamentos | CT21 parcialmente verificado em EV24; sem aprovação automática de CT17 ou das demais entidades. |
| CA26 — Normalização e validação do CPF | RF02 — Cadastrar membros; RF04 — Atualizar membros | RN03, RN04, RN05, RN21 | CT03, CT04, CT07 | Planejado |
| CA27 — Pagamento associado a membro inexistente | RF08 — Registrar pagamentos | RN10, RN21 | CT12 | Parcialmente verificado — EV23; limites em CT12. |
| CA28 — Registro técnico válido de histórico | RF09, como preparação dos dados | RN14, RN15, RN16 | Caso específico pendente | Lacuna registrada; CT13 não comprova integralmente a carga. |
| CA29 — Rejeição de dados inválidos do histórico | RF09, como preparação dos dados; RNF04 | RN14, RN15, RN16, RN21 | Caso específico pendente | Lacuna registrada. |
| CA30 — Consulta com referência de membro inválida | RF07, RF09 | Validação da seleção e da referência na consulta | CT22 | Parcialmente verificado — EV25; PostgreSQL pendente. |
| CA31 — Listagem geral de pagamentos | RF10 | Sem regra específica de ordenação; comportamento definido no RF10 | CT18 | Parcialmente verificado — EV23 e EV24. |
| CA32 — Consulta de pagamentos por membro | RF10 | RN10 quanto à associação consultada | CT19 | Parcialmente verificado — EV23 e EV24. |
| CA33 — Consulta de pagamentos sem resultados | RF10 | Sem regra específica | CT19 | Parcialmente verificado — EV23; limites descritos no plano. |
| CA34 — Consulta de pagamentos com referência inválida | RF10 | Validação da referência definida no RF10 | CT20 | Parcialmente verificado — EV23. |

## 5. Relação das regras de negócio

Esta seção diferencia a existência de um CT da existência de um procedimento que verifica a regra. “Procedimento definido” significa que o CT descreve passos e resultado esperado; não significa que o teste tenha sido executado ou aprovado. “Parcialmente coberta” indica que há verificações descritas, mas ainda existe uma dependência que impede a cobertura completa. A mera preparação de dados não conta como verificação.

| Regra de negócio | Requisito(s) / critério(s) relacionados | CT relacionado(s) | Procedimento e cobertura documentada |
|---|---|---|---|
| RN01 — Campos obrigatórios do membro | RF02/CA02; RF04/CA17 | CT02, CT07 | CT02 e CT07 tentam cada campo ausente, vazio ou só com espaços; espera rejeição e não persistência. Procedimento definido; execução pendente. |
| RN02 — Identificação e data de cadastro | RF02/CA24; RF04/CA24 | CT01, CT07 | CT01 verifica geração automática de ID e data; CT07 verifica sua preservação na atualização. Procedimento definido; execução pendente. |
| RN03 — Validade do CPF | RF02/CA03, CA26; RF04/CA17, CA26 | CT03, CT07 | CT03 e CT07 verificam quantidade de dígitos, dígitos verificadores e sequências repetidas, com e sem pontuação; esperam rejeição. Procedimento definido; execução pendente. |
| RN04 — CPF único | RF02/CA04, CA26; RF04/CA17, CA18, CA26 | CT04, CT07 | CT04 e CT07 verificam duplicidade com formatações iguais/diferentes, CPF próprio e novo CPF inédito. Procedimento definido; execução pendente. |
| RN05 — Atualização válida | RF04/CA07, CA17, CA18 | CT07 | CT07 verifica atualização válida e rejeição de dados inválidos com preservação dos dados anteriores. Procedimento definido; execução pendente. |
| RN06 — Exclusão de membros | RF05/CA08, CA19 | CT08 | CT08 verifica exclusão sem vínculos e bloqueio com cada tipo de vínculo, preservando registros. Procedimento definido; execução pendente. |
| RN07 — Vínculo do treino | RF06/CA09, CA10, CA21; RF07/CA20, CA30 | CT09, CT10, CT22 | CT09 verifica associação a membro existente e rejeição de inexistente; CT10 e CT22 verificam filtro obrigatório e isolamento da consulta. Testes unitários e de renderização aprovados em M09; PostgreSQL pendente em EV25. |
| RN08 — Dados obrigatórios do treino | RF06/CA09, CA21 | CT09 | CT09 verifica campos obrigatórios, tipo/descrição não vazios e ID automático; entradas inválidas devem ser rejeitadas e não persistidas. Procedimento definido; execução pendente. |
| RN09 — Duração e data do treino | RF06/CA09, CA21 | CT09 | CT09 verifica duração inteira positiva em minutos e data válida, incluindo rejeição de valores inválidos. Procedimento definido; execução pendente. |
| RN10 — Vínculo do pagamento | RF08/CA11, CA12, CA27; RF10/CA32 | CT11, CT12, CT19 | Verificações de membro existente, rejeição de referência inválida no registro e isolamento da consulta por membro. Cobertura parcial relacionada em EV23 e EV24. |
| RN11 — Dados obrigatórios do pagamento | RF08/CA11, CA12 | CT11, CT12 | Validação dos dados obrigatórios; status atribuído pela aplicação antes da persistência. Cobertura parcial relacionada em EV23. |
| RN12 — Valor do pagamento | RF08/CA11, CA12 | CT11, CT12 | Valor positivo com até 10 dígitos inteiros e duas casas decimais; rejeição das demais entradas previstas. Cobertura parcial relacionada em EV23. |
| RN13 — Status do pagamento | RF08/CA11, CA12 | CT11, CT12 | CT11 verifica atribuição automática de `PAGO` e que o cliente não a substitui. CT12 verifica status inválido na validação interna; ausência de status no formulário é permitida. Cobertura parcial relacionada em EV23. |
| RN14 — Vínculos da atividade | RF09/CA14, CA28, CA29 | CT13; casos específicos de CA28 e CA29 pendentes | A V5 e a entidade definem vínculos obrigatórios com membro e funcionário. A existência do mapeamento não comprova execução da carga técnica ou de seus testes. |
| RN15 — Dados obrigatórios da atividade | RF09/CA14, CA28, CA29 | CT13; casos específicos de CA28 e CA29 pendentes | Dados obrigatórios definidos no modelo e na V5. Verificação integral da carga técnica permanece pendente de procedimento e evidências. |
| RN16 — Tempo de execução | RF09/CA14, CA28, CA29 | CT13; casos específicos de CA28 e CA29 pendentes | CT13 confere tempo inteiro positivo nos registros consultados. Cenários específicos de carga válida e rejeição de carga inválida permanecem pendentes. |
| RN17 — Consulta do histórico | RF09/CA14, CA22, CA30 | CT13, CT14, CT22 | CT13 compara as atividades consultadas com os registros do membro; CT14 verifica a mensagem de ausência; CT22 verifica seleção e referência. Testes unitários e de renderização aprovados em M09; PostgreSQL pendente em EV25. |
| RN18 — Dados obrigatórios do funcionário | RF01/CA15; RNF01 | CT15 | Entidade, configuração BCrypt e comparação por `matches` identificadas na D04. Provisionamento documentado e execução integrada do CT15 não estão comprovados por essa identificação. |
| RN19 — Login único | RF01/CA15 | CT15 | Comparar logins com normalização equivalente a `lower(btrim(login))`. Índice único definido; execução específica do cenário deve ser registrada. |
| RN20 — Validação das credenciais | RF01/CA15, CA16 | CT15, CT16 | CT15 verifica autenticação válida; CT16 verifica login inexistente, senha incorreta e acesso sem autenticação. Procedimento definido; execução integrada pendente de comprovação. |
| RN21 — Não persistência de dados inválidos | RF02/CA02–CA04, CA26; RF04/CA17, CA26; RF05/CA19; RF06/CA10, CA21; RF08/CA12, CA27; RF09/CA29 | CT02–CT04, CT07–CT09, CT12; caso específico de CA29 pendente | Os CT verificam rejeição e não persistência ou preservação dos dados conforme a operação. Verificações de pagamentos parcialmente relacionadas em EV23; os demais cenários mantêm seus próprios estados. |

## 6. Lacunas, testes sem vínculo e divergências

- **Requisitos sem caso planejado:** RF01–RF10 possuem casos relacionados. CA28 e CA29 permanecem pendentes de procedimentos específicos da carga técnica no L03. O CA30 está associado ao CT22. CA13 permanece futuro e sem teste por não integrar esta entrega.

- **Regras com procedimento definido:** RN01–RN13, RN16–RN17 e RN19–RN21 têm passos e resultados esperados descritos nos CT correspondentes; RN14, RN15 e RN18 também têm verificações descritas, com dependências indicadas abaixo. A existência de procedimento não comprova execução. As verificações parciais de pagamentos estão relacionadas aos CT11, CT12 e CT18–CT21.

- **Regras usadas somente como preparação:** nenhuma das RN14, RN15, RN16, RN18 ou RN19 permanece apenas como pré-condição. CT13 inspeciona os registros de atividades e seus vínculos/dados; CT15 verifica os dados dos funcionários, a unicidade dos logins e o procedimento de hash. A carga técnica continua sendo necessária para fornecer os registros de teste. Essas verificações não comprovam integralmente os cenários de carga dos CA28 e CA29.

- **Cobertura parcial e dependências:** conforme a revisão D04, a representação física do funcionário responsável no histórico está definida na V5, e foram identificadas a entidade de funcionário, a configuração BCrypt e a autenticação no serviço. Permanecem pendentes de comprovação o provisionamento técnico documentado e o fluxo integrado de autenticação e proteção das rotas. RN19 depende da disponibilização dos registros de funcionários para sua verificação. Os cenários de preparação técnica dos CA28 e CA29 permanecem no L03; o CA30 está associado ao CT22 e tem execução parcial registrada em EV25.

- **Execução e cobertura automatizadas:** a execução global de 10/11/2026 permanece registrada em `docs/testing/evidencias.md`, incluindo 37 testes aprovados e os resultados JaCoCo. Na D05, EV21–EV23 registram a execução local de 21/11/2026 com 238 testes sem falhas, erros ou ignorados, cobertura de 83% de instruções e 64% de branches e aprovação do limite de 75% de instruções. Esses resultados não equivalem à execução integral de cada CT/VN. CI da D05 verificada na execução 44972713889: 238 testes sem falhas, erros ou ignorados, limite JaCoCo aprovado e relatório publicado. Referência em evidencias.md.

- **Atualização após a auditoria da Issue #20:** ficam preservadas as referências estabelecidas nessa revisão: CT07 para atualização, CT10 para consulta e conferência dos detalhes do treino, CT12 para pagamentos inválidos/inexistência de membro e CT15 para os dados do funcionário e verificação planejada da proteção por hash.

- **Complementação da D05:** CT11 e CT12 distinguem a atribuição automática de `PAGO` no fluxo web da validação interna do registro. CT18–CT20 contemplam listagem, consulta por membro, conjuntos vazios e referência inválida. CT21 trata da persistência de pagamentos após reinício. As verificações parciais estão registradas em EV23 e EV24; as etapas ainda não comprovadas mantêm as limitações descritas no plano.

- **Testes sem requisito associado:** nenhum dos CT01–CT22 ou VN01–VN06 está sem vínculo documentado. Isso não elimina as lacunas dos CA28 e CA29 nem comprova aprovação integral dos cenários.

## 7. Acompanhamento

A situação de cada CT/VN deve acompanhar as evidências específicas. Uma verificação parcial não deve ser apresentada como execução integral, e a aprovação global do build não aprova automaticamente os casos funcionais.

Ao concluir uma execução, registrar data, ambiente, resultado, referência ao teste ou procedimento e limitações em `docs/testing/evidencias.md`, atualizando o plano e esta matriz.

Os CA28 e CA29 permanecem vinculados à carga técnica do histórico no L03; sua inclusão nesta matriz não declara os procedimentos concluídos. O CA30 tem consulta e referência vinculadas ao CT22, com limitações da execução registradas em EV25.

O CA13 permanece futuro e não exige uma interface de registro de atividades nesta entrega.

# Matriz de Rastreabilidade — VIGOR

## 1. Objetivo e situação

Relacionar os requisitos funcionais e não funcionais do VIGOR às regras de negócio, aos critérios de aceite e aos casos ou verificações definidos em `docs/testing/plano-de-testes.md`.

Os CT01 a CT17 e as verificações VN01 a VN06 estão **Planejados**. Esta matriz não registra implementação, execução, aprovação ou evidências já obtidas. As evidências esperadas indicadas abaixo são registros a produzir quando as verificações forem realizadas.

## 2. Requisitos funcionais

Os nomes dos requisitos abaixo reproduzem os definidos em `docs/requirements/requisitos.md`.

| Requisito funcional | Regras de negócio relacionadas | Critérios de aceite relacionados | Casos de teste relacionados | Situação |
|---|---|---|---|---|
| RF01 — Autenticar funcionários | RN18 — Dados obrigatórios do funcionário (preparação); RN19 — Login único (preparação); RN20 — Validação das credenciais | CA15 — Login válido; CA16 — Login inválido; CA23 — Acesso sem autenticação | CT15, CT16 | Planejado |
| RF02 — Cadastrar membros | RN01 — Campos obrigatórios do membro; RN02 — Identificação e data de cadastro; RN03 — Validade do CPF; RN04 — CPF único; RN21 — Não persistência de dados inválidos | CA01 — Cadastro válido; CA02 — Campos obrigatórios não preenchidos; CA03 — CPF inválido; CA04 — CPF duplicado; CA24 — Identificação e data de cadastro; CA26 — Normalização e validação do CPF | CT01, CT02, CT03, CT04; CT17 (persistência após cadastro) | Planejado |
| RF03 — Consultar membros | Não se aplica regra de negócio específica | CA05 — Consulta de membros cadastrados; CA06 — Consulta sem resultados | CT05, CT06 | Planejado |
| RF04 — Atualizar membros | RN01 — Campos obrigatórios do membro; RN02 — Identificação e data de cadastro; RN03 — Validade do CPF; RN04 — CPF único; RN05 — Atualização válida; RN21 — Não persistência de dados inválidos | CA07 — Atualização válida; CA17 — Atualização inválida; CA18 — Manutenção do CPF do próprio membro; CA24 — Identificação e data de cadastro; CA26 — Normalização e validação do CPF | CT07; CT17 (persistência após atualização) | Planejado |
| RF05 — Excluir membros | RN06 — Exclusão de membros; RN21 — Não persistência de dados inválidos | CA08 — Exclusão de membro sem vínculos; CA19 — Exclusão bloqueada por vínculos | CT08 | Planejado |
| RF06 — Cadastrar treinos | RN07 — Vínculo do treino; RN08 — Dados obrigatórios do treino; RN09 — Duração e data do treino; RN21 — Não persistência de dados inválidos | CA09 — Cadastro de treino; CA10 — Treino associado a membro inexistente; CA21 — Dados de treino inválidos | CT09 | Planejado |
| RF07 — Consultar treinos | RN07 — Vínculo do treino (consistência da associação consultada) | CA20 — Consulta de treinos | CT10 | Planejado |
| RF08 — Registrar pagamentos | RN10 — Vínculo do pagamento; RN11 — Dados obrigatórios do pagamento; RN12 — Valor do pagamento; RN13 — Status do pagamento; RN21 — Não persistência de dados inválidos | CA11 — Registro de pagamento válido; CA12 — Dados de pagamento inválidos; CA27 — Pagamento associado a membro inexistente | CT11, CT12 | Planejado |
| RF09 — Consultar histórico de atividades | RN14 — Vínculos da atividade (preparação); RN15 — Dados obrigatórios da atividade (preparação); RN16 — Tempo de execução (preparação); RN17 — Consulta do histórico | CA14 — Consulta de histórico; CA22 — Histórico sem atividades | CT13, CT14 | Planejado |

## 3. Requisitos não funcionais

Os nomes reproduzem `docs/requirements/requisitos.md`. Os registros na coluna de evidência são esperados, não resultados existentes.

| Requisito não funcional | Forma de verificação e vínculos | Evidências esperadas | Verificação / CT | Situação |
|---|---|---|---|---|
| RNF01 — Segurança | Verificar restrição de acesso a funcionários autenticados e armazenamento de senhas por hash apropriado, sem texto puro. Relaciona-se a RF01 e aos CA15, CA16 e CA23. | Registro dos resultados dos logins e do acesso sem autenticação; registro da inspeção da credencial armazenada sem expor senha. | VN01; CT15, CT16 | Planejado |
| RNF02 — Desempenho | Avaliar os fluxos no ambiente de testes e registrar tempos observados nas consultas e ocorrência de travamentos ou erros de tempo limite. Não há limite numérico de resposta definido. | Ambiente, volume de dados, tempos observados e registro de travamentos/timeouts. | VN02 | Planejado |
| RNF03 — Usabilidade | Avaliar os fluxos principais quanto à identificação de campos, indicação de obrigatoriedade e clareza das mensagens de validação, sucesso e ausência de registros. | Registro da avaliação, dificuldades observadas e correções necessárias. | VN03 | Planejado |
| RNF04 — Integridade e persistência | Verificar persistência no PostgreSQL, rejeição de dados inválidos, integridade dos relacionamentos e disponibilidade após reinício. Relaciona-se também ao CA25. | Resultado dos cenários de integridade e registro da consulta após reinício efetivo da aplicação. | VN04; CT17 e casos funcionais relacionados | Planejado |
| RNF05 — Manutenibilidade | Revisar a separação entre apresentação, regras de negócio e persistência e a execução de análise estática. | Registro da revisão, ferramenta/configuração de análise e resultado do quality gate adotado. | VN05 | Planejado |
| RNF06 — Testabilidade | Verificar testes automatizados com Maven e na integração contínua e geração de relatório JaCoCo. A meta de 75% é de cobertura, não substitui a cobertura de requisitos. | Saída/relatório de `mvn test`, resultado da integração contínua e relatório JaCoCo com percentual medido. | VN06 | Planejado |

## 4. Cobertura dos critérios de aceite

Os nomes dos critérios reproduzem `docs/requirements/criterios-de-aceite.md`. A coluna CT segue a definição individual em `docs/testing/plano-de-testes.md`.

| Critério de aceite | Requisito(s) | Regra(s) relacionada(s) | Caso(s) planejado(s) | Cobertura |
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
| CA10 — Treino associado a membro inexistente | RF06 — Cadastrar treinos | RN07, RN21 | CT09 | Planejado |
| CA11 — Registro de pagamento válido | RF08 — Registrar pagamentos | RN10, RN11, RN12, RN13 | CT11 | Planejado |
| CA12 — Dados de pagamento inválidos | RF08 — Registrar pagamentos | RN11, RN12, RN13, RN21 | CT12 | Planejado |
| CA13 — Registro de atividade — Futuro | Fora do escopo desta entrega; sem requisito funcional de registro pela interface | Não aplicável a um fluxo de registro por interface nesta entrega | Nenhum; sem CT por escopo | Não aplicável |
| CA14 — Consulta de histórico | RF09 — Consultar histórico de atividades | RN14, RN15, RN16 (preparação); RN17 (consulta) | CT13 | Planejado |
| CA15 — Login válido | RF01 — Autenticar funcionários | RN18, RN19 (preparação); RN20 | CT15 | Planejado |
| CA16 — Login inválido | RF01 — Autenticar funcionários | RN20 | CT16 | Planejado |
| CA17 — Atualização inválida | RF04 — Atualizar membros | RN01, RN03, RN04, RN05, RN21 | CT07 | Planejado |
| CA18 — Manutenção do CPF do próprio membro | RF04 — Atualizar membros | RN04, RN05 | CT07 | Planejado |
| CA19 — Exclusão bloqueada por vínculos | RF05 — Excluir membros | RN06, RN21 | CT08 | Planejado |
| CA20 — Consulta de treinos | RF07 — Consultar treinos | RN07 (vínculo dos registros consultados) | CT10 | Planejado |
| CA21 — Dados de treino inválidos | RF06 — Cadastrar treinos | RN07, RN08, RN09, RN21 | CT09 | Planejado |
| CA22 — Histórico sem atividades | RF09 — Consultar histórico de atividades | RN17 | CT14 | Planejado |
| CA23 — Acesso sem autenticação | RF01 — Autenticar funcionários; RNF01 — Segurança | Restrição de acesso definida em RF01/RNF01 | CT16 | Planejado |
| CA24 — Identificação e data de cadastro | RF02 — Cadastrar membros; RF04 — Atualizar membros | RN02 | CT01, CT07 | Planejado |
| CA25 — Persistência dos dados | RNF04 — Integridade e persistência; fluxos de cadastro/atualização em RF02/RF04 | Persistência definida diretamente em RNF04 | CT17 | Planejado |
| CA26 — Normalização e validação do CPF | RF02 — Cadastrar membros; RF04 — Atualizar membros | RN03, RN04, RN05, RN21 | CT03, CT04, CT07 | Planejado |
| CA27 — Pagamento associado a membro inexistente | RF08 — Registrar pagamentos | RN10, RN21 | CT12 | Planejado |

## 5. Relação das regras de negócio

Esta tabela torna explícita a rastreabilidade de RN que se aplicam à preparação de dados ou a mais de um fluxo. Os nomes são os definidos em `docs/requirements/regras-de-negocio.md`.

| Regra de negócio | Requisito(s) / critério(s) relacionados | CT relacionado(s) | Observação de cobertura |
|---|---|---|---|
| RN01 — Campos obrigatórios do membro | RF02/CA02; RF04/CA17 | CT02, CT07 | Planejado |
| RN02 — Identificação e data de cadastro | RF02/CA24; RF04/CA24 | CT01, CT07 | Planejado |
| RN03 — Validade do CPF | RF02/CA03, CA26; RF04/CA17, CA26 | CT03, CT07 | Planejado |
| RN04 — CPF único | RF02/CA04, CA26; RF04/CA17, CA18, CA26 | CT04, CT07 | Planejado |
| RN05 — Atualização válida | RF04/CA07, CA17, CA18 | CT07 | Planejado |
| RN06 — Exclusão de membros | RF05/CA08, CA19 | CT08 | Planejado |
| RN07 — Vínculo do treino | RF06/CA09, CA10, CA21; RF07/CA20 | CT09, CT10 | CT10 usa a relação para conferir os treinos do membro selecionado; planejado |
| RN08 — Dados obrigatórios do treino | RF06/CA09, CA21 | CT09 | Planejado |
| RN09 — Duração e data do treino | RF06/CA09, CA21 | CT09 | Planejado |
| RN10 — Vínculo do pagamento | RF08/CA11, CA27 | CT11, CT12 | Planejado |
| RN11 — Dados obrigatórios do pagamento | RF08/CA11, CA12 | CT11, CT12 | Planejado |
| RN12 — Valor do pagamento | RF08/CA11, CA12 | CT11, CT12 | Planejado |
| RN13 — Status do pagamento | RF08/CA11, CA12 | CT11, CT12 | Planejado |
| RN14 — Vínculos da atividade | RF09/CA14 (preparação dos dados) | CT13 | Dados previamente carregados, conforme pré-condições do caso; planejado |
| RN15 — Dados obrigatórios da atividade | RF09/CA14 (preparação dos dados) | CT13 | Dados previamente carregados, conforme pré-condições do caso; planejado |
| RN16 — Tempo de execução | RF09/CA14 (preparação dos dados) | CT13 | Dados previamente carregados, conforme pré-condições do caso; planejado |
| RN17 — Consulta do histórico | RF09/CA14, CA22 | CT13, CT14 | Planejado |
| RN18 — Dados obrigatórios do funcionário | RF01/CA15 (preparação do funcionário) | CT15 | Funcionário previamente disponibilizado; não implica tela de cadastro; planejado |
| RN19 — Login único | RF01/CA15 (preparação do funcionário) | CT15 | Funcionário previamente disponibilizado; planejado |
| RN20 — Validação das credenciais | RF01/CA15, CA16 | CT15, CT16 | Planejado |
| RN21 — Não persistência de dados inválidos | RF02/CA02–CA04, CA26; RF04/CA17, CA26; RF05/CA19; RF06/CA10, CA21; RF08/CA12, CA27 | CT02–CT04, CT07–CT09, CT12 | Regra transversal; cenários específicos seguem os casos listados; planejado |

## 6. Lacunas, testes sem vínculo e divergências

- **Requisitos sem teste associado:** não há RF01–RF09 sem CT no plano. Cada RNF01–RNF06 tem uma verificação VN01–VN06 planejada; RNF01 e RNF04 também se relacionam a CT funcionais indicados nas tabelas.
- **Critérios aplicáveis sem CT associado:** nenhum. CA13 é futuro e não aplicável à entrega; não recebe CT.
- **Testes sem requisito associado:** nenhum. CT01–CT17 estão ligados a RF e/ou RNF e a critérios aplicáveis. VN01–VN06 estão ligados aos respectivos RNF.
- **Regras sem verificação associada:** nenhuma regra RN01–RN21 fica sem CT relacionado, consideradas as regras RN14–RN16 como condições de preparação dos dados do CT13 e RN18–RN19 como condições de preparação do funcionário do CT15.
- **Divergências entre fontes consultadas:** não foram identificadas contradições entre requisitos, regras de negócio, critérios de aceite e plano de testes nesta revisão.
- **Limite do mapeamento:** a relação documenta cobertura planejada a partir dos passos e pré-condições dos CT; não comprova implementação ou execução.

## 7. Acompanhamento

Os CT01–CT17 e VN01–VN06 devem permanecer com situação **Planejado** até que haja evidência real de implementação ou execução, conforme aplicável. Quando executados, registrar resultado, data e referência à evidência em `docs/testing/evidencias.md` e atualizar esta matriz sem inferir aprovação apenas pela existência do caso.

O CA13 permanece identificado como futuro no documento oficial de critérios de aceite. Esta matriz não o converte em requisito, teste ou atividade de registro de atividades pela interface.

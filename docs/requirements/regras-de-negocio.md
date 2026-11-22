# Regras de Negócio — VIGOR

## 1. Objetivo

Definir as regras aplicáveis ao escopo documentado em `requisitos.md`, orientando a implementação, os critérios de aceitação e os testes.

As regras descrevem o comportamento esperado e não indicam, por si só, que esse comportamento já esteja implementado ou testado.


## 2. Regras de membros

| ID | Regra de negócio | Especificação |
|---|---|---|
| RN01 | Campos obrigatórios do membro | Nome, CPF, telefone e endereço são obrigatórios. Valores ausentes, vazios ou compostos apenas por espaços devem ser rejeitados. |
| RN02 | Identificação e data de cadastro | O identificador do membro deve ser gerado automaticamente e ser único. A data de cadastro deve ser preenchida automaticamente no cadastro. Ambos devem ser preservados nas atualizações. |
| RN03 | Validade do CPF | O CPF pode ser informado com ou sem pontuação. Para validá-lo, o sistema remove os espaços das extremidades, os pontos e os hífens. O resultado deve conter exatamente 11 dígitos de 0 a 9 e os dois dígitos verificadores devem estar corretos. Letras, espaços internos e sequências com todos os dígitos iguais, como `00000000000` e `11111111111`, tornam o CPF inválido. |
| RN04 | CPF único | No cadastro, após a validação dos dados, o sistema deve consultar a existência do CPF pelo valor normalizado conforme a RN03. Se esse valor já estiver cadastrado, deve rejeitar o novo cadastro e preservar o registro existente. Caso contrário, deve gravar o CPF normalizado. Na atualização, deve ser permitido manter o CPF do próprio membro, mas não utilizar o de outro membro, considerando a mesma comparação normalizada. |
| RN05 | Atualização válida | A atualização deve respeitar as mesmas validações aplicáveis ao cadastro. Quando os novos dados forem inválidos, a alteração deve ser rejeitada e os dados anteriormente persistidos devem ser preservados. |
| RN06 | Exclusão de membros | A exclusão somente deve ser permitida quando o membro não possuir treinos, pagamentos ou atividades vinculados. Caso existam vínculos, a exclusão deve ser bloqueada e o motivo deve ser informado, preservando todos os registros. |

## 3. Regras de treinos

| ID | Regra de negócio | Especificação |
|---|---|---|
| RN07 | Vínculo do treino | Cada treino deve estar associado a um membro existente. Não deve ser permitido gravar um treino sem membro ou com referência a um membro inexistente. |
| RN08 | Dados obrigatórios do treino | O treino deve possuir identificador único gerado automaticamente, membro, tipo, descrição, duração e data de início. Antes da validação, o serviço remove os espaços das extremidades de tipo e descrição. Esses campos não podem ser ausentes, vazios nem compostos apenas por espaços. |
| RN09 | Duração e data do treino | A duração é obrigatória e deve ser um número inteiro maior que zero, expresso em minutos. A data de início é obrigatória e deve ser válida. Não há restrição adicional documentada que exija data passada, atual ou futura. |

## 4. Regras de pagamentos

| ID | Regra de negócio | Especificação |
|---|---|---|
| RN10 | Vínculo do pagamento | Cada pagamento deve estar associado a um membro existente. Não deve ser permitido gravar um pagamento sem membro ou com referência a um membro inexistente. |
| RN11 | Dados obrigatórios do pagamento | O pagamento deve possuir identificador único gerado automaticamente, membro, valor, data do pagamento e status. A data do pagamento é obrigatória e deve ser válida. |
| RN12 | Valor do pagamento | O valor é obrigatório, deve ser maior que zero e possuir no máximo 10 dígitos na parte inteira e duas casas decimais, compatível com NUMERIC(12,2). Valores ausentes, não numéricos, iguais a zero, negativos ou que excedam esses limites devem ser rejeitados antes da persistência. |
| RN13 | Status do pagamento | Nesta entrega, o registro representa um pagamento já recebido. O sistema deve atribuir o valor PAGO ao status e não permitir outros valores. A interface pode apresentar o texto Pago. Controle de pagamentos pendentes, vencidos ou cancelados não faz parte desta entrega. |

## 5. Regras de histórico de atividades

| ID | Regra de negócio | Especificação |
|---|---|---|
| RN14 | Vínculos da atividade | Cada registro de atividade deve estar associado a um membro existente e a um funcionário responsável existente. Referências ausentes ou inexistentes devem impedir a gravação. |
| RN15 | Dados obrigatórios da atividade | Cada registro deve possuir identificador único gerado automaticamente, membro, atividade, data, tempo de execução e funcionário responsável. O nome da atividade não pode ser ausente, vazio nem composto apenas por espaços. A data é obrigatória e deve ser válida. |
| RN16 | Tempo de execução | O tempo de execução é obrigatório e deve ser um número inteiro maior que zero, expresso em minutos. |
| RN17 | Consulta do histórico | A consulta deve apresentar somente as atividades vinculadas ao membro selecionado. Quando não houver registros, o sistema deve informar a ausência de atividades. |

Nesta entrega, o histórico será disponibilizado por dados previamente carregados por procedimento técnico documentado. As regras RN14 a RN16 também se aplicam a esses dados.

Não é prevista uma tela de registro, atualização ou exclusão de atividades nesta entrega.

## 6. Regras de funcionários e autenticação

| ID | Regra de negócio | Especificação |
|---|---|---|
| RN18 | Dados obrigatórios do funcionário | Cada funcionário deve possuir identificador único, nome, cargo, login e senha protegida por hash apropriado. Nome, cargo e login não podem ser vazios nem conter somente espaços. O endereço não é previsto para essa entidade. |
| RN19 | Login único | Não deve existir mais de um funcionário com o mesmo login. |
| RN20 | Validação das credenciais | A autenticação somente deve ser permitida quando o login identificar um funcionário existente e a senha informada corresponder à credencial armazenada. Login inexistente ou senha incorreta devem impedir o acesso. |

Os funcionários serão previamente disponibilizados por procedimento técnico documentado. Não é prevista uma tela de cadastro ou gerenciamento de funcionários nesta entrega.

A proteção das senhas e a restrição de acesso às funcionalidades internas são detalhadas no RNF01 de `requisitos.md`.

## 7. Regra geral de integridade

| ID | Regra de negócio | Especificação |
|---|---|---|
| RN21 | Não persistência de dados inválidos | Operações que violem as validações definidas ou referenciem registros inexistentes não devem ser persistidas. Uma operação rejeitada deve preservar os dados válidos anteriormente armazenados. |

## 8. Decisões de implementação para confirmação da equipe

Os requisitos, as regras e os critérios de aceite desta revisão adotam as seguintes definições:

- Os funcionários serão cadastrados previamente por procedimento técnico documentado, sem tela de cadastro ou gerenciamento nesta entrega.
- O histórico de atividades será inicialmente alimentado por carga técnica documentada, respeitando as regras de integridade e mantendo a consulta funcional.
- Os pagamentos representarão valores já recebidos e serão registrados somente com o status PAGO.
- As operações de atualização e exclusão serão previstas somente para membros.
- A gestão de treinos será atendida pelo cadastro e pela consulta.
- A exclusão de membros será bloqueada quando existirem registros vinculados.
- A duração dos treinos e o tempo de execução das atividades serão expressos em minutos inteiros positivos.

Essas decisões estão propostas de forma consistente nos documentos e aguardam confirmação da equipe na revisão deste PR. Não representam, isoladamente, exigências textuais do enunciado do professor.

Caso alguma definição seja alterada, os requisitos, as regras de negócio, os critérios de aceite e os vínculos da matriz deverão ser atualizados em conjunto.

As funcionalidades fora do escopo final estão identificadas na seção 7 de `requisitos.md`.

## 9. Aplicação e verificação

Os critérios de aceitação são mantidos em documento próprio. A associação entre requisitos, regras, critérios e testes deve ser registrada na matriz de rastreabilidade.

Os testes automatizados devem ser implementados em `src/test/java`, conforme a funcionalidade correspondente for desenvolvida. O planejamento e as evidências de execução devem permanecer em `docs/testing`.

As regras referentes a dados previamente carregados também devem ser verificadas, sem que isso exija criar telas fora do escopo.

Não é necessário criar um teste isolado para cada regra. Os cenários devem cobrir os comportamentos relevantes, incluindo situações válidas, inválidas e preservação da integridade dos dados.

Até a execução das verificações, os testes e critérios devem permanecer identificados como planejados, sem indicação de aprovação.

Esta atualização documental não implementa funcionalidades nem comprova a execução de testes.

## 10. Revisão documental D04 — implementação e pendências

A revisão comparou os requisitos e as regras dos módulos de treinos, pagamentos e histórico com as entidades, os serviços, as migrations V3 a V5 e o modelo de dados fornecidos. A conferência foi realizada por leitura dos arquivos, sem execução de testes ou consulta direta ao PostgreSQL.

### Comportamentos identificados no código

- Treinos: cadastro com validação dos campos obrigatórios, vínculo com membro existente, duração inteira positiva e consulta por membro.
- Pagamentos: registro com validação de membro existente, valor positivo com até 10 dígitos inteiros e duas casas decimais, data obrigatória e status exclusivamente PAGO; consulta por membro disponível no serviço.
- Histórico: registro técnico com membro e funcionário responsável existentes, atividade preenchida, data obrigatória e tempo inteiro positivo; consultas por membro e por funcionário disponíveis no serviço.
- As migrations definem identificadores automáticos, campos obrigatórios e chaves estrangeiras com ON DELETE RESTRICT.

### Pendências e limites

- A atribuição automática de PAGO permanece como comportamento esperado de RF08 e RN13. No serviço de pagamentos revisado, o status precisa chegar preenchido: o método registrar valida seu conteúdo, mas não o atribui. A atribuição em outra camada não foi comprovada nesta revisão.
- A D05 prevê listagem e consulta de pagamentos por membro. O serviço já possui consulta por membro, mas os requisitos atuais descrevem somente o registro. A inclusão explícita desse fluxo nos requisitos, critérios e rastreabilidade deve ser alinhada antes da conclusão da D05.
- A existência dos serviços não comprova telas, mensagens, proteção das rotas ou funcionamento integrado. Controllers e templates desses módulos não foram examinados nesta revisão.
- O método de registro de histórico não comprova a existência de um procedimento de carga técnica documentado. Esse procedimento deve respeitar RN14 a RN16, sem exigir uma tela fora do escopo.
- As validações da aplicação e as constraints do banco são complementares. Cargas que não utilizem os serviços também devem respeitar as regras de negócio.
- Nenhum teste ou critério foi declarado aprovado por esta revisão documental. Resultados de execução devem possuir evidências próprias.

### Atualização de acompanhamento — D05

A D05 explicita a listagem geral e a consulta de pagamentos por membro no RF10, nos CA31–CA34 e nos CT18–CT20. O CT21 trata da persistência de pagamentos após reinício.

No fluxo web implementado, o `PagamentoController` atribui `PAGO` antes de chamar o serviço. O `PagamentoService` continua responsável pela validação do registro recebido. A ausência de status na solicitação do funcionário é permitida; o cliente não deve substituir o status definido pela aplicação.

As observações anteriores da D04 descrevem o estado examinado naquela revisão. A atualização da D05 não comprova automaticamente autenticação integrada, execução da CI ou conclusão das pendências da carga técnica do histórico.
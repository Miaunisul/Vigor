# Regras de Negócio — VIGOR

## 1. Objetivo

Definir as regras aplicáveis ao escopo documentado em `requisitos.md`, orientando a implementação, os critérios de aceitação e os testes.

As regras descrevem o comportamento esperado e não indicam, por si só, que esse comportamento já esteja implementado ou testado.


## 2. Regras de membros

| ID | Regra de negócio | Especificação |
|---|---|---|
| RN01 | Campos obrigatórios do membro | Nome, CPF, telefone e endereço são obrigatórios. Valores ausentes, vazios ou compostos apenas por espaços devem ser rejeitados. |
| RN02 | Identificação e data de cadastro | O identificador do membro deve ser gerado automaticamente e ser único. A data de cadastro deve ser preenchida automaticamente no cadastro. Ambos devem ser preservados nas atualizações. |
| RN03 | Validade do CPF | O CPF deve conter 11 dígitos e possuir dígitos verificadores válidos. Sequências com todos os dígitos iguais devem ser rejeitadas. Pontos e hífen de formatação devem ser desconsiderados na validação e na comparação. |
| RN04 | CPF único | Não deve ser permitido cadastrar ou atualizar um membro utilizando o CPF de outro membro. Na atualização, manter o CPF do próprio registro deve ser permitido. A comparação deve utilizar o CPF normalizado, sem pontos ou hífen. |
| RN05 | Atualização válida | A atualização deve respeitar as mesmas validações aplicáveis ao cadastro. Quando os novos dados forem inválidos, a alteração deve ser rejeitada e os dados anteriormente persistidos devem ser preservados. |
| RN06 | Exclusão de membros | A exclusão somente deve ser permitida quando o membro não possuir treinos, pagamentos ou atividades vinculados. Caso existam vínculos, a exclusão deve ser bloqueada e o motivo deve ser informado, preservando todos os registros. |

## 3. Regras de treinos

| ID | Regra de negócio | Especificação |
|---|---|---|
| RN07 | Vínculo do treino | Cada treino deve estar associado a um membro existente. Não deve ser permitido gravar um treino sem membro ou com referência a um membro inexistente. |
| RN08 | Dados obrigatórios do treino | O treino deve possuir identificador único gerado automaticamente, membro, tipo, descrição, duração e data de início. Tipo e descrição não podem ser vazios nem conter somente espaços. |
| RN09 | Duração e data do treino | A duração deve ser um número inteiro positivo, expresso em minutos. A data de início deve ser uma data válida. |

## 4. Regras de pagamentos

| ID | Regra de negócio | Especificação |
|---|---|---|
| RN10 | Vínculo do pagamento | Cada pagamento deve estar associado a um membro existente. Não deve ser permitido gravar um pagamento sem membro ou com referência a um membro inexistente. |
| RN11 | Dados obrigatórios do pagamento | O pagamento deve possuir identificador único gerado automaticamente, membro, valor, data do pagamento e status. A data do pagamento é obrigatória e deve ser válida. |
| RN12 | Valor do pagamento | O valor deve ser maior que zero e possuir, no máximo, duas casas decimais. Valores ausentes, não numéricos, iguais a zero, negativos ou com mais de duas casas decimais devem ser rejeitados. |
| RN13 | Status do pagamento | Nesta entrega, o registro representa um pagamento já recebido. O sistema deve atribuir o status Pago e não aceitar outro status. Controle de pagamentos pendentes, vencidos ou cancelados não faz parte desta entrega. |

## 5. Regras de histórico de atividades

| ID | Regra de negócio | Especificação |
|---|---|---|
| RN14 | Vínculos da atividade | Cada registro de atividade deve estar associado a um membro existente e a um funcionário responsável existente. |
| RN15 | Dados obrigatórios da atividade | Cada registro deve possuir identificador único, membro, atividade, data, tempo de execução e funcionário responsável. O nome da atividade não pode ser vazio nem conter somente espaços. A data deve ser válida. |
| RN16 | Tempo de execução | O tempo de execução deve ser um número inteiro positivo, expresso em minutos. |
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
- Os pagamentos representarão valores já recebidos e serão registrados somente com o status Pago.
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
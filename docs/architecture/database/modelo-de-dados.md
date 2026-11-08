# Modelo de Dados — VIGOR

## 1. Objetivo

Este documento descreve o modelo de dados previsto para o VIGOR — Sistema de Cadastro de Academia, conforme as orientações específicas da A3 de Gestão e Qualidade de Software da UNISUL.

O modelo é preliminar e poderá ser ajustado durante a implementação, desde que as alterações sejam documentadas e mantenham os requisitos do projeto.

## 2. Entidades e atributos

### 2.1. Membros

| Atributo | Descrição |
|---|---|
| ID | Identificador único do membro, gerado automaticamente e preservado nas atualizações. |
| Nome | Nome completo do membro. |
| CPF | CPF do membro, sujeito à validação dos dígitos verificadores, rejeição de sequências repetidas, normalização de pontos e hífen e unicidade. |
| Telefone | Telefone de contato. |
| Endereço | Endereço do membro. |
| Data de cadastro | Data preenchida automaticamente no cadastro e preservada nas atualizações. |

### 2.2. Treinos

| Atributo | Descrição |
|---|---|
| ID | Identificador único do treino, gerado automaticamente. |
| ID_Membro | Referência ao membro associado. |
| Tipo | Tipo de treino. |
| Descrição | Descrição do treino. |
| Duração | Duração prevista do treino. |
| Data_Início | Data de início do treino. |

Conforme RN07–RN09, membro, tipo, descrição, duração e data de início são obrigatórios. Tipo e descrição não podem ser vazios nem conter somente espaços. A duração deve ser um número inteiro positivo em minutos, e a data de início deve ser válida.

### 2.3. Pagamentos

| Atributo | Descrição |
|---|---|
| ID | Identificador único do pagamento, gerado automaticamente. |
| ID_Membro | Referência ao membro associado. |
| Valor | Valor do pagamento. |
| Data_Pagamento | Data do pagamento. |
| Status | Situação do pagamento; nesta entrega, somente `Pago`. |

O módulo registra somente pagamentos efetivamente recebidos. Não contempla contas a receber, pagamentos pendentes ou um módulo financeiro.

Conforme RN10–RN13, membro, valor, data de pagamento e status são obrigatórios. A data deve ser válida; o valor deve ser maior que zero e possuir no máximo duas casas decimais. O sistema atribui `Pago` e não aceita outro status.

### 2.4. Histórico de Atividades

| Atributo | Descrição |
|---|---|
| ID | Identificador único do registro de atividade, gerado automaticamente. |
| ID_Membro | Referência ao membro associado. |
| Atividade | Descrição da atividade realizada. |
| Data | Data da atividade. |
| Tempo_Execução | Tempo de execução da atividade. |

Conforme RN14 e RN15, cada registro deve identificar o membro e o funcionário responsável existente; a associação com um funcionário responsável é obrigatória como regra de negócio. Sua representação física no modelo ainda está pendente de definição no diagrama de domínio.

Conforme RN15–RN16, ID, membro, atividade, data, tempo de execução e funcionário responsável são dados obrigatórios. A atividade não pode ser vazia nem conter somente espaços, a data deve ser válida e o tempo de execução deve ser um número inteiro positivo em minutos.

### 2.5. Funcionários

| Atributo | Descrição |
|---|---|
| ID | Identificador único do funcionário. |
| Nome | Nome completo do funcionário. |
| Cargo | Função exercida na academia. |
| Login | Identificação utilizada para acessar o sistema. |
| Senha | Credencial de acesso, que deverá ser armazenada por meio de hash apropriado. |

Os funcionários são previamente cadastrados e utilizados para autenticação. Não está prevista uma tela de cadastro ou gerenciamento de funcionários nesta entrega.

Conforme RN18–RN20, ID, nome, cargo, login e senha protegida por hash são necessários. Nome, cargo e login não podem ser vazios nem conter somente espaços; o login deve ser único. A autenticação requer funcionário existente e credenciais correspondentes.

## 3. Relacionamentos previstos

As relações abaixo são descrições conceituais do modelo atual. Tipos, constraints definitivas, chaves estrangeiras e cardinalidades físicas dependem da consolidação do modelo e do diagrama de classes do domínio.

- **Membros e Treinos:** um membro pode possuir vários treinos. Cada treino está associado a um membro.
- **Membros e Pagamentos:** um membro pode possuir vários pagamentos. Cada pagamento está associado a um membro.
- **Membros e Histórico de Atividades:** um membro pode possuir vários registros de atividades. Cada registro está associado a um membro.
- **Funcionários e atividades:** cada registro de atividade deve identificar um funcionário responsável existente, conforme RN14 e RN15. A multiplicidade no lado do funcionário e a representação técnica desse relacionamento ainda dependem da consolidação do diagrama de domínio.

## 4. Integridade dos dados

O modelo deverá respeitar as seguintes condições:

- Cada entidade deverá possuir um identificador único.
- Os identificadores de Membro, Treino, Pagamento e Histórico de Atividades devem ser gerados automaticamente conforme RN02, RN08, RN11 e RN15; a estratégia de geração no banco não está definida neste modelo.
- As referências entre entidades deverão corresponder a registros existentes.
- O CPF dos membros deverá conter 11 dígitos com dígitos verificadores válidos; sequências repetidas devem ser rejeitadas. Pontos e hífen devem ser desconsiderados na validação e comparação, e o CPF normalizado deve ser único.
- Os campos obrigatórios deverão ser validados antes da persistência.
- Nome, CPF, telefone e endereço são obrigatórios para Membro, conforme RN01.
- Os campos obrigatórios de Treino, Pagamento, Histórico de Atividades e Funcionário devem seguir RN08, RN11, RN15 e RN18, conforme detalhado nas respectivas seções.
- O login de Funcionário deve ser único, conforme RN19.
- Pagamentos devem ter valor maior que zero e no máximo duas casas decimais (RN12); duração de Treino e tempo de execução de atividade devem ser inteiros positivos expressos em minutos (RN09 e RN16).
- A exclusão de membros com treinos, pagamentos ou atividades vinculados deverá ser bloqueada; quando uma exclusão permitida ocorrer, os registros relacionados devem ser preservados. Não se presume exclusão em cascata.
- Dados inválidos ou referências a registros inexistentes não devem ser persistidos; uma operação rejeitada deve preservar dados válidos previamente armazenados (RN21).
- As senhas dos funcionários não deverão ser armazenadas em texto puro.

## 5. Banco de dados e persistência

O PostgreSQL está previsto como banco de dados do projeto, com persistência por meio do Spring Data JPA.

A estrutura definitiva deverá ser validada durante a implementação. Os tipos de dados, constraints definitivas, chaves estrangeiras e cardinalidades físicas dependem da consolidação do modelo e do diagrama de classes do domínio. As relações descritas nesta seção são conceituais e não afirmam que já estejam implementadas no banco.

## 6. Escopo funcional e implementação

As entidades **Membros**, **Treinos**, **Pagamentos**, **Histórico de Atividades** e **Funcionários** fazem parte do escopo funcional previsto para a entrega, conforme os requisitos RF01–RF09. A implementação ocorre por etapas; a prioridade inicial em membros não retira as demais entidades do escopo final.

No código consultado, somente a entidade `Membro` está implementada. As demais entidades e seus mapeamentos ainda pertencem ao modelo previsto, não à implementação atual.

O registro de pagamentos limita-se a pagamentos recebidos, com status `Pago`, conforme RN13. Funcionários previamente cadastrados são usados na autenticação, sem tela para cadastrá-los. O histórico de atividades será consultado a partir de dados previamente disponibilizados por procedimento técnico documentado; não haverá tela de registro nesta entrega.

## 7. Pontos pendentes

- Consolidar o diagrama de classes do domínio e o diagrama lógico do banco de dados.
- Definir os tipos de dados, constraints definitivas, chaves estrangeiras e cardinalidades físicas durante a consolidação do modelo.
- Definir como o funcionário responsável será representado no Histórico de Atividades, sem antecipar atributo ou mapeamento físico.
- Definir e registrar a estratégia de gerenciamento de alterações do schema, conforme pendente em [migrations.md](migrations.md).

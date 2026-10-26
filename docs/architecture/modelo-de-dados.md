# Modelo de Dados — VIGOR

## 1. Objetivo

Este documento descreve o modelo de dados previsto para o VIGOR — Sistema de Cadastro de Academia, conforme as orientações específicas da A3 de Gestão e Qualidade de Software da UNISUL.

O modelo é preliminar e poderá ser ajustado durante a implementação, desde que as alterações sejam documentadas e mantenham os requisitos do projeto.

## 2. Entidades e atributos

### 2.1. Membros

| Atributo | Descrição |
|---|---|
| ID | Identificador único do membro. |
| Nome | Nome completo do membro. |
| CPF | CPF do membro, sujeito a validação e unicidade. |
| Telefone | Telefone de contato. |
| Endereço | Endereço do membro. |
| Data de cadastro | Data em que o membro foi cadastrado. |

### 2.2. Treinos

| Atributo | Descrição |
|---|---|
| ID | Identificador único do treino. |
| ID_Membro | Referência ao membro associado. |
| Tipo | Tipo de treino. |
| Descrição | Descrição do treino. |
| Duração | Duração prevista do treino. |
| Data_Início | Data de início do treino. |

### 2.3. Pagamentos

| Atributo | Descrição |
|---|---|
| ID | Identificador único do pagamento. |
| ID_Membro | Referência ao membro associado. |
| Valor | Valor do pagamento. |
| Data_Pagamento | Data do pagamento. |
| Status | Situação do pagamento. |

### 2.4. Histórico de Atividades

| Atributo | Descrição |
|---|---|
| ID | Identificador único do registro de atividade. |
| ID_Membro | Referência ao membro associado. |
| Atividade | Descrição da atividade realizada. |
| Data | Data da atividade. |
| Tempo_Execução | Tempo de execução da atividade. |

### 2.5. Funcionários

| Atributo | Descrição |
|---|---|
| ID | Identificador único do funcionário. |
| Nome | Nome completo do funcionário. |
| Cargo | Função exercida na academia. |
| Login | Identificação utilizada para acessar o sistema. |
| Senha | Credencial de acesso, que deverá ser armazenada de forma protegida. |

## 3. Relacionamentos previstos

- **Membros e Treinos:** um membro pode possuir vários treinos. Cada treino está associado a um membro.
- **Membros e Pagamentos:** um membro pode possuir vários pagamentos. Cada pagamento está associado a um membro.
- **Membros e Histórico de Atividades:** um membro pode possuir vários registros de atividades. Cada registro está associado a um membro.
- **Funcionários e atividades:** os funcionários podem estar relacionados às atividades que executam ou registram. A forma exata desse relacionamento ainda precisa ser definida pela equipe.

## 4. Integridade dos dados

O modelo deverá respeitar as seguintes condições:

- Cada entidade deverá possuir um identificador único.
- As referências entre entidades deverão corresponder a registros existentes.
- O CPF dos membros deverá ser válido e único.
- Os campos obrigatórios deverão ser validados antes da persistência.
- As operações de atualização e exclusão deverão preservar a integridade dos relacionamentos.
- As senhas dos funcionários não deverão ser armazenadas em texto puro.

## 5. Banco de dados e persistência

O PostgreSQL está previsto como banco de dados do projeto, com persistência por meio do Spring Data JPA.

A estrutura definitiva deverá ser validada durante a implementação. Os atributos, tipos de dados, restrições e chaves estrangeiras serão definidos no mapeamento das entidades.

## 6. Escopo do MVP

A primeira etapa prioriza a entidade Membros, incluindo cadastro, validação de CPF, prevenção de duplicidade e consulta dos registros.

As demais entidades fazem parte do modelo previsto para o sistema e serão implementadas conforme a priorização e a divisão de responsabilidades da equipe.

## 7. Pontos pendentes

- Definir os tipos de dados e as restrições de cada atributo.
- Definir como os funcionários serão associados às atividades.
- Definir a política de exclusão de membros com registros relacionados.
- Definir os estados possíveis para pagamentos.
- Confirmar os atributos adicionais necessários durante a implementação.
- Elaborar o diagrama de classes e o diagrama lógico do banco de dados.

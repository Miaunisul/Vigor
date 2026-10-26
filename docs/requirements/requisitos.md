# Requisitos do Sistema VIGOR

## 1. Visão geral

O VIGOR é um sistema de cadastro e gestão de academia desenvolvido como projeto acadêmico da A3 de Gestão e Qualidade de Software da UNISUL.

O sistema tem como objetivo apoiar o gerenciamento de membros e suas informações, permitindo organizar cadastros, consultas e outras atividades relacionadas à rotina da academia.

## 2. Objetivo

Desenvolver uma aplicação web com persistência em banco de dados, validação das informações cadastradas e testes automatizados, seguindo os requisitos definidos para o projeto.

## 3. Usuários e atores

- **Funcionário da academia:** realiza operações de cadastro, consulta e atualização de informações conforme suas permissões.
- **Gestor da academia:** acompanha e administra as informações relacionadas aos membros e às atividades da academia.
- **Membro da academia:** pessoa cadastrada no sistema. O acesso direto do membro à aplicação ainda precisa ser definido.

## 4. Escopo funcional do sistema

O sistema proposto contempla:

- Cadastro e consulta de membros.
- Gerenciamento de planos de treino.
- Registro e gerenciamento de pagamentos.
- Consulta do histórico de atividades dos membros.
- Cadastro e acesso de funcionários.
- Atualização e exclusão de registros, respeitando as regras de negócio.

A implementação dessas funcionalidades será organizada em etapas, de acordo com o planejamento e a divisão de responsabilidades da equipe.

## 5. Requisitos funcionais

| ID | Requisito | Prioridade |
|---|---|---|
| RF01 | Cadastrar membros com as informações obrigatórias. | Alta |
| RF02 | Consultar e listar membros cadastrados. | Alta |
| RF03 | Validar o CPF informado durante o cadastro. | Alta |
| RF04 | Impedir o cadastro de dois membros com o mesmo CPF. | Alta |
| RF05 | Atualizar as informações dos membros. | Média |
| RF06 | Excluir registros de membros, respeitando as regras definidas. | Média |
| RF07 | Cadastrar e gerenciar planos de treino. | A definir |
| RF08 | Registrar e consultar pagamentos dos membros. | A definir |
| RF09 | Registrar e consultar o histórico de atividades dos membros. | A definir |
| RF10 | Permitir o acesso de funcionários por meio de login. | A definir |

**Observação:** as prioridades e a inclusão de cada funcionalidade no MVP devem ser confirmadas pela equipe. Os requisitos registrados não significam que as funcionalidades já estejam implementadas.

## 6. Requisitos não funcionais

| ID | Requisito | Descrição |
|---|---|---|
| RNF01 | Segurança | Proteger os dados armazenados e evitar exposição indevida de informações. |
| RNF02 | Desempenho | Permitir consultas e operações com tempo de resposta adequado ao uso previsto. |
| RNF03 | Usabilidade | Oferecer uma interface clara e fácil de utilizar. |
| RNF04 | Integridade | Manter a consistência dos dados e impedir registros inválidos. |
| RNF05 | Manutenibilidade | Organizar o código para facilitar correções e evolução do sistema. |
| RNF06 | Testabilidade | Permitir a execução de testes automatizados para verificar as regras e funcionalidades. |

## 7. Regras gerais de cadastro

- Os campos obrigatórios devem ser preenchidos.
- O CPF deve apresentar um formato válido e passar pela validação definida para o sistema.
- Não deve ser permitido cadastrar mais de um membro com o mesmo CPF.
- Registros inválidos não devem ser persistidos.
- Os dados cadastrados devem permanecer disponíveis para consulta após serem salvos no banco de dados.

## 8. Dados previstos para o sistema

### 8.1. Membros

- ID
- Nome
- CPF
- Telefone
- Endereço
- Data de cadastro

### 8.2. Treinos

- ID
- ID do membro
- Tipo
- Descrição
- Duração
- Data de início

### 8.3. Pagamentos

- ID
- ID do membro
- Valor
- Data do pagamento
- Status

### 8.4. Histórico de atividades

- ID
- ID do membro
- Atividade
- Data
- Tempo de execução

### 8.5. Funcionários

- ID
- Nome
- Cargo
- Login
- Senha

A estrutura definitiva do banco de dados, os relacionamentos e os campos adicionais serão detalhados no documento `docs/architecture/modelo-de-dados.md`.

## 9. Escopo inicial do MVP

A primeira etapa prioriza:

1. Cadastro de membros.
2. Validação dos campos obrigatórios e do CPF.
3. Impedimento de CPF duplicado.
4. Consulta e listagem de membros.
5. Persistência em banco de dados.
6. Testes automatizados das funcionalidades e regras implementadas.

As funcionalidades de treinos, pagamentos, histórico de atividades, login de funcionários, atualização e exclusão serão planejadas conforme a divisão de tarefas e a evolução do projeto. A equipe deverá confirmar quais delas entrarão no MVP.

## 10. Tecnologias previstas

- **Linguagem:** Java 21.
- **Framework:** Spring Boot.
- **Gerenciamento de dependências:** Maven.
- **Interface:** Thymeleaf, HTML, CSS e Bootstrap.
- **Banco de dados:** PostgreSQL.
- **Persistência:** Spring Data JPA.
- **Validação:** Bean Validation.
- **Testes:** JUnit 5, Mockito e Spring MockMvc, conforme a necessidade.
- **Cobertura:** JaCoCo.
- **Controle de versão:** Git e GitHub.

Essas tecnologias representam o planejamento técnico do projeto. A configuração e a implementação devem ser verificadas durante o desenvolvimento.

## 11. Critérios de qualidade e testes

O sistema deverá ser verificado por meio de testes unitários, testes de integração e testes de usabilidade.

Os testes devem contemplar, no mínimo, o cadastro válido, o preenchimento de campos obrigatórios, o CPF inválido, o CPF duplicado e a consulta de membros.

A meta de cobertura de código definida para o projeto é de 75%, conforme o planejamento da A3.

## 12. Itens pendentes de definição

- Confirmar as funcionalidades que entrarão no MVP.
- Definir os campos adicionais de cada entidade.
- Detalhar as permissões dos funcionários e gestores.
- Definir se os membros terão acesso direto ao sistema.
- Elaborar os diagramas UML e BPM previstos.
- Desenvolver a prototipação das telas.
- Confirmar a estrutura final do banco de dados.
- Definir a licença do repositório em conjunto com a equipe.
- Implementar e validar os requisitos por meio de testes.


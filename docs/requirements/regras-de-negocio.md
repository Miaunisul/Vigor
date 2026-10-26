# Regras de Negócio — VIGOR

## 1. Objetivo

Este documento define as regras de negócio do VIGOR — Sistema de Cadastro de Academia, desenvolvido para a A3 de Gestão e Qualidade de Software da UNISUL.

As regras orientam o desenvolvimento, a validação dos dados e a criação dos testes automatizados.

## 2. Regras de cadastro de membros

**RN01 — Campos obrigatórios**

O sistema deve exigir o preenchimento de todos os campos definidos como obrigatórios para o cadastro de um membro.

**RN02 — Validação do CPF**

O CPF informado deve passar pela validação adotada pelo sistema. CPFs inválidos devem ser rejeitados.

**RN03 — CPF único**

Não deve ser permitido cadastrar dois membros com o mesmo CPF.

**RN04 — Não persistência de dados inválidos**

Cadastros que não atendam às regras de validação não devem ser salvos no banco de dados.

**RN05 — Consulta de membros**

O sistema deve permitir consultar os membros cadastrados e apresentar as informações correspondentes aos registros existentes.

**RN06 — Integridade dos dados**

As operações realizadas devem preservar a consistência das informações armazenadas.

**RN07 — Atualização de membros**

Quando a atualização estiver disponível, o sistema deverá validar os novos dados antes de salvá-los, respeitando as regras de campos obrigatórios e CPF único.

**RN08 — Exclusão de membros**

Quando a exclusão estiver disponível, o sistema deverá tratar os vínculos com treinos, pagamentos e histórico de atividades de forma a preservar a integridade dos dados. A política definitiva de exclusão deverá ser definida pela equipe.

## 3. Regras de treinos

**RN09 — Vínculo com o membro**

Cada treino cadastrado deve estar associado a um membro existente.

**RN10 — Dados do treino**

O cadastro de treino deve respeitar os campos definidos no modelo de dados, incluindo tipo, descrição, duração e data de início, conforme a implementação acordada pela equipe.

**RN11 — Integridade dos treinos**

Um treino não deve ser associado a um membro inexistente.

## 4. Regras de pagamentos

**RN12 — Vínculo com o membro**

Cada pagamento registrado deve estar associado a um membro existente.

**RN13 — Valor do pagamento**

O valor informado deve atender às validações definidas para o campo, não sendo aceitos valores inválidos.

**RN14 — Status do pagamento**

O pagamento deve possuir um status válido, conforme os estados que forem definidos pela equipe.

## 5. Regras de histórico de atividades

**RN15 — Vínculo com o membro**

Cada registro de atividade deve estar associado a um membro existente.

**RN16 — Dados da atividade**

O registro deve conter as informações previstas no modelo de dados, incluindo atividade, data e tempo de execução, de acordo com as validações definidas.

## 6. Regras de funcionários

**RN17 — Dados obrigatórios**

O cadastro de funcionários deve exigir os campos definidos como obrigatórios para essa entidade.

**RN18 — Autenticação**

Caso o login de funcionários seja implementado, o acesso deverá validar as credenciais informadas.

**RN19 — Proteção das credenciais**

As senhas não devem ser armazenadas em texto puro. A implementação deverá adotar um mecanismo apropriado de proteção das credenciais.

## 7. Aplicação e validação das regras

As regras devem ser verificadas por meio de testes automatizados, conforme o escopo de cada funcionalidade implementada.

As regras de cadastro de membros, validação do CPF, unicidade do CPF e consulta de membros são prioritárias para o MVP inicial.

As regras referentes a treinos, pagamentos, histórico, funcionários, atualização e exclusão deverão ser implementadas e testadas conforme a priorização e a divisão de responsabilidades da equipe.

## 8. Pontos pendentes de definição

- Definir todos os campos obrigatórios de cada entidade.
- Definir os status possíveis para pagamentos.
- Definir os critérios de atualização e exclusão de membros e registros relacionados.
- Definir os detalhes da autenticação e das permissões dos funcionários.
- Confirmar quais regras adicionais entrarão no MVP.

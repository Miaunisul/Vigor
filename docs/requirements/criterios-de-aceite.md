# Critérios de Aceite — VIGOR

## 1. Objetivo

Este documento define os critérios que serão utilizados para verificar se as funcionalidades do VIGOR — Sistema de Cadastro de Academia atendem aos requisitos e às regras de negócio estabelecidos para o projeto.

Os critérios serão utilizados durante a implementação, os testes e a validação do sistema.

## 2. Cadastro de membros

### CA01 — Cadastro válido

**Dado** que o usuário preenche os campos obrigatórios com informações válidas e informa um CPF válido ainda não cadastrado,

**Quando** solicita o cadastro do membro,

**Então** o sistema deve salvar o registro no banco de dados e apresentar uma confirmação da operação.

### CA02 — Campos obrigatórios não preenchidos

**Dado** que o usuário deixa um ou mais campos obrigatórios sem preenchimento,

**Quando** solicita o cadastro,

**Então** o sistema deve informar os campos que precisam ser corrigidos e não deve salvar o registro inválido.

### CA03 — CPF inválido

**Dado** que o usuário informa um CPF inválido,

**Quando** solicita o cadastro,

**Então** o sistema deve rejeitar o cadastro e informar que o CPF precisa ser corrigido.

### CA04 — CPF duplicado

**Dado** que já existe um membro cadastrado com determinado CPF,

**Quando** o usuário tenta cadastrar outro membro com o mesmo CPF,

**Então** o sistema deve rejeitar o novo cadastro e preservar o registro existente.

## 3. Consulta de membros

### CA05 — Consulta de membros cadastrados

**Dado** que existem membros cadastrados no sistema,

**Quando** o usuário acessa a funcionalidade de consulta,

**Então** o sistema deve apresentar os registros existentes de acordo com os filtros e critérios de consulta implementados.

### CA06 — Consulta sem resultados

**Dado** que não existem membros correspondentes à consulta realizada,

**Quando** o usuário pesquisa por esses registros,

**Então** o sistema deve informar que nenhum resultado foi encontrado.

## 4. Atualização e exclusão de membros

### CA07 — Atualização válida

**Dado** que existe um membro cadastrado,

**Quando** o usuário atualiza suas informações com dados válidos,

**Então** o sistema deve salvar as alterações, respeitando a validação dos campos e a unicidade do CPF.

### CA08 — Exclusão de membro

**Dado** que existe um membro cadastrado,

**Quando** o usuário solicita sua exclusão,

**Então** o sistema deve aplicar a política de exclusão definida para o projeto e preservar a integridade dos registros relacionados.

## 5. Gerenciamento de treinos

### CA09 — Cadastro de treino

**Dado** que existe um membro cadastrado,

**Quando** o usuário registra um treino com os dados obrigatórios válidos,

**Então** o sistema deve associar o treino ao membro correspondente e salvar o registro.

### CA10 — Treino associado a membro inexistente

**Dado** que o identificador informado não corresponde a um membro cadastrado,

**Quando** o usuário tenta registrar um treino,

**Então** o sistema deve rejeitar a operação.

## 6. Registro de pagamentos

### CA11 — Registro de pagamento válido

**Dado** que existe um membro cadastrado e os dados do pagamento são válidos,

**Quando** o usuário registra o pagamento,

**Então** o sistema deve salvar o pagamento associado ao membro e registrar o status definido para a operação.

### CA12 — Dados de pagamento inválidos

**Dado** que o pagamento possui dados inválidos,

**Quando** o usuário tenta registrá-lo,

**Então** o sistema deve rejeitar a operação e informar os dados que precisam ser corrigidos.

## 7. Histórico de atividades

### CA13 — Registro de atividade

**Dado** que existe um membro cadastrado,

**Quando** o usuário registra uma atividade com os dados obrigatórios válidos,

**Então** o sistema deve salvar o registro associado ao membro.

### CA14 — Consulta de histórico

**Dado** que um membro possui atividades registradas,

**Quando** o usuário consulta seu histórico,

**Então** o sistema deve apresentar os registros correspondentes ao membro consultado.

## 8. Login de funcionários

### CA15 — Login válido

**Dado** que existe um funcionário com credenciais válidas,

**Quando** ele informa suas credenciais corretas,

**Então** o sistema deve permitir o acesso conforme as permissões definidas para o funcionário.

### CA16 — Login inválido

**Dado** que as credenciais informadas são inválidas,

**Quando** o funcionário tenta acessar o sistema,

**Então** o sistema deve negar o acesso e apresentar uma mensagem apropriada, sem expor informações sensíveis.

## 9. Validação e testes

Os critérios de aceite devem orientar a criação de testes unitários, de integração e de usabilidade, conforme aplicável a cada funcionalidade.

Os critérios CA01 a CA06 são prioritários para a primeira etapa do MVP. Os demais deverão ser validados conforme as funcionalidades forem implementadas.

## 10. Status dos critérios

Os critérios descritos representam os resultados esperados do sistema. Eles não significam que as funcionalidades já estejam implementadas ou aprovadas.

A equipe deverá registrar os resultados dos testes e as evidências correspondentes no documento `docs/testing/evidencias.md`.

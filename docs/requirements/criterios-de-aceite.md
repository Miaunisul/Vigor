# Critérios de Aceite — VIGOR

## 1. Objetivo

Este documento define os critérios utilizados para verificar se as funcionalidades do VIGOR — Sistema de Cadastro de Academia atendem aos requisitos e às regras de negócio estabelecidos para o projeto.

Os critérios serão utilizados durante a implementação, os testes e a validação do sistema.

Nos fluxos internos, o usuário é o funcionário autenticado. Os critérios de login e de acesso sem autenticação verificam as situações anteriores à autenticação.

## 2. Cadastro de membros

### CA01 — Cadastro válido

**Dado** que o funcionário preenche os campos obrigatórios com informações válidas e informa um CPF válido ainda não cadastrado,

**Quando** solicita o cadastro do membro,

**Então** o sistema deve salvar o registro no banco de dados e apresentar uma confirmação da operação.

### CA02 — Campos obrigatórios não preenchidos

**Dado** que o funcionário deixa um ou mais campos obrigatórios ausentes, vazios ou compostos apenas por espaços,

**Quando** solicita o cadastro,

**Então** o sistema deve informar os campos que precisam ser corrigidos e não deve salvar o registro inválido.

### CA03 — CPF inválido

**Dado** que o funcionário informa um CPF inválido,

**Quando** solicita o cadastro,

**Então** o sistema deve rejeitar o cadastro, não salvar o registro e informar que o CPF precisa ser corrigido.

### CA04 — CPF duplicado

**Dado** que já existe um membro cadastrado com determinado CPF,

**Quando** o funcionário tenta cadastrar outro membro com o mesmo CPF,

**Então** o sistema deve rejeitar o novo cadastro e preservar o registro existente.

## 3. Consulta de membros

### CA05 — Consulta de membros cadastrados

**Dado** que existem membros cadastrados no sistema,

**Quando** o funcionário acessa a listagem de membros,

**Então** o sistema deve apresentar os registros existentes com suas informações cadastrais.

### CA06 — Consulta sem resultados

**Dado** que não existem membros cadastrados no sistema,

**Quando** o funcionário acessa a listagem de membros,

**Então** o sistema deve informar que não existem membros cadastrados.

## 4. Atualização e exclusão de membros

### CA07 — Atualização válida

**Dado** que existe um membro cadastrado,

**Quando** o funcionário atualiza suas informações com dados válidos,

**Então** o sistema deve salvar as alterações, respeitando a validação dos campos e a unicidade do CPF.

### CA08 — Exclusão de membro sem vínculos

**Dado** que existe um membro sem treinos, pagamentos ou atividades vinculados,

**Quando** o funcionário solicita sua exclusão,

**Então** o sistema deve excluir o membro e apresentar a confirmação da operação.

## 5. Gerenciamento de treinos

### CA09 — Cadastro de treino

**Dado** que existe um membro cadastrado,

**Quando** o funcionário registra um treino com os dados obrigatórios válidos,

**Então** o sistema deve associar o treino ao membro correspondente e salvar o registro com um identificador único gerado automaticamente.

### CA10 — Treino associado a membro inexistente

**Dado** que o identificador informado não corresponde a um membro cadastrado,

**Quando** é solicitada a gravação de um treino associado a esse identificador,

**Então** o sistema deve rejeitar a operação e não persistir o treino.

## 6. Registro de pagamentos

### CA11 — Registro de pagamento válido

**Dado** que existe um membro cadastrado e foram informados um valor maior que zero, com no máximo duas casas decimais, e uma data de pagamento válida,

**Quando** o funcionário registra o pagamento,

**Então** o sistema deve salvar o pagamento associado ao membro, gerar um identificador único, atribuir o status Pago e apresentar a confirmação da operação.

### CA12 — Dados de pagamento inválidos

**Dado** que o pagamento possui dados inválidos,

**Quando** é solicitada sua gravação,

**Então** o sistema deve rejeitar a operação, não persistir o pagamento e informar os dados que precisam ser corrigidos.

## 7. Histórico de atividades

### CA13 — Registro de atividade — Futuro

Este critério está fora do escopo desta entrega e não integra seus critérios de conclusão.

O registro de atividades por meio de uma tela poderá ser considerado em uma versão futura. Nesta entrega, o histórico será disponibilizado por carga técnica documentada e acessado pela funcionalidade de consulta.

O identificador CA13 foi preservado para manter o histórico documental.

### CA14 — Consulta de histórico

**Dado** que um membro possui atividades registradas,

**Quando** o funcionário consulta seu histórico,

**Então** o sistema deve apresentar somente os registros correspondentes ao membro consultado, incluindo atividade, data e tempo de execução.

## 8. Login de funcionários

### CA15 — Login válido

**Dado** que existe um funcionário previamente cadastrado,

**Quando** ele informa seu login e sua senha corretamente,

**Então** o sistema deve permitir o acesso às funcionalidades internas.

### CA16 — Login inválido

**Dado** que o login informado não existe ou a senha está incorreta,

**Quando** o usuário tenta acessar o sistema,

**Então** o sistema deve negar o acesso e apresentar uma mensagem apropriada, sem expor informações sensíveis.

## 9. Verificações complementares da entrega

### CA17 — Atualização inválida

**Dado** que existe um membro cadastrado,

**Quando** o funcionário tenta atualizar seus dados com campos obrigatórios ausentes, vazios ou compostos apenas por espaços, CPF inválido ou CPF pertencente a outro membro,

**Então** o sistema deve rejeitar a alteração, informar o problema e preservar os dados anteriormente armazenados.

### CA18 — Manutenção do CPF do próprio membro

**Dado** que existe um membro cadastrado,

**Quando** o funcionário atualiza seus dados mantendo o CPF do próprio registro e informa os demais campos corretamente,

**Então** o sistema deve permitir a atualização sem apontar duplicidade de CPF.

### CA19 — Exclusão bloqueada por vínculos

**Dado** que existe um membro com pelo menos um treino, pagamento ou registro de atividade vinculado,

**Quando** o funcionário solicita sua exclusão,

**Então** o sistema deve bloquear a operação, explicar o motivo e preservar o membro e seus registros vinculados.

### CA20 — Consulta de treinos

**Dado** que um membro está selecionado para consulta,

**Quando** o funcionário acessa seus treinos,

**Então** o sistema deve apresentar somente os treinos associados a esse membro ou informar que não existem treinos cadastrados para ele.

### CA21 — Dados de treino inválidos

**Dado** que o cadastro de um treino possui campos obrigatórios ausentes, tipo ou descrição vazios ou compostos apenas por espaços, duração que não seja um número inteiro positivo ou data de início inválida,

**Quando** é solicitada a gravação,

**Então** o sistema deve rejeitar a operação, informar os dados que precisam ser corrigidos e não salvar o treino.

### CA22 — Histórico sem atividades

**Dado** que um membro não possui atividades registradas,

**Quando** o funcionário consulta seu histórico,

**Então** o sistema deve informar a ausência de atividades para esse membro.

### CA23 — Acesso sem autenticação

**Dado** que o usuário não está autenticado,

**Quando** tenta acessar diretamente uma funcionalidade interna,

**Então** o sistema deve impedir o acesso e solicitar autenticação.

### CA24 — Identificação e data de cadastro

**Dado** que os dados de um novo membro são válidos,

**Quando** o cadastro é concluído,

**Então** o sistema deve gerar um identificador único e preencher automaticamente a data de cadastro.

**E** esses dados devem permanecer inalterados nas atualizações posteriores.

### CA25 — Persistência dos dados

**Dado** que um cadastro ou uma atualização válida foi concluída,

**Quando** a aplicação é reiniciada e o funcionário realiza uma nova consulta,

**Então** os dados salvos devem continuar disponíveis.

### CA26 — Normalização e validação do CPF

**Dado** que o funcionário informa um CPF com ou sem pontos e hífen,

**Quando** o sistema valida o CPF,

**Então** deve considerar os mesmos dígitos para validar e verificar duplicidade, rejeitando sequências repetidas e dígitos verificadores incorretos.

**E** um CPF válido e ainda não cadastrado deve ser aceito em ambas as formas, desde que os demais dados estejam válidos.

### CA27 — Pagamento associado a membro inexistente

**Dado** que a referência informada não corresponde a um membro existente,

**Quando** é solicitada a gravação de um pagamento,

**Então** o sistema deve rejeitar a operação e não persistir o pagamento.

## 10. Validação e testes

Os critérios de aceite devem orientar os testes unitários, de integração, de validação, de sistema e de usabilidade, conforme aplicável.

Todos os critérios deste documento integram a entrega atual, exceto o CA13, identificado como futuro.

Cada situação inválida descrita em um critério deve ser verificada separadamente, para identificar qual comportamento está sendo avaliado.

Os testes de pagamento inválido devem contemplar valor ausente, não numérico, zero, negativo ou com mais de duas casas decimais, data ausente ou inválida e tentativa de gravação com status diferente de Pago.

A preparação dos dados de atividades deve respeitar as regras RN14 a RN16. A preparação dos dados de funcionários deve respeitar as regras RN18 e RN19.

Os requisitos não funcionais também devem ser verificados:

- RNF01: verificar a restrição de acesso e o armazenamento de senhas por hash apropriado, sem expor credenciais nas evidências.
- RNF02: registrar ambiente, volume de dados, tempos de consulta e ocorrência de travamentos ou erros de tempo limite.
- RNF03: realizar avaliação de usabilidade e registrar dificuldades e correções necessárias.
- RNF04: verificar persistência, rejeição de dados inválidos e integridade dos relacionamentos.
- RNF05: revisar a organização do código e registrar a análise estática e o resultado do quality gate.
- RNF06: executar testes com Maven, gerar relatório de cobertura mínima de 75% com JaCoCo e verificar a execução na integração contínua.

## 11. Status dos critérios

Os critérios representam resultados esperados. Sua documentação não significa que as funcionalidades já estejam implementadas ou aprovadas.

Os testes devem permanecer identificados como planejados até sua execução. Depois disso, a equipe deve registrar o resultado efetivamente observado.

As evidências devem ser registradas em `docs/testing/evidencias.md`, com referências aos testes ou às verificações correspondentes.
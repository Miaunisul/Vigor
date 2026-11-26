# Critérios de Aceite — VIGOR

## 1. Objetivo

Este documento define os critérios utilizados para verificar se as funcionalidades do VIGOR — Sistema de Gestão de Academia atendem aos requisitos e às regras de negócio estabelecidos para o projeto.

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

**Dado** que, após a normalização da RN03, o CPF não contém exatamente 11 dígitos numéricos, possui todos os dígitos iguais ou apresenta algum dígito verificador incorreto,

**Quando** o funcionário solicita o cadastro,

**Então** o sistema deve rejeitar o cadastro, informar que o CPF precisa ser corrigido e não salvar o membro.

### CA04 — CPF duplicado

**Dado** que já existe um membro cadastrado com determinado CPF,

**Quando** o funcionário tenta cadastrar outro membro com o mesmo CPF normalizado, ainda que use uma formatação diferente,

**Então** o sistema deve informar que o CPF já está cadastrado, não criar um segundo membro e preservar o registro existente.

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

**Dado** que existe um membro cadastrado e foram informados tipo e descrição preenchidos, duração inteira maior que zero em minutos e data de início válida,

**Quando** o funcionário solicita o cadastro do treino,

**Então** o sistema deve remover os espaços das extremidades de tipo e descrição, validar os dados e salvar o treino associado ao membro selecionado, com identificador único gerado automaticamente.

### CA10 — Treino sem membro ou associado a membro inexistente

**Dado** que a referência ao membro está ausente ou não corresponde a um membro cadastrado,

**Quando** é solicitada a gravação de um treino,

**Então** o sistema deve rejeitar a operação, informar o problema e não persistir o treino.

## 6. Registro de pagamentos

### CA11 — Registro de pagamento válido

**Dado** que existe um membro cadastrado e foram informados valor maior que zero, com no máximo 10 dígitos na parte inteira e duas casas decimais, e data de pagamento válida,

**Quando** o funcionário solicita o registro do pagamento recebido, sem precisar informar o status,

**Então** a aplicação deve atribuir automaticamente o status `PAGO` antes da persistência, salvar o pagamento associado ao membro, gerar um identificador único e apresentar a confirmação da operação.

A ausência do campo status na solicitação do funcionário não deve causar rejeição do pagamento.

### CA12 — Dados de pagamento inválidos

**Dado** que o pagamento apresenta referência de membro ausente ou inexistente, valor ausente, não numérico, igual a zero, negativo, com mais de 10 dígitos inteiros ou mais de duas casas decimais, ou data ausente ou inválida,

**Quando** é solicitada sua gravação,

**Então** o sistema deve rejeitar a operação, informar qual informação precisa ser corrigida e não persistir o pagamento.

O status não é um campo de preenchimento do funcionário. A aplicação deve atribuir `PAGO` antes da persistência, conforme CA11. Um valor de status enviado pelo cliente não deve substituir essa atribuição automática.

Na validação interna do registro, um status diferente de `PAGO` deve impedir a persistência. Essa verificação não deve ser confundida com a ausência do campo status na solicitação do funcionário, que é permitida.

## 7. Histórico de atividades

### CA13 — Registro de atividade — Futuro

O registro de atividades por meio de uma tela não integra o escopo final desta entrega da A3. O identificador CA13 foi preservado para manter o histórico documental.

Nesta entrega, os dados do histórico serão disponibilizados por carga técnica documentada, conforme as regras RN14 a RN16.

Essa exclusão se refere somente ao registro pela interface. A consulta do histórico permanece obrigatória na entrega final, conforme RF09, CA14 e CA22.

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

**Dado** que um membro existente está selecionado para consulta,

**Quando** o funcionário acessa seus treinos,

**Então** o sistema deve apresentar somente os treinos associados a esse membro, com tipo, descrição, duração em minutos e data de início correspondentes aos dados armazenados.

**E** quando esse membro não possuir treinos, deve informar a ausência de registros.

### CA21 — Dados de treino inválidos

**Dado** que o cadastro de um treino possui campos obrigatórios ausentes, tipo ou descrição vazios ou compostos apenas por espaços, duração que não seja um número inteiro maior que zero ou data de início ausente ou inválida,

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

**Dado** que o funcionário informa `52998224725` ou `529.982.247-25`, inclusive com espaços nas extremidades,

**Quando** o sistema valida o CPF e verifica sua unicidade no cadastro,

**Então** deve usar `52998224725` como valor normalizado nas duas verificações. Em cadastros independentes, com os demais dados válidos e o CPF ainda não utilizado, ambas as formas devem ser aceitas e gravadas sem pontuação.

**E** se esse CPF já estiver cadastrado, a outra forma de apresentação deve ser rejeitada como duplicada, conforme o CA04. A normalização não remove letras nem espaços internos; entradas que permaneçam inválidas devem ser rejeitadas conforme o CA03.

### CA27 — Pagamento associado a membro inexistente

**Dado** que a referência informada não corresponde a um membro existente,

**Quando** é solicitada a gravação de um pagamento,

**Então** o sistema deve rejeitar a operação e não persistir o pagamento.

### CA28 — Registro técnico válido de histórico

**Dado** que existem um membro e um funcionário responsável cadastrados, e os dados da atividade possuem nome preenchido, data válida e tempo de execução inteiro maior que zero em minutos,

**Quando** o registro é incluído pelo procedimento técnico de carga,

**Então** deve ser persistido com identificador único gerado automaticamente e com os vínculos corretos ao membro e ao funcionário responsável.

**E** deve ficar disponível para consulta no histórico do membro correspondente.

Este critério verifica a preparação dos dados do histórico e não exige uma tela de cadastro de atividades.

### CA29 — Rejeição de dados inválidos do histórico

**Dado** que os dados destinados ao histórico possuem referência de membro ou funcionário ausente ou inexistente, atividade ausente, vazia ou composta apenas por espaços, data ausente ou inválida, ou tempo de execução ausente, não inteiro, igual a zero ou negativo,

**Quando** é tentada sua inclusão pelo procedimento técnico de carga,

**Então** a inclusão inválida deve ser rejeitada, sem persistir o registro e sem alterar os registros válidos anteriormente armazenados.

Cada condição inválida deve ser verificada separadamente, mantendo os demais dados válidos.

### CA30 — Consulta com referência de membro inválida

**Dado** que nenhum membro foi selecionado, que a referência informada tem formato inválido ou não corresponde a um membro cadastrado,

**Quando** é solicitada uma consulta de treinos ou histórico por esse membro,

**Então** o sistema deve informar se falta selecionar um membro, se a referência é inválida ou se o membro não existe, sem apresentar registros de outros membros.

Essa situação deve ser diferenciada da consulta de um membro existente que não possui registros.

### CA31 — Listagem geral de pagamentos

**Dado** que existem pagamentos registrados,

**Quando** o funcionário acessa a listagem sem selecionar um membro,

**Então** o sistema deve apresentar os pagamentos com membro, valor em reais, data do pagamento e status apresentado como Pago.

**E** os registros devem estar ordenados por data do pagamento decrescente e, em caso de empate, por identificador decrescente.

### CA32 — Consulta de pagamentos por membro

**Dado** que existem pagamentos associados a membros distintos,

**Quando** o funcionário seleciona um membro existente e solicita a consulta,

**Então** o sistema deve apresentar somente os pagamentos desse membro, com os dados correspondentes aos registros armazenados, preservando a seleção realizada.

**E** ao limpar a seleção, deve voltar à listagem geral.

### CA33 — Consulta de pagamentos sem resultados

**Dado** que não existem pagamentos registrados, ou que o membro existente selecionado não possui pagamentos,

**Quando** o funcionário solicita a consulta correspondente,

**Então** o sistema deve informar a ausência de pagamentos no conjunto consultado.

**E** quando houver um membro selecionado, deve preservar essa seleção.

Os cenários de listagem geral vazia e de membro existente sem pagamentos devem ser verificados separadamente.

### CA34 — Consulta de pagamentos com referência inválida

**Dado** que foi informada uma referência de membro não numérica, fora dos limites do identificador, igual a zero, negativa ou correspondente a um membro inexistente,

**Quando** é solicitada a consulta de pagamentos,

**Então** o sistema deve informar o problema e não apresentar pagamentos de outros membros.

A ausência de seleção no formulário de consulta é permitida e corresponde à listagem geral, conforme CA31. Ela não deve ser tratada como referência inválida.

## 10. Validação e testes

Os critérios de aceite devem orientar os testes unitários, de integração, de validação, de sistema e de usabilidade, conforme aplicável.

Todos os critérios deste documento integram a entrega final da A3, exceto o CA13, identificado como futuro. A execução das verificações acompanhará as etapas de implementação.

Os critérios CA17 a CA34 descrevem comportamentos esperados. Os identificadores CT01 a CT22 estão definidos em `docs/testing/plano-de-testes.md`. A existência de um caso de teste não comprova sua execução integral. Resultados e limitações devem ser associados às evidências registradas em `docs/testing/evidencias.md`.

O CA25 exige verificar a permanência dos dados após reiniciar a aplicação. Um teste que apenas salva e consulta dados sem reiniciar a aplicação não comprova, por si só, o atendimento integral desse critério.

Cada situação inválida descrita em um critério deve ser verificada separadamente, para identificar qual comportamento está sendo avaliado.

Os testes de pagamento inválido devem contemplar valor ausente, não numérico, zero, negativo, com mais de 10 dígitos inteiros ou mais de duas casas decimais, data ausente ou inválida e referência de membro ausente ou inexistente.

Os testes do fluxo de registro devem verificar que uma solicitação válida sem o campo status é aceita e que a aplicação atribui `PAGO` antes da persistência. Um status enviado pelo cliente não deve substituir o valor definido pela aplicação. A rejeição de status diferente de `PAGO` deve ser verificada na camada interna responsável pela validação do registro.

A preparação dos dados de atividades deve respeitar as regras RN14 a RN16. A preparação dos dados de funcionários deve respeitar as regras RN18 e RN19.

Os critérios CA28 a CA30 foram explicitados na revisão D04. O CA30 está associado ao CT22 e à matriz de rastreabilidade. Os cenários de preparação técnica dos CA28 e CA29 permanecem pendentes no trabalho L03; não se presume que CT13 contemple integralmente esses cenários.

Entradas não numéricas, datas inválidas e valores fracionários em campos inteiros devem ser verificadas na camada que recebe e converte esses dados. Os serviços utilizam tipos Java como `BigDecimal`, `LocalDate` e `Integer`; a existência desses tipos não comprova o tratamento de erros na interface.

Na implementação da D05, o `PagamentoController` atribui `PAGO` antes de chamar o serviço. O formulário recebe membro, valor e data; o status não integra os campos vinculados à entrada do usuário. O `PagamentoService` continua validando o status do registro recebido. Os testes do controller verificam a atribuição automática e que um status enviado pelo cliente não substitui o valor definido pela aplicação. Essas verificações não comprovam, por si só, autenticação ou proteção das rotas.

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

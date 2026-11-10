# Relacionamentos do Banco de Dados — VIGOR

## 1. Objetivo

Este documento registra os relacionamentos previstos entre as entidades do VIGOR, suas regras de participação e as restrições de integridade aplicáveis. Distingue referências conceituais descritas nos requisitos de chaves estrangeiras efetivamente definidas no modelo físico ou mapeadas no código.

## 2. Visão geral

O modelo de dados prevê as entidades **Membro**, **Treino**, **Pagamento**, **Histórico de Atividades** e **Funcionário**. Os documentos de requisitos e regras de negócio vinculam treinos, pagamentos e atividades a um membro existente. Os requisitos também exigem que cada registro de atividade tenha um funcionário responsável existente. Entretanto, o modelo de dados atual ainda não apresenta o atributo correspondente em Histórico de Atividades. Portanto, esse relacionamento é uma exigência dos requisitos, mas ainda não está completamente especificado no modelo.

As referências a `ID_Membro` aparecem nos atributos de Treino, Pagamento e Histórico de Atividades do [modelo de dados](modelo-de-dados.md). Os documentos não especificam nomes físicos de constraints nem todos os detalhes das chaves estrangeiras. No código atual existe somente a entidade `Membro`; ela não declara associações JPA. Não foram encontrados mapeamentos de entidades dependentes nem chaves estrangeiras implementadas pela aplicação.

## 3. Detalhamento

### 3.1 Membro e Treino

- **Entidade de origem:** Treino; entidade relacionada: Membro.
- **Cardinalidade documentada:** um membro pode possuir vários treinos; cada treino está associado a um membro. Assim, um membro pode não ter treinos ou ter vários, e cada treino requer um membro.
- **Referência:** `Treino.ID_Membro` aponta conceitualmente para `Membro.ID`. O modelo ainda não define a constraint ou o mapeamento físico da chave estrangeira.
- **Obrigatoriedade:** vínculo obrigatório para cada Treino, conforme RN07 e RN08; a existência de treinos para um Membro é opcional.
- **Regras relacionadas:** RN07–RN09; RF06–RF07. A gravação deve rejeitar membro ausente ou inexistente (CA10) e a consulta deve limitar os resultados ao membro selecionado (CA20).
- **Implementação:** não há entidade, repositório ou associação de Treino no código consultado.

### 3.2 Membro e Pagamento

- **Entidade de origem:** Pagamento; entidade relacionada: Membro.
- **Cardinalidade documentada:** o modelo descreve que um membro pode possuir vários pagamentos e cada pagamento está associado a um membro. Logo, o membro pode não ter pagamentos ou ter vários; cada pagamento requer um membro.
- **Referência:** `Pagamento.ID_Membro` aponta conceitualmente para `Membro.ID`. A constraint física não está definida no modelo.
- **Obrigatoriedade:** vínculo obrigatório para cada Pagamento, conforme RN10–RN11; a existência de pagamentos para um Membro é opcional.
- **Regras relacionadas:** RN10–RN13 e RF08. O pagamento deve referenciar membro existente; a gravação de referência inválida deve ser rejeitada (CA27). Nesta entrega, o registro representa pagamento recebido e recebe status Pago.
- **Implementação:** não há entidade, repositório ou associação de Pagamento no código consultado.

### 3.3 Membro e Histórico de Atividades

- **Entidade de origem:** registro de Histórico de Atividades; entidade relacionada: Membro.
- **Cardinalidade documentada:** o modelo descreve que um membro pode possuir vários registros de atividades e cada registro está associado a um membro. Portanto, o membro pode não ter atividades ou ter várias; cada registro requer um membro.
- **Referência:** `Histórico de Atividades.ID_Membro` aponta conceitualmente para `Membro.ID`. A constraint física não está definida no modelo.
- **Obrigatoriedade:** vínculo obrigatório para cada atividade, conforme RN14–RN15; a existência de atividades para um Membro é opcional.
- **Regras relacionadas:** RN14–RN17 e RF09. A consulta deve retornar somente atividades do membro selecionado e informar quando não houver registros (CA14 e CA22). Os registros iniciais serão disponibilizados por carga técnica documentada; não haverá tela de cadastro nesta entrega (CA13).
- **Implementação:** não há entidade, repositório ou associação de atividade no código consultado.

### 3.4 Funcionário e Histórico de Atividades

- **Entidade de origem:** registro de Histórico de Atividades; entidade relacionada: Funcionário responsável.
- **Cardinalidade documentada:** RN14 e RN15 exigem que cada registro esteja associado a um funcionário responsável existente, isto é, cada atividade requer um funcionário. O modelo de dados declara que funcionários podem estar relacionados a atividades que executam ou registram, mas afirma que a forma exata do relacionamento ainda precisa ser definida; não determina quantas atividades cada funcionário pode ter associadas.
- **Referência:** os requisitos preveem uma referência ao funcionário responsável, mas o atributo não aparece na lista de atributos de Histórico de Atividades do modelo. Nome, coluna e constraint da chave estrangeira não estão definidos.
- **Obrigatoriedade:** funcionário responsável obrigatório por atividade segundo RN14–RN15; a multiplicidade de atividades por funcionário permanece sem definição explícita no modelo.
- **Regras relacionadas:** RN14–RN16. A carga técnica inicial deve respeitar os vínculos e a existência do funcionário.
- **Implementação:** não há entidades de Funcionário ou Atividade nem mapeamento dessa relação no código consultado.

## 4. Integridade referencial

As referências de Treino, Pagamento e Atividade devem apontar para registros existentes de Membro; a referência de Atividade a Funcionário também deve apontar para funcionário existente. Inserções sem esses vínculos ou com referências inexistentes devem ser rejeitadas, conforme RN07, RN10, RN14 e RN21. O requisito RNF04 exige que relacionamentos inválidos não sejam persistidos.

A exclusão de um Membro deve ser permitida somente quando não houver treinos, pagamentos ou atividades vinculados. Havendo qualquer um desses registros, a operação deve ser bloqueada, o motivo informado e todos os dados preservados (RN06, RF05, CA08 e CA19). Essa regra é uma exigência de comportamento; os documentos não definem se será aplicada por consultas e validação na aplicação, por política de chave estrangeira no banco ou pela combinação desses mecanismos. Portanto, não se presume cascade nem uma ação específica de `ON DELETE`.

O modelo prevê que referências correspondam a registros existentes e que atualizações e exclusões preservem a integridade, mas informa que tipos, restrições e chaves estrangeiras serão definidos no mapeamento das entidades. No código consultado, `Membro` tem chave primária gerada e CPF único, mas não há outras entidades nem relações mapeadas. Não há evidência de que as relações descritas já existam no banco PostgreSQL.

## 5. Pendências

- Definir e implementar as chaves estrangeiras e constraints para `Treino.ID_Membro`, `Pagamento.ID_Membro` e `Histórico de Atividades.ID_Membro`.
- Confirmar o atributo de referência a Funcionário no modelo de Histórico de Atividades, seu nome físico e sua chave estrangeira, em consistência com os requisitos e regras RN14–RN15.
- Definir a multiplicidade do relacionamento Funcionário–Atividade no modelo, além da obrigatoriedade já estabelecida para cada atividade.
- Definir como o banco e a aplicação aplicarão a regra de exclusão bloqueada por vínculos, sem exclusão em cascata que remova os registros protegidos.
- Implementar as entidades, associações e repositórios ainda ausentes e validar a integridade referencial no PostgreSQL.
- Atualizar o [modelo de dados](modelo-de-dados.md) quando os detalhes do schema e do relacionamento Funcionário–Atividade forem confirmados.

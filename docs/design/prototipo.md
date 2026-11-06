# Protótipo de Interface — VIGOR

## 1. Objetivo

Documentar e disponibilizar os protótipos de interface do VIGOR, relacionando as telas da entrega final da A3 aos requisitos funcionais e diferenciando as propostas de evolução futura.

O protótipo principal orienta a implementação visual da aplicação. A versão estendida é uma referência complementar com funcionalidades adicionais, sem compromisso de implementação na entrega final da A3.

A existência de telas e estados no Figma não significa que as funcionalidades estejam implementadas ou testadas.

## 2. Acesso aos protótipos

### 2.1. Protótipo principal — referência da entrega

- [Abrir o protótipo principal no Figma](https://www.figma.com/design/dq77qXaA3a3tJIsMqRcGBm/VIGOR?node-id=2-31671&t=H2iKpjY8cxHdWkj5-1).
- Nome do arquivo: VIGOR.
- Finalidade: orientar a implementação dos requisitos RF01 a RF09 na versão desktop.

O protótipo principal contém telas e estados estáticos, além de uma versão navegável com interações para demonstrar os fluxos da interface.

A navegação do protótipo simula o uso da aplicação e não comprova a implementação do backend, da persistência ou das validações reais.

- [Abrir o protótipo navegável no Figma](https://www.figma.com/proto/dq77qXaA3a3tJIsMqRcGBm/VIGOR?node-id=2-31671&t=hY5R31RGG4Oi8oCr-1&scaling=min-zoom&content-scaling=fixed&page-id=0%3A1&starting-point-node-id=2%3A31671).

### 2.2. Versão estendida — referência complementar

- [Abrir a versão estendida no Figma](https://www.figma.com/design/BHnpvrVjH5vEswS6MgZNDu/VIGOR-extra?node-id=0-1&t=HIocQdgfXdCjZjjU-1).
- Nome do arquivo: VIGOR-extra.
- Localização das telas adicionais: página “Telas desktop” do arquivo de origem.
- O link disponibilizado abre a apresentação das telas estáticas.

A versão estendida preserva propostas adicionais de interface e funcionalidades para possíveis evoluções do produto. Ela não substitui o protótipo principal nem amplia o escopo da entrega final da A3.

Os acréscimos estão identificados na seção 10 deste documento. Sua presença no Figma não significa que estejam implementados, testados ou aprovados para desenvolvimento.

### 2.3. Fonte editável e permissões

O Figma é a fonte editável dos protótipos. Eventuais capturas ou exportações em PDF são materiais complementares.

A edição dos arquivos depende das permissões concedidas à equipe. 

Os dados apresentados nas telas são ilustrativos e não representam registros reais da aplicação.

## 3. Organização do protótipo principal

Para localizar o material, abra o arquivo “VIGOR” e selecione a página correspondente na barra lateral do Figma.

| Página | Conteúdo e finalidade |
|---|---|
| Estilos e componentes | Padrões visuais e componentes reutilizáveis da interface. |
| Telas desktop | Telas principais de login, listagem, cadastro e edição de membros, consulta e cadastro de treinos, registro de pagamentos e consulta do histórico de atividades. |
| Estados e feedbacks | Variações das telas com carregamento, credenciais inválidas, campos obrigatórios, CPF inválido ou duplicado, outros erros de validação, mensagens de sucesso, listas vazias, confirmação de exclusão e bloqueio por registros vinculados. |
| Adaptações mobile | Material fora da referência de implementação desta entrega, que será voltada à versão desktop. |
| Fluxos do protótipo | Mapa estático dos caminhos administrativos e dos comportamentos esperados. |

Para implementar uma funcionalidade, consulte sua tela em **“Telas desktop”** e as variações correspondentes em **“Estados e feedbacks”**. Essas páginas se complementam e devem ser consultadas em conjunto.

A equipe de UI deve utilizar essas representações para construir as telas e suas mensagens. A equipe de backend deve seguir os requisitos, as regras de negócio e os critérios de aceite para implementar as validações e os resultados apresentados pela interface.

O mapa de fluxos e a versão navegável auxiliam na compreensão da sequência de uso. As interações do protótipo não substituem as regras documentadas nem comprovam sua implementação.

## 4. Telas e requisitos funcionais

As telas principais estão localizadas na página “Telas desktop” do arquivo “VIGOR”. Os estados complementares estão na página “Estados e feedbacks”.

As denominações abaixo identificam o conteúdo das telas para facilitar sua localização.

| Requisito | Tela ou representação | Descrição |
|---|---|---|
| RF01 — Autenticar funcionários | Login de funcionários | Apresenta os campos Login e Senha e o botão Entrar. Os estados complementares representam o envio das credenciais e a rejeição de credenciais inválidas. |
| RF02 — Cadastrar membros | Novo membro | Apresenta nome completo, CPF, telefone e endereço como campos obrigatórios. O identificador e a data de cadastro serão gerados automaticamente pela aplicação. |
| RF03 — Consultar membros | Membros | Apresenta a listagem com nome, CPF, telefone, endereço e data de cadastro. Inclui uma representação da ausência de membros cadastrados. |
| RF04 — Atualizar membros | Editar membro | Apresenta os dados existentes para alteração de nome, CPF, telefone e endereço. O identificador e a data de cadastro devem ser preservados. |
| RF05 — Excluir membros | Ação Excluir na listagem de membros e estados de exclusão | Apresenta confirmação para excluir um membro sem vínculos e bloqueio quando existem treinos, pagamentos ou atividades associados. |
| RF06 — Cadastrar treinos | Novo treino | Apresenta membro, tipo de treino, descrição, duração em minutos e data de início. Inclui representações de duração inválida e cadastro realizado. |
| RF07 — Consultar treinos | Treinos | Apresenta a seleção de um membro e seus treinos, com tipo, descrição, duração e data de início. Inclui representação de lista vazia. |
| RF08 — Registrar pagamentos | Pagamentos e Registrar pagamento | Apresenta acesso ao formulário com membro, valor, data do pagamento e status Pago somente para leitura. Inclui representações de dados inválidos e registro salvo. |
| RF09 — Consultar histórico de atividades | Histórico de atividades | Apresenta a seleção de um membro e suas atividades, com data, tempo de execução e funcionário responsável. Inclui representação de histórico vazio. |

Todos os requisitos RF01 a RF09 fazem parte da entrega final da A3. A implementação em etapas define a ordem de desenvolvimento e não retira funcionalidades do escopo final.

## 5. Estados e feedbacks

As representações abaixo podem ser localizadas na página “Estados e feedbacks” do protótipo principal, pelo módulo e pelo estado indicado.

| Módulo | Estado ou mensagem representada | Referências |
|---|---|---|
| Login | Entrando | RF01 e RNF03 |
| Login | Credenciais inválidas | RF01, RN20 e CA16 |
| Membros | Campos obrigatórios não preenchidos | RF02, RN01, RN21 e CA02 |
| Membros | CPF inválido | RF02, RN03 e CA03 |
| Membros | CPF já cadastrado | RF02, RN04 e CA04 |
| Membros | Cadastro realizado | RF02 e CA01 |
| Membros | Nenhum membro cadastrado | RF03 e CA06 |
| Membros | Atualização realizada com sucesso | RF04 e CA07 |
| Membros | Confirmar exclusão | RF05, RN06 e CA08 |
| Membros | Exclusão bloqueada por registros vinculados | RF05, RN06 e CA19 |
| Treinos | Duração inválida | RF06, RN09, RN21 e CA21 |
| Treinos | Cadastro realizado | RF06 e CA09 |
| Treinos | Lista vazia | RF07 e CA20 |
| Pagamentos | Valor e data inválidos | RF08, RN11, RN12, RN21 e CA12 |
| Pagamentos | Registro salvo | RF08 e CA11 |
| Atividades | Histórico vazio | RF09, RN17 e CA22 |

As mensagens utilizam texto e elementos visuais para comunicar o resultado esperado das operações, apoiando o RNF03 — Usabilidade.

O estado de confirmação de exclusão representa a etapa anterior à operação. Ele não comprova, isoladamente, que a exclusão foi executada.

As representações não substituem a implementação das validações nem os testes definidos para a aplicação. Não há correspondência obrigatória de uma tela para cada cenário de teste.

## 6. Fluxos representados

A página “Fluxos do protótipo” do arquivo principal contém o “Mapa de fluxos”, com a documentação estática dos seguintes caminhos:

| Fluxo | Caminho representado | Requisitos |
|---|---|---|
| Acesso de funcionários | Login → Entrar → Membros. | RF01 |
| Cadastro de membro | Membros → Novo membro → Salvar membro → Sucesso e lista atualizada. | RF02 e RF03 |
| Edição de membro | Membros → Editar membro → Salvar membro → Lista atualizada. | RF03 e RF04 |
| Exclusão de membro | Membros → Excluir → Confirmação → Sucesso ou bloqueio. | RF05 |
| Registro e consulta de treinos | Selecionar membro → Registrar treino → Salvar treino → Consultar treinos. | RF06 e RF07 |
| Registro de pagamento | Pagamentos → Registrar pagamento → Salvar registro → Pagamento registrado. | RF08 |
| Consulta do histórico de atividades | Atividades → Selecionar membro → Consultar histórico → Apresentação somente para leitura. | RF09 |
| Navegação pela barra lateral | Acesso direto aos módulos Membros, Treinos, Pagamentos e Atividades. | RF02 a RF09 |

O “Mapa de fluxos” é uma representação estática dos caminhos esperados. Suas setas documentam a sequência das operações. A navegação interativa está disponível na versão navegável do protótipo principal.

A sequência apresentada para os treinos exemplifica um cadastro seguido de consulta. A consulta também deve estar disponível independentemente de um novo cadastro.

Na implementação, a atualização de membros deve preservar o identificador e a data de cadastro. Alterações inválidas devem ser rejeitadas sem modificar os dados anteriormente salvos, conforme RN02 e RN05.

## 7. Delimitação da implementação

O protótipo principal é a referência visual da versão desktop prevista para a entrega final.

As decisões documentadas na D01 orientam sua implementação e estão sujeitas à confirmação da equipe na revisão correspondente:

- Funcionários serão previamente cadastrados por procedimento técnico documentado, sem tela de cadastro ou gerenciamento.
- Atualização e exclusão são operações previstas para membros.
- A exclusão de membros com treinos, pagamentos ou atividades vinculados deve ser bloqueada.
- Para treinos, estão previstos cadastro e consulta.
- Pagamentos representam valores já recebidos, com status Pago atribuído pelo sistema.
- O histórico de atividades será alimentado por carga técnica documentada e disponibilizado para consulta.
- Não está prevista uma tela de registro, atualização ou exclusão de atividades.
- Não está prevista adaptação mobile nesta entrega.

Funcionalidades adicionais, como controle de pagamentos pendentes, edição e exclusão de treinos, cadastro de atividades pela interface, dashboards e integrações financeiras, não fazem parte da implementação documentada para esta entrega.

As funcionalidades fora do escopo final estão detalhadas na seção 7 do documento de [requisitos](../requirements/requisitos.md). Sua eventual implementação futura depende de revisão e aprovação do escopo.


## 8. Relação com os documentos do projeto

O protótipo principal deve permanecer consistente com:

- [Requisitos funcionais e não funcionais](../requirements/requisitos.md).
- [Regras de negócio](../requirements/regras-de-negocio.md).
- [Critérios de aceite](../requirements/criterios-de-aceite.md).
- [Matriz de rastreabilidade](../planning/matriz-de-rastreabilidade.md).

Os documentos de requisitos e regras de negócio definem os comportamentos e as validações da aplicação. Textos ilustrativos e controles apresentados no Figma não estabelecem, isoladamente, novas regras de negócio.

As opções exibidas no campo “Tipo de treino”, por exemplo, não definem uma lista obrigatória de tipos permitidos sem que essa restrição seja aprovada e documentada pela equipe.

Se houver alteração de escopo ou divergência entre o protótipo principal e esses documentos, a equipe deverá revisar os materiais relacionados antes da implementação.

## 9. Verificação e situação do material

As telas e os estados descritos neste documento foram conferidos visualmente por meio do protótipo no Figma, considerando os requisitos e as regras revisados na D01.

Essa conferência não constitui teste de usabilidade com participantes, execução de testes automatizados ou comprovação de funcionamento da aplicação.



## 10. Versão estendida e possibilidades futuras

O arquivo “VIGOR-extra” reúne funcionalidades já previstas na entrega e propostas adicionais. As telas descritas abaixo estão localizadas na página “Telas desktop”.

A classificação como evolução futura se aplica aos acréscimos identificados, não a todas as funcionalidades presentes nessa versão.

| Tela | Conteúdo apresentado | Diferença em relação à entrega da A3 |
|---|---|---|
| Detalhes do membro | Informações pessoais, treinos vinculados, pagamentos e atividades reunidos em uma única tela. | A apresentação consolidada e a consulta financeira são propostas adicionais. As consultas previstas nos RF03, RF07 e RF09 permanecem no escopo da entrega, conforme o protótipo principal. |
| Detalhes do treino | Informações do treino e ações de edição e exclusão. | A consulta pertence ao RF07. A edição e a exclusão de treinos são possibilidades futuras; nesta entrega, essas operações ficam restritas aos membros. |
| Pagamentos | Listagem de pagamentos, busca, filtro por status, paginação e apresentação de registros pagos e pendentes. | A listagem financeira, a busca, o filtro, a paginação e o controle de pendências não fazem parte da entrega atual. |
| Registrar pagamento | Formulário de pagamento com seleção de status. | O registro de pagamento pertence ao RF08. A seleção de outros status é uma proposta futura; na entrega atual, o sistema atribui somente Pago, conforme RN13. |
| Atividades | Consulta de atividades com busca, filtros por período, paginação e acesso ao registro de atividade. | A consulta por membro pertence ao RF09. Busca textual, filtros por período, paginação e cadastro pela interface são propostas adicionais. |
| Registrar atividade | Formulário com membro, atividade, data, tempo de execução e funcionário responsável. | O registro de atividades pela interface está fora da entrega atual. O histórico será alimentado por carga técnica documentada e disponibilizado para consulta. |

A implementação dessas propostas depende de aprovação da equipe e de atualização dos requisitos, regras de negócio, critérios de aceite e testes correspondentes.

Em caso de divergência entre as versões, a referência para a entrega da A3 é o protótipo principal, em conjunto com os documentos revisados na D01.

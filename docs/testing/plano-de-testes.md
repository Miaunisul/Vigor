# Plano de Testes — VIGOR

## 1. Objetivo

Definir a estratégia de testes do VIGOR — Sistema de Gestão de Academia, para verificar se as funcionalidades implementadas atendem aos requisitos funcionais, às regras de negócio e aos requisitos de qualidade estabelecidos para a A3 de Gestão e Qualidade de Software da UNISUL.

## 2. Escopo dos testes

Os testes contemplarão as funcionalidades RF01 a RF10 previstas para a entrega. A existência da implementação não significa aprovação do caso. Cada cenário permanece planejado ou parcialmente verificado até que sua execução e seus resultados sejam associados a evidências específicas. O CA13, registro de atividades por interface, está fora do escopo; os dados do histórico serão previamente disponibilizados por procedimento técnico documentado.

### 2.1. Cadastro de membros

- Cadastro com dados válidos.
- Validação de campos obrigatórios.
- Rejeição de CPF inválido.
- Rejeição de CPF duplicado.
- Verificação de persistência dos registros válidos.

### 2.2. Consulta de membros

- Listagem de membros cadastrados.
- Consulta sem resultados.
- Verificação da consistência das informações apresentadas.

### 2.3. Atualização e exclusão

- Atualização de dados válidos.
- Rejeição de alterações inválidas.
- Verificação da integridade dos registros relacionados durante a exclusão.

### 2.4. Treinos

- Cadastro de treinos válidos e associação a membro existente.
- Consulta de treinos, conferindo tipo, descrição, duração, data de início e membro associado (CT10).
- Rejeição de associação a membro inexistente.

### 2.5. Pagamentos

- Registro de pagamentos recebidos, com atribuição automática de `PAGO` pela aplicação.
- Validação de membro, valor e data.
- Verificação de que o status enviado pelo cliente não substitui o valor definido pela aplicação.
- Rejeição de status inválido na validação interna do registro.
- Persistência e leitura dos dados no PostgreSQL.
- Listagem geral ordenada por data decrescente e identificador decrescente no desempate.
- Consulta por membro, sem apresentar pagamentos de outros membros.
- Mensagens para conjuntos sem pagamentos e referências inválidas.
- Persistência dos pagamentos após reinício da aplicação.

### 2.6. Histórico de atividades

- Consulta do histórico de atividades.
- Preparação técnica de dados de atividades válidos e vinculados a membro e funcionário existentes.

### 2.7. Funcionários

- Preparação técnica de funcionários previamente cadastrados, com login único e senha protegida por hash.
- Teste de login com credenciais válidas.
- Rejeição de credenciais inválidas.
- Verificação do armazenamento da senha por hash apropriado.

### 2.8. Requisitos não funcionais

- Verificação da restrição de acesso e do armazenamento de senhas por hash (RNF01).
- Registro do ambiente, volume de dados e tempos observados nas consultas, sem presumir limite numérico (RNF02).
- Avaliação de usabilidade dos fluxos principais (RNF03).
- Verificação de persistência, validações e integridade dos relacionamentos (RNF04).
- Revisão da separação de responsabilidades e registro da análise estática e do quality gate adotado (RNF05).
- Verificação da execução Maven e na integração contínua e do relatório JaCoCo, incluindo a meta planejada de 75% (RNF06).

## 3. Casos de teste funcionais

Os casos descrevem procedimentos e resultados esperados. A situação de cada caso deve distinguir execução integral, verificação parcial e planejamento. Testes automatizados de componentes e verificações manuais podem cobrir partes diferentes de um mesmo caso. Uma execução global bem-sucedida não aprova automaticamente todos os cenários. Os dados devem ser fictícios, e cada condição inválida deve ser verificada separadamente.

### CT01 — Cadastro válido e dados automáticos

- **Status:** Planejado.
- **RF/CA:** RF02; CA01, CA24.
- **Pré-condições:** Funcionário autenticado; CPF válido ainda não cadastrado; dados obrigatórios disponíveis.
- **Passos:** Abrir o cadastro, informar nome, CPF, telefone e endereço válidos, solicitar o cadastro e consultar o membro criado.
- **Resultado esperado:** O membro é salvo e confirmado, recebe ID único e data de cadastro automaticamente, e os dados são apresentados na consulta.

### CT02 — Campos obrigatórios do membro

- **Status:** Planejado.
- **RF/CA:** RF02; CA02.
- **Pré-condições:** Funcionário autenticado; dados válidos, exceto o campo sob avaliação.
- **Passos:** Repetir o cadastro deixando, em execuções separadas, cada campo (nome, CPF, telefone e endereço) ausente, vazio e composto apenas por espaços.
- **Resultado esperado:** Cada tentativa informa o dado a corrigir e não persiste o membro.

### CT03 — CPF inválido

- **Status:** Planejado.
- **RF/CA:** RF02; CA03, CA26.
- **Pré-condições:** Funcionário autenticado; demais dados do membro válidos.
- **Passos:** Com os demais dados válidos, solicitar um cadastro separado para cada entrada inválida da seção 9.1. Conferir a indicação de erro no CPF e verificar que nenhum membro foi criado em cada tentativa.
- **Resultado esperado:** O sistema rejeita cada CPF inválido, informa que o campo precisa ser corrigido e não grava o membro.

### CT04 — CPF duplicado e normalizado

- **Status:** Planejado.
- **RF/CA:** RF02; CA04, CA26.
- **Pré-condições:** Funcionário autenticado; existe membro cadastrado com CPF válido.
- **Passos:** 1. Com `52998224725` já cadastrado, tentar cadastrar outro membro com `52998224725` e, separadamente, com `529.982.247-25`. Conferir os registros após cada tentativa. 2. Para os cenários positivos da seção 9.1, usar bases de teste independentes nas quais esse CPF ainda não exista. Cadastrar uma vez com a forma sem pontuação e, na outra base, com a forma pontuada. Conferir o CPF gravado.
- **Resultado esperado:** As tentativas duplicadas informam que o CPF já está cadastrado, não criam outro membro e preservam o registro original. Em cada cenário positivo, o cadastro é aceito e o valor gravado é `52998224725`.

### CT05 — Consulta de membros cadastrados

- **Status:** Planejado.
- **RF/CA:** RF03; CA05.
- **Pré-condições:** Funcionário autenticado; existem membros cadastrados com dados conhecidos.
- **Passos:** Acessar a listagem e conferir os registros retornados.
- **Resultado esperado:** A listagem apresenta os registros existentes com suas informações cadastrais correspondentes.

### CT06 — Consulta de membros sem resultados

- **Status:** Planejado.
- **RF/CA:** RF03; CA06.
- **Pré-condições:** Funcionário autenticado; não existem membros cadastrados no conjunto consultado.
- **Passos:** Acessar a listagem de membros.
- **Resultado esperado:** O sistema informa que não existem membros cadastrados.

### CT07 — Atualização válida, CPF próprio e rejeição de dados inválidos

- **Status:** Planejado.
- **RF/CA:** RF04; CA07, CA17, CA18, CA24, CA26. **Regras:** RN01, RN02, RN03, RN04 e RN05.
- **Pré-condições:** Funcionário autenticado; existem pelo menos dois membros cadastrados, cada um com CPF válido diferente. O membro-alvo e o segundo membro são identificados para os cenários; há também um CPF válido ainda não utilizado por nenhum cadastro.
- **Passos:** 1. Atualizar o membro-alvo com dados válidos e um novo CPF válido e inédito, ainda não utilizado por qualquer outro membro; consultar o registro. 2. Em execução separada, manter o CPF próprio do membro-alvo e atualizar os demais dados válidos. 3. Em execuções separadas, repetir para atualização as variações de campo ausente, vazio e composto apenas por espaços descritas no CT02, para cada campo obrigatório (nome, CPF, telefone e endereço). 4. Em execuções separadas, repetir para atualização os casos de CPF inválido do CT03, incluindo quantidade incorreta de dígitos, dígitos verificadores incorretos e sequência de dígitos iguais; considerar CPF com e sem pontuação. 5. Tentar atualizar o membro-alvo com o CPF válido pertencente ao segundo membro, incluindo representação com pontuação diferente para verificar a comparação normalizada. Consultar o membro-alvo após cada tentativa.
- **Resultado esperado:** A atualização com CPF válido inédito é aceita; manter o CPF próprio também é permitido, sem falsa duplicidade. Campos obrigatórios inválidos e CPFs inválidos, inclusive com dígitos verificadores incorretos, são rejeitados. O CPF de outro membro é rejeitado mesmo quando informado com formatação diferente. Cada tentativa rejeitada informa o problema e preserva os dados anteriores. ID e data de cadastro permanecem inalterados.

### CT08 — Exclusão de membro sem vínculos e bloqueio por vínculos

- **Status:** Planejado.
- **RF/CA:** RF05; CA08, CA19.
- **Pré-condições:** Funcionário autenticado; existe um membro sem vínculos e membros de teste com treino, pagamento ou atividade vinculados (um tipo de vínculo por cenário).
- **Passos:** 1. Excluir o membro sem vínculos e consultar a listagem. 2. Para cada tipo de vínculo, solicitar separadamente a exclusão do membro correspondente e consultar o membro e seus registros.
- **Resultado esperado:** O membro sem vínculos é excluído e a operação é confirmada. Cada exclusão com vínculo é bloqueada, o motivo é informado e o membro e os registros vinculados são preservados.

### CT09 — Cadastro de treino válido e rejeição de dados inválidos

- **Status:** Planejado.
- **RF/CA:** RF06; CA09, CA10, CA21.
- **Pré-condições:** Funcionário autenticado; existe membro válido; também há referência que não corresponde a membro cadastrado para o cenário inválido.
- **Passos:** Cadastrar treino válido. Em execuções separadas, tentar gravar sem cada dado obrigatório preenchido pelo usuário (membro, tipo, descrição, duração e data de início); o ID não é informado pelo usuário, pois é gerado automaticamente. Testar também tipo/descrição vazios ou só com espaços, duração ausente, não inteira, zero ou negativa, data ausente/inválida e membro inexistente. Nos casos em que a validação da interface prevê bloquear o envio, verificar esse bloqueio; para os demais casos, exercitar a rejeição na validação do servidor pelo mecanismo de teste disponível.
- **Resultado esperado:** O treino válido é associado ao membro e recebe ID único automaticamente. Entradas inválidas são bloqueadas pela interface quando esse comportamento estiver previsto; em todos os casos, devem ser rejeitadas e não persistidas, com indicação dos dados que precisam ser corrigidos.

### CT10 — Consulta de treinos por membro

- **Status:** Planejado.
- **RF/CA:** RF07; CA20.
- **Pré-condições:** Funcionário autenticado; existe um membro com um treino previamente cadastrado (por CT09 ou preparação de dados), e os valores armazenados de tipo, descrição, duração em minutos, data de início e membro associado são conhecidos para comparação. Para verificar a consulta sem resultados, existe também outro membro sem treinos.
- **Passos:** Consultar os treinos do membro com o treino conhecido e comparar cada detalhe apresentado com os valores armazenados: tipo, descrição, duração, data de início e membro associado. Em execução separada, consultar o membro sem treinos.
- **Resultado esperado:** A consulta apresenta somente os treinos associados ao membro consultado, e tipo, descrição, duração, data de início e membro de cada treino apresentado correspondem aos valores efetivamente armazenados. Para o membro sem treinos, o sistema informa que não há treinos cadastrados.

### CT11 — Registro de pagamento válido

- **Status:** Parcialmente verificado; referências às verificações locais em EV21–EV24. Execução integrada com autenticação ainda não comprovada.
- **RF/CA:** RF08; CA11.
- **Regras:** RN10, RN11, RN12 e RN13.
- **Pré-condições:** Membro existente; valor positivo com no máximo 10 dígitos inteiros e duas casas decimais; data válida. Para a verificação completa pela interface, funcionário autenticado.
- **Passos:** Registrar o pagamento informando membro, valor e data, sem enviar status. Conferir a confirmação e consultar o registro armazenado, comparando membro, valor, data, identificador e status. Em teste separado do controller, enviar um status diferente e verificar que ele não substitui a atribuição automática.
- **Resultado esperado:** O pagamento recebe ID único e status `PAGO`, mantém os dados válidos informados e a operação é confirmada. A ausência do status na solicitação é aceita. Um status enviado pelo cliente não altera o valor definido pela aplicação.
- **Limite:** A leitura após gravação não comprova persistência após reinício; essa verificação pertence ao CT21.

### CT12 — Rejeição de pagamento inválido ou sem membro existente

- **Status:** Parcialmente verificado; testes de controller, serviço e PostgreSQL relacionados em EV23. A execução integral de todas as variantes pela interface não está declarada.
- **RF/CA:** RF08; CA12, CA27.
- **Regras:** RN10, RN11, RN12, RN13 e RN21.
- **Pré-condições:** Demais dados válidos para cada tentativa; membro existente para os cenários de valor e data.
- **Passos:** Em execuções separadas, tentar registrar sem membro, com membro inexistente, valor ausente, não numérico, zero, negativo, com mais de 10 dígitos inteiros ou mais de duas casas decimais e data ausente ou inválida. Exercitar a validação do servidor mesmo quando o navegador impedir o envio. Separadamente, testar diretamente a validação interna de um registro com status nulo, vazio ou diferente de `PAGO`.
- **Resultado esperado:** Cada entrada inválida é rejeitada sem persistência. No fluxo web, os erros identificam os dados a corrigir e preservam os dados válidos do formulário. Na validação interna, status inválido impede a persistência.
- **Distinção:** Não enviar status no formulário é permitido, pois a aplicação o preenche. Enviar status pelo cliente não deve substituir `PAGO`, conforme CT11.

### CT13 — Consulta do histórico com atividades

- **Status:** Planejado.
- **RF/CA:** RF09; CA14. **Regras:** RN14, RN15, RN16 e RN17.
- **Pré-condições:** Funcionário autenticado; dados de atividades e cadastros de membros e funcionários disponibilizados por procedimentos técnicos documentados; atividades associadas a membros distintos.
- **Passos:** 1. Conferir cada registro de atividade disponibilizado: verificar identificador presente e único; presença do membro, atividade, data, tempo de execução e funcionário responsável; nome da atividade não vazio nem composto apenas por espaços; data válida; tempo de execução inteiro positivo em minutos. 2. Conferir que as referências de cada atividade correspondem a membro e funcionário existentes nos dados disponibilizados, sem pressupor nome físico de atributo ou coluna. 3. Consultar o histórico de cada membro selecionado e comparar os resultados com os registros armazenados, incluindo atividade, data e tempo de execução.
- **Resultado esperado:** Cada atividade possui identificador único e todos os dados obrigatórios; atividade e data respeitam as regras; o tempo é inteiro positivo em minutos; cada referência corresponde a membro e funcionário existentes. A consulta apresenta somente as atividades do membro selecionado e seus dados apresentados correspondem aos valores armazenados.

### CT14 — Consulta do histórico sem atividades

- **Status:** Planejado.
- **RF/CA:** RF09; CA22.
- **Pré-condições:** Funcionário autenticado; existe membro sem atividades carregadas.
- **Passos:** Consultar o histórico desse membro.
- **Resultado esperado:** O sistema informa a ausência de atividades para o membro consultado.

### CT15 — Login válido e proteção por hash da senha

- **Status:** Planejado como cenário completo; depende de provisionamento técnico documentado e verificação do fluxo integrado de autenticação.
- **RF/CA:** RF01; CA15. **Regras:** RN18, RN19 e RN20. **RNF:** RNF01.
- **Pré-condições:** Ambiente de teste isolado; disponíveis valores fictícios para preparar um funcionário e uma senha de teste. O mecanismo de codificação e verificação de senhas deve estar implementado para executar as verificações de hash.
- **Passos:** 1. Preparar tecnicamente um funcionário de teste com identificador, nome, cargo, login e uma senha fictícia, sem criar uma tela de cadastro. 2. Conferir nos funcionários disponibilizados que os identificadores são únicos e que nome, cargo e login estão preenchidos e não contêm somente espaços; comparar a quantidade de funcionários com a quantidade de logins distintos após normalização equivalente a `lower(btrim(login))`, conforme o índice único do banco. 3. Em ambiente controlado, consultar o valor armazenado no campo de senha do funcionário de teste, sem registrar o valor nem credenciais nas evidências. 4. Verificar que o valor armazenado não é igual à senha original e que seu formato corresponde a um hash produzido pelo mecanismo de codificação de senhas adotado pelo projeto. 5. Usar o mecanismo de verificação correspondente para confirmar que a senha original corresponde ao valor armazenado; não tentar reverter ou decodificar o hash. 6. Autenticar com a senha original e acessar uma funcionalidade interna.
- **Resultado esperado:** Dados obrigatórios do funcionário estão presentes, os identificadores e logins são únicos, o valor armazenado difere da senha original e corresponde ao formato e à verificação do mecanismo de hash adotado. A autenticação com a senha original permite o acesso interno. Nenhuma senha ou valor armazenado é exposto nas evidências.
- **Situação da implementação:** A revisão D04 identificou entidade e repositório de funcionários, configuração de `BCryptPasswordEncoder` e autenticação no serviço por `matches`. Isso não comprova provisionamento técnico documentado, sessão autenticada, tela de login ou proteção das rotas. A execução completa deste CT permanece dependente dessas verificações.

### CT16 — Login inválido e acesso sem autenticação

- **Status:** Planejado.
- **RF/CA:** RF01; CA16, CA23. **RNF:** RNF01.
- **Pré-condições:** Funcionário de teste previamente disponibilizado; sessão não autenticada para o acesso direto.
- **Passos:** 1. Tentar login com usuário inexistente. 2. Em outra tentativa, usar login existente com senha incorreta. 3. Sem possuir sessão autenticada, tentar acessar diretamente uma rota ou funcionalidade interna protegida.
- **Resultado esperado:** As tentativas de login são negadas com mensagem apropriada, sem expor informação sensível. O acesso direto é negado, a funcionalidade protegida não pode ser visualizada nem utilizada e o sistema solicita autenticação.

### CT17 — Persistência de membros após reinício da aplicação

- **Status:** Planejado.
- **RF/CA:** RF02, RF04; CA25. **RNF:** RNF04.
- **Pré-condições:** PostgreSQL de testes configurado; funcionário autenticado; dados fictícios disponíveis.
- **Passos:** Cadastrar um membro com dados válidos e registrar os valores apresentados, incluindo ID e data de cadastro. Atualizar validamente esse membro e registrar os valores atualizados. Encerrar e reiniciar efetivamente a aplicação mantendo o banco; autenticar novamente, consultar o membro e comparar ID, data de cadastro, nome, CPF, telefone e endereço com os valores registrados antes do reinício.
- **Resultado esperado:** Os dados do membro criado e atualizado, incluindo ID e data de cadastro, permanecem disponíveis e correspondem aos valores anteriores ao reinício. Este CT verifica persistência após reinício somente para cadastro e atualização de membros; não verifica persistência após reinício de treinos, pagamentos, atividades ou funcionários.

### CT18 — Listagem geral e ordenação de pagamentos

- **Status:** Parcialmente verificado; testes automatizados relacionados em EV23 e consulta manual registrada em EV24. O fluxo com autenticação não está comprovado por esses testes.
- **RF/CA:** RF10; CA31.
- **Pré-condições:** Pagamentos de membros distintos, com datas diferentes e pelo menos dois registros com a mesma data.
- **Passos:** Abrir a listagem sem selecionar membro. Comparar membro, valor, data e status com os registros armazenados. Conferir a ordem por data decrescente e identificador decrescente no desempate.
- **Resultado esperado:** Todos os pagamentos do conjunto de teste são apresentados com seus dados corretos e na ordem definida. Valores são apresentados em reais, datas em DD/MM/AAAA e status como Pago.

### CT19 — Consulta de pagamentos por membro e conjuntos vazios

- **Status:** Parcialmente verificado; referências em EV23 e EV24. A ação de limpar e a execução integral com autenticação devem ter resultado específico registrado.
- **RF/CA:** RF10; CA32, CA33.
- **Pré-condições:** Dois membros com pagamentos e um terceiro sem pagamentos. Para o cenário geral vazio, usar um conjunto de teste isolado sem pagamentos.
- **Passos:** Consultar separadamente os dois membros com pagamentos e comparar os resultados. Consultar o membro sem pagamentos. Limpar a seleção e conferir o retorno à listagem geral. Em cenário independente, abrir a listagem geral sem pagamentos.
- **Resultado esperado:** A consulta apresenta somente pagamentos do membro selecionado e preserva a seleção. Os cenários vazios apresentam mensagem correspondente. Limpar a seleção restaura a listagem geral.

### CT20 — Referência inválida na consulta de pagamentos

- **Status:** Parcialmente verificado pelos testes do controller relacionados em EV23; execução completa com autenticação pendente.
- **RF/CA:** RF10; CA34.
- **Pré-condições:** Existirem pagamentos no ambiente para verificar que não são apresentados indevidamente.
- **Passos:** Consultar com referência não numérica, zero, negativa, acima do limite de `Long` e com identificador inexistente, em tentativas separadas. Como controle, consultar sem informar membro.
- **Resultado esperado:** Referências inválidas produzem mensagem de erro sem apresentar pagamentos. A consulta sem membro retorna a listagem geral.

### CT21 — Persistência de pagamento após reinício

- **Status:** Verificação manual parcial registrada em EV24: permanência do pagamento após reinício confirmada. A comparação detalhada de todos os campos e o fluxo com autenticação devem possuir registro específico para aprovação integral.
- **RF/CA:** RF08, RF10; CA25.
- **RNF:** RNF04.
- **Pré-condições:** PostgreSQL configurado; pagamento válido registrado; identificação e dados do registro anotados.
- **Passos:** Registrar membro, ID do pagamento, valor, data e status. Encerrar e reiniciar efetivamente a aplicação, mantendo o banco. Autenticar, consultar os pagamentos e comparar os dados com os valores anteriores ao reinício.
- **Resultado esperado:** O mesmo pagamento permanece disponível, com ID, vínculo, valor, data e status preservados.
- **Limite:** Este caso não verifica persistência após reinício das demais entidades.

## 4. Verificações planejadas de requisitos não funcionais

As verificações abaixo complementam os CT funcionais, sem receber identificadores CT na matriz. A situação individual distingue planejamento e verificação parcial. Seus resultados deverão ser registrados em `docs/testing/evidencias.md`.

| ID | Requisito | Procedimento e resultado esperado | Status |
|---|---|---|---|
| VN01 | RNF01 | Usar CT15 e CT16 para verificar autenticação, bloqueio de acesso e senha armazenada por hash apropriado; não expor credenciais nas evidências. | Planejado |
| VN02 | RNF02 | Registrar ambiente, volume de dados, tempos observados nas consultas e ocorrência de travamentos/timeouts, sem presumir limite numérico. | Planejado |
| VN03 | RNF03 | Avaliar fluxos principais quanto a campos identificados, obrigatoriedade, clareza das mensagens e navegação; registrar dificuldades. | Planejado |
| VN04 | RNF04 | Verificar rejeição de dados inválidos e integridade pelos CT funcionais. CT11 verifica a gravação de pagamento; CT17 verifica membros após reinício; CT21 verifica pagamentos após reinício. Não estender esses resultados às demais entidades. | Parcialmente verificado; ver evidências específicas. |
| VN05 | RNF05 | Revisar separação de apresentação, regras de negócio e persistência; registrar análise estática e resultado do quality gate adotado. | Planejado |
| VN06 | RNF06 | Executar `./mvnw clean verify`, conferir testes executados e ignorados, relatório JaCoCo e limite mínimo de 75% de instruções. Conferir separadamente a execução na CI. | Validação local da D05 registrada em EV21–EV23; CI da D05 verificada na execução 44972713889: 238 testes sem falhas, erros ou ignorados, limite JaCoCo aprovado e relatório publicado. Referência em evidencias.md. |

## 5. Cobertura e rastreabilidade

| Requisito / critério | Caso planejado |
|---|---|
| RF02: CA01, CA02, CA03, CA04, CA24, CA26 | CT01, CT02, CT03, CT04 |
| RF03: CA05, CA06 | CT05, CT06 |
| RF04: CA07, CA17, CA18, CA24, CA26 | CT07 — verifica a atualização de membros e reutiliza variações de entrada dos CT02–CT04, que tratam do cadastro. |
| RF05: CA08, CA19 | CT08 |
| RF06: CA09, CA10, CA21 | CT09 |
| RF07: CA20 | CT10 |
| RF08: CA11, CA12, CA27 | CT11, CT12 |
| RF09: CA14, CA22 | CT13, CT14 |
| RF01: CA15, CA16, CA23 | CT15, CT16 |
| RNF01 a RNF06 | VN01 a VN06; CT15, CT16, CT17 e CT21 complementam conforme indicado |
| RF10: CA31, CA32, CA33, CA34 | CT18, CT19, CT20 |
| RF08, RF10 e RNF04 / CA25: pagamentos após reinício | CT21 |
| RF09 / CA28, CA29: preparação técnica do histórico | Caso específico pendente de definição e vínculo. |
| RF07 e RF09 / CA30: referência inválida na consulta | Caso específico pendente de definição e vínculo. |

O CA13 permanece fora do escopo de registro pela interface. Os identificadores CT01 a CT21 estão definidos neste plano. CT13 e CT14 tratam das consultas do histórico; não comprovam integralmente os CA28–CA30. A definição dos casos específicos desses critérios permanece como acompanhamento da D04. A situação de cada CT deve seguir seus registros de execução e limitações.

## 6. Tipos de teste

### 6.1. Testes unitários

Verificar componentes e regras de negócio isoladamente, incluindo validações, CPF, prevenção de duplicidade e operações de cadastro.

### 6.2. Testes de integração

Verificar a comunicação entre os componentes da aplicação e a persistência dos dados no banco de dados, incluindo as operações de consulta e cadastro.

### 6.3. Testes de usabilidade

Avaliar a clareza dos formulários, a compreensão das mensagens de validação, a navegação e a facilidade de utilização das telas implementadas.

### 6.4. Testes de sistema e validação

Verificar os fluxos completos implementados, desde a entrada das informações até a apresentação do resultado ao usuário.

## 7. Estratégia de execução

Os testes serão elaborados de acordo com os requisitos, as regras de negócio e os critérios de aceite documentados.

A estratégia prevê:

1. Criar os testes correspondentes às funcionalidades implementadas.
2. Executar os testes automatizados durante o desenvolvimento.
3. Corrigir falhas identificadas.
4. Reexecutar os testes após as correções.
5. Registrar os resultados e as evidências.
6. Verificar a cobertura de código.

## 8. Ambiente de testes

O ambiente previsto utiliza:

- Java 21.
- Spring Boot.
- Maven.
- JUnit 5.
- Mockito.
- Spring MockMvc, quando aplicável.
- JaCoCo para análise de cobertura.
- PostgreSQL para testes que necessitem de persistência real.

A configuração definitiva do ambiente de testes deverá acompanhar a configuração efetivamente adotada pela aplicação.

## 9. Dados de teste

Serão utilizados dados fictícios, evitando a utilização de informações pessoais reais.

Os conjuntos de dados deverão incluir:

- Membros com informações válidas.
- Campos obrigatórios vazios.
- CPFs inválidos.
- CPFs já cadastrados.
- Consultas com resultados e sem resultados.
- Treinos, pagamentos e atividades válidos e inválidos, conforme as regras implementadas.
- Credenciais fictícias para os testes de autenticação.

### 9.1. Dados de CPF para CT03 e CT04

Usar os valores abaixo apenas como dados de teste. Manter os demais campos válidos para que o resultado de cada tentativa dependa do CPF.

| Caso | Entrada | Condição ou motivo | Resultado esperado |
|---|---|---|---|
| CT04 | `52998224725` | CPF válido, ainda não cadastrado | Aceitar e gravar `52998224725`. |
| CT04 | ` 529.982.247-25 ` | Mesmo CPF, pontuado e com espaços externos; base independente, sem cadastro prévio | Aceitar e gravar `52998224725`. |
| CT03 | `52998224726` | Dígito verificador incorreto | Rejeitar. |
| CT03 | `5299822472` | Apenas 10 dígitos | Rejeitar. |
| CT03 | `52998224A25` | Contém letra | Rejeitar. |
| CT03 | `529 98224725` | Contém espaço interno | Rejeitar. |
| CT03 | `00000000000` e `11111111111` | Onze dígitos iguais; uma tentativa para cada sequência | Rejeitar ambas. |
| CT04 | `529.982.247-25` | Já existe um membro com `52998224725` | Rejeitar por duplicidade e preservar o cadastro existente. |

### 9.2. Validação de CPF: testes existentes e evidências

| Referência | O que o código de teste verifica | Situação da evidência |
|---|---|---|
| CT03 — `CpfUtilsTest` e `MembroServiceTest` | CPF com dígito verificador incorreto, letra, quantidade incorreta de dígitos ou sequência `11111111111`. No serviço, os testes esperam rejeição antes de consultar o repositório. | Testes escritos. O registro da CI não discrimina a execução dessas variantes. A entrada `00000000000` está prevista na seção 9.1, mas não aparece explicitamente nessas duas classes. |
| CT04 — `CpfUtilsTest` e `MembroServiceTest` | Normalização de CPF com e sem pontuação e de espaços externos. No cadastro, o serviço consulta o CPF normalizado e não chama a gravação se o repositório indicar duplicidade. | Testes escritos. O teste de serviço usa repositório simulado; a execução específica não está identificada no registro da CI. |
| CT04 — `MembroCpfPostgresIntegrationTest.postgresConstraintRejectsSecondMemberWithSameCpf` | Após o primeiro cadastro, uma inserção direta no PostgreSQL com o mesmo CPF deve violar a restrição `uk_membros_cpf` (SQLSTATE `23505`). Deve permanecer apenas um registro com esse CPF. | Teste integrado escrito, mas ainda não executado no PostgreSQL. A execução e o registro do resultado estão pendentes. |
| CT04 — `MembroCpfPostgresIntegrationTest.servicoNormalizaPontuacaoERejeitaCpfDuplicadoMantendoUmRegistro` | Após cadastrar o CPF sem pontuação, uma nova tentativa pelo serviço com o mesmo CPF pontuado deve ser rejeitada. A consulta ao banco deve retornar o CPF sem pontuação e apenas um registro. | Teste integrado escrito, mas ainda não executado no PostgreSQL. A execução e o registro do resultado estão pendentes. |

A execução de 10/11/2026 está documentada em [evidencias.md](evidencias.md), seção 3. A EV02 registra 37 testes executados com sucesso no run #27, sem identificar os métodos de CPF. A EV03 identifica o carregamento do contexto Spring com PostgreSQL e Flyway. Nenhuma dessas evidências, isoladamente, comprova a execução integral dos CT03 e CT04.

Para registrar a execução integrada, identificar o método, a data, o resultado e o relatório correspondente. A classe de integração exige `DB_PASSWORD` definida e não vazia; um teste ignorado não deve ser registrado como aprovado.

## 10. Critérios de aceitação dos testes

Uma funcionalidade será considerada aprovada quando:

- Atender aos critérios de aceite correspondentes.
- Produzir o resultado esperado para entradas válidas.
- Rejeitar entradas inválidas conforme as regras definidas.
- Não apresentar falhas que comprometam a integridade dos dados.
- Possuir resultados de teste registrados.

A meta de cobertura de código definida para o projeto é de **75%**, conforme o planejamento da A3. A cobertura deverá ser medida com JaCoCo; atingir esse percentual, isoladamente, não comprova que todos os requisitos foram testados.

## 11. Ferramentas e execução

Os testes automatizados e o relatório JaCoCo são executados pelo ciclo Maven `verify`:

```powershell
mvn clean verify
```

No Git Bash, Linux ou macOS: `./mvnw clean verify`. No PowerShell do Windows: `.\mvnw.cmd clean verify`. O JaCoCo 0.8.16 gera os relatórios HTML e XML em `target/site/jacoco/index.html` e `target/site/jacoco/jacoco.xml`.

Os resultados reais da execução de 10/11/2026 estão registrados em [`evidencias.md`](evidencias.md). A aprovação do conjunto automatizado não altera automaticamente o estado dos CTs: a execução específica e suas evidências devem ser registradas para justificar a situação de cada cenário.

Na execução local da D05 de 21/11/2026, foram registrados 238 testes, sem falhas, erros ou testes ignorados. O JaCoCo apresentou 83% de cobertura de instruções e 64% de branches, com aprovação do limite configurado de 75% de instruções. Esses valores correspondem à execução registrada em EV21–EV23, anterior à atualização da branch com a D04; não representam automaticamente o resultado de uma execução posterior.

## 12. Responsabilidades

A elaboração e a execução dos testes deverão respeitar a divisão de responsabilidades definida no documento `docs/planning/plano-de-tasks.md`.

Cada integrante deverá contribuir com os testes relacionados às funcionalidades sob sua responsabilidade, conforme os acordos do grupo.

## 13. Registro de resultados e defeitos

Os resultados deverão ser documentados em `docs/testing/evidencias.md`.

Para cada execução relevante, registrar:

- Identificador do teste.
- Funcionalidade verificada.
- Resultado obtido.
- Data da execução.
- Evidência disponível.
- Falhas encontradas e situação da correção.

Os defeitos deverão ser acompanhados pela equipe até que sejam corrigidos e verificados novamente.

## 14. Situação atual

Este documento representa o planejamento dos cenários. A execução automatizada registrada em [`evidencias.md`](evidencias.md) não comprova a execução individual dos CTs descritos; eles não devem ser considerados aprovados até que seus procedimentos e resultados específicos tenham evidências correspondentes.

## 15. Pendências

- Ampliar a associação entre testes automatizados e os CT/VN aplicáveis, sem inferir cobertura funcional pela execução global.
- Registrar em `evidencias.md` os resultados individuais dos testes integrados de CPF descritos na seção 9.2 e ampliar os testes de persistência das demais funcionalidades conforme a implementação.
- Realizar os testes de usabilidade.
- Registrar as evidências ainda pendentes de testes funcionais, usabilidade e outras execuções de CI.
- Relacionar cada integrante às funcionalidades e aos respectivos arquivos de teste.
- Concluir a associação e a execução integral dos CT11, CT12 e CT18–CT21, respeitando as verificações parciais já registradas.
- Definir e vincular os casos específicos dos CA28–CA30 na tarefa de acompanhamento da rastreabilidade.

A validação local após o merge e a execução da CI da D05 foram concluídas com 238 testes, sem falhas, erros ou testes ignorados, e aprovação do limite automático de 75% de cobertura de instruções. O relatório JaCoCo foi publicado na execução [44972713889](https://github.com/Miaunisul/Vigor/actions/runs/44972713889). Os resultados e links estão registrados em `evidencias.md`.
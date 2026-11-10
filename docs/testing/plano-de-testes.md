# Plano de Testes — VIGOR

## 1. Objetivo

Definir a estratégia de testes do VIGOR — Sistema de Cadastro de Academia, para verificar se as funcionalidades implementadas atendem aos requisitos funcionais, às regras de negócio e aos requisitos de qualidade estabelecidos para a A3 de Gestão e Qualidade de Software da UNISUL.

## 2. Escopo dos testes

Os testes contemplarão as funcionalidades RF01 a RF09 previstas para a entrega. Cada caso abaixo permanece planejado até que sua implementação ou execução seja confirmada. O CA13, registro de atividades por interface, está fora do escopo; os dados do histórico serão previamente disponibilizados por procedimento técnico documentado.

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

- Registro de pagamentos recebidos com status Pago.
- Verificação da persistência do registro por leitura técnica de teste, sem depender de tela financeira (CT11).
- Rejeição de dados inválidos e de status diferente de Pago (CT12).

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

Os casos desta seção estão **Planejados**. Os passos descrevem verificações a realizar, não resultados já observados. Os dados devem ser fictícios. Situações inválidas indicadas em um critério devem ser executadas separadamente.

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
- **Passos:** Tentar cadastrar separadamente CPFs com quantidade incorreta de dígitos, dígitos verificadores incorretos e todos os dígitos iguais; considerar CPF com e sem pontuação.
- **Resultado esperado:** Cada CPF inválido é rejeitado, o sistema informa a correção necessária e não persiste o registro.

### CT04 — CPF duplicado e normalizado

- **Status:** Planejado.
- **RF/CA:** RF02; CA04, CA26.
- **Pré-condições:** Funcionário autenticado; existe membro cadastrado com CPF válido.
- **Passos:** Tentar cadastrar outro membro com os mesmos dígitos do CPF existente usando formatação igual e diferente. Em cada tentativa positiva, cadastrar com CPF válido que ainda não esteja cadastrado, considerando a comparação normalizada; para as duas formas de pontuação, usar CPFs válidos de teste distintos e inéditos.
- **Resultado esperado:** Tentativas duplicadas são rejeitadas e preservam o registro existente. Cada tentativa positiva com CPF válido ainda não cadastrado é aceita, com ou sem pontuação, se os demais dados forem válidos.

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

- **Status:** Planejado.
- **RF/CA:** RF08; CA11.
- **Pré-condições:** Funcionário autenticado; membro existente, valor positivo com no máximo duas casas decimais e data válida.
- **Passos:** Registrar o pagamento recebido para o membro. Confirmar a operação e verificar a persistência por consulta técnica ao registro armazenado no PostgreSQL no ambiente de teste, comparando membro, valor, data, identificador e status Pago, sem usar uma tela financeira.
- **Resultado esperado:** O registro persistido corresponde ao membro, valor e data informados, possui ID único e status Pago; a operação é confirmada. Esta verificação é de persistência do registro após gravação, sem reinício da aplicação.

### CT12 — Rejeição de pagamento inválido ou sem membro existente

- **Status:** Planejado.
- **RF/CA:** RF08; CA12, CA27. **Regras:** RN10, RN11, RN12 e RN13.
- **Pré-condições:** Funcionário autenticado; membro cadastrado para validar os demais dados; dados de teste que permitam também enviar pagamento sem referência a membro e com referência a membro inexistente.
- **Passos:** Em execuções separadas, tentar gravar pagamento sem referência a membro, com referência a membro inexistente, valor ausente, não numérico, igual a zero, negativo ou com mais de duas casas decimais, data ausente ou inválida e status diferente de Pago. Nos casos em que a interface prevê bloquear o envio, verificar o bloqueio; para os demais, exercitar a validação do servidor pelo mecanismo de teste disponível.
- **Resultado esperado:** Cada tentativa é rejeitada e o pagamento não é persistido. A interface bloqueia o envio quando esse comportamento estiver previsto; em qualquer caso, a validação deve rejeitar os dados e identificar a informação a corrigir: selecionar membro cadastrado, informar valor numérico positivo com no máximo duas casas decimais, informar data válida ou manter status Pago.

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

- **Status:** Planejado; execução pendente da implementação de cadastro técnico de funcionários, autenticação e proteção de senha.
- **RF/CA:** RF01; CA15. **Regras:** RN18, RN19 e RN20. **RNF:** RNF01.
- **Pré-condições:** Ambiente de teste isolado; disponíveis valores fictícios para preparar um funcionário e uma senha de teste. O mecanismo de codificação e verificação de senhas deve estar implementado para executar as verificações de hash.
- **Passos:** 1. Preparar tecnicamente um funcionário de teste com identificador, nome, cargo, login e uma senha fictícia, sem criar uma tela de cadastro. 2. Conferir nos funcionários disponibilizados que os identificadores são únicos e que nome, cargo e login estão preenchidos e não contêm somente espaços; comparar a quantidade de funcionários com a quantidade de logins distintos. 3. Em ambiente controlado, consultar o valor armazenado no campo de senha do funcionário de teste, sem registrar o valor nem credenciais nas evidências. 4. Verificar que o valor armazenado não é igual à senha original e que seu formato corresponde a um hash produzido pelo mecanismo de codificação de senhas adotado pelo projeto. 5. Usar o mecanismo de verificação correspondente para confirmar que a senha original corresponde ao valor armazenado; não tentar reverter ou decodificar o hash. 6. Autenticar com a senha original e acessar uma funcionalidade interna.
- **Resultado esperado:** Dados obrigatórios do funcionário estão presentes, os identificadores e logins são únicos, o valor armazenado difere da senha original e corresponde ao formato e à verificação do mecanismo de hash adotado. A autenticação com a senha original permite o acesso interno. Nenhuma senha ou valor armazenado é exposto nas evidências.
- **Situação da implementação:** Não foram encontrados no código uma entidade ou cadastro técnico de funcionário, componentes de autenticação, dependência de segurança ou mecanismo de codificação/verificação de senhas. O mecanismo efetivamente adotado ainda não pode ser identificado; portanto, este teste não pode ser executado nem considerado aprovado até que a implementação exista.

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

## 4. Verificações planejadas de requisitos não funcionais

As verificações abaixo complementam os CT funcionais, sem receber identificadores CT na matriz. Permanecem planejadas e seus resultados deverão ser registrados em `docs/testing/evidencias.md`.

| ID | Requisito | Procedimento e resultado esperado | Status |
|---|---|---|---|
| VN01 | RNF01 | Usar CT15 e CT16 para verificar autenticação, bloqueio de acesso e senha armazenada por hash apropriado; não expor credenciais nas evidências. | Planejado |
| VN02 | RNF02 | Registrar ambiente, volume de dados, tempos observados nas consultas e ocorrência de travamentos/timeouts, sem presumir limite numérico. | Planejado |
| VN03 | RNF03 | Avaliar fluxos principais quanto a campos identificados, obrigatoriedade, clareza das mensagens e navegação; registrar dificuldades. | Planejado |
| VN04 | RNF04 | Usar os CT funcionais para verificar rejeição/não persistência de dados inválidos e integridade dos vínculos; CT11 verifica persistência do pagamento após gravação; CT17 verifica persistência após reinício somente dos dados de membro criado/atualizado. Não atribuir ao CT17 persistência após reinício de treinos, pagamentos, atividades ou funcionários. | Planejado |
| VN05 | RNF05 | Revisar separação de apresentação, regras de negócio e persistência; registrar análise estática e resultado do quality gate adotado. | Planejado |
| VN06 | RNF06 | Executar `mvn test`, verificar execução na integração contínua e gerar relatório JaCoCo após sua configuração; registrar cobertura frente à meta planejada de 75%. | Planejado |

## 5. Cobertura e rastreabilidade

| Requisito / critério | Caso planejado |
|---|---|
| RF02: CA01, CA02, CA03, CA04, CA24, CA26 | CT01, CT02, CT03, CT04 |
| RF03: CA05, CA06 | CT05, CT06 |
| RF04: CA07, CA17, CA18, CA24, CA26 | CT07; CT03 e CT04 cobrem validações correlatas de CPF |
| RF05: CA08, CA19 | CT08 |
| RF06: CA09, CA10, CA21 | CT09 |
| RF07: CA20 | CT10 |
| RF08: CA11, CA12, CA27 | CT11, CT12 |
| RF09: CA14, CA22 | CT13, CT14 |
| RF01: CA15, CA16, CA23 | CT15, CT16 |
| RNF01 a RNF06 | VN01 a VN06; CT15, CT16 e CT17 complementam conforme indicado |

O CA13 é futuro e não se aplica aos testes desta entrega. Os identificadores CT01 a CT17 estão definidos no plano. CT13 e CT14 cobrem, respectivamente, histórico com e sem atividades. Todos os casos permanecem planejados.

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

No Windows, também pode ser usado `.\mvnw.cmd clean verify`; em Linux ou macOS, `./mvnw clean verify`. O JaCoCo 0.8.16 gera os relatórios HTML e XML em `target/site/jacoco/index.html` e `target/site/jacoco/jacoco.xml`.

Os resultados reais da execução de 10/11/2026 estão registrados em [`evidencias.md`](evidencias.md). A aprovação do conjunto automatizado não altera automaticamente o estado dos CTs: cada cenário funcional continua planejado até que sua execução específica e evidência sejam registradas.

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
- Ampliar os testes de persistência no PostgreSQL além do carregamento de contexto, conforme as funcionalidades implementadas.
- Realizar os testes de usabilidade.
- Registrar as evidências pendentes de testes funcionais, usabilidade e CI.
- Avaliar os indicadores de cobertura: instruções em 78% e branches em 73% na execução de 10/11/2026; a configuração ainda não aplica um limite automático.
- Relacionar cada integrante às funcionalidades e aos respectivos arquivos de teste.

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

- Cadastro de treinos válidos.
- Associação do treino a um membro existente.
- Rejeição de associação a membro inexistente.

### 2.5. Pagamentos

- Registro de pagamentos válidos.
- Validação dos valores informados.
- Verificação dos status de pagamento definidos pela equipe.

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
- **Passos:** Tentar cadastrar outro membro com os mesmos dígitos do CPF existente usando formatação igual e diferente. Em execuções separadas, cadastrar CPF válido inédito com e sem pontuação.
- **Resultado esperado:** Tentativas duplicadas são rejeitadas e preservam o registro existente; CPF válido inédito é aceito em ambas as formas, se os demais dados forem válidos.

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
- **Passos:** Cadastrar treino válido. Em execuções separadas, tentar gravar sem cada dado obrigatório, tipo/descrição vazios ou só com espaços, duração ausente, não inteira, zero ou negativa, data ausente/inválida e membro inexistente.
- **Resultado esperado:** Treino válido é associado ao membro e recebe ID único. Cada tentativa inválida é rejeitada, informa os dados a corrigir e não persiste o treino.

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
- **Passos:** Registrar o pagamento para o membro e consultar o resultado.
- **Resultado esperado:** Pagamento associado ao membro é persistido com ID único e status Pago, e a operação é confirmada.

### CT12 — Rejeição de pagamento inválido ou sem membro existente

- **Status:** Planejado.
- **RF/CA:** RF08; CA12, CA27. **Regras:** RN10, RN11, RN12 e RN13.
- **Pré-condições:** Funcionário autenticado; membro cadastrado para validar os demais dados; dados de teste que permitam também enviar pagamento sem referência a membro e com referência a membro inexistente.
- **Passos:** Em execuções separadas, tentar gravar pagamento sem referência a membro, com referência a membro inexistente, valor ausente, não numérico, igual a zero, negativo ou com mais de duas casas decimais, data ausente ou inválida e status diferente de Pago.
- **Resultado esperado:** Cada tentativa é rejeitada e o pagamento não é persistido. A mensagem identifica a informação que impede o registro: selecionar um membro cadastrado; informar valor numérico positivo com no máximo duas casas decimais; informar uma data de pagamento válida; ou manter o status Pago.

### CT13 — Consulta do histórico com atividades

- **Status:** Planejado.
- **RF/CA:** RF09; CA14.
- **Pré-condições:** Funcionário autenticado; dados de atividade carregados por procedimento técnico documentado e válidos conforme RN14 a RN16; registros de membros distintos.
- **Passos:** Consultar o histórico de cada membro selecionado.
- **Resultado esperado:** Cada consulta apresenta somente as atividades do membro correspondente, incluindo atividade, data e tempo de execução.

### CT14 — Consulta do histórico sem atividades

- **Status:** Planejado.
- **RF/CA:** RF09; CA22.
- **Pré-condições:** Funcionário autenticado; existe membro sem atividades carregadas.
- **Passos:** Consultar o histórico desse membro.
- **Resultado esperado:** O sistema informa a ausência de atividades para o membro consultado.

### CT15 — Login válido e senha protegida

- **Status:** Planejado.
- **RF/CA:** RF01; CA15. **RNF:** RNF01.
- **Pré-condições:** Funcionário previamente disponibilizado por procedimento técnico documentado, com login único e senha armazenada por hash apropriado.
- **Passos:** Autenticar com credenciais corretas, acessar funcionalidade interna e verificar a forma armazenada da credencial sem expor senha nas evidências.
- **Resultado esperado:** Acesso interno permitido; senha não persistida em texto puro e evidências sem credenciais.

### CT16 — Login inválido e acesso sem autenticação

- **Status:** Planejado.
- **RF/CA:** RF01; CA16, CA23. **RNF:** RNF01.
- **Pré-condições:** Funcionário de teste previamente disponibilizado; sessão não autenticada para o acesso direto.
- **Passos:** 1. Tentar login com usuário inexistente. 2. Em outra tentativa, usar login existente com senha incorreta. 3. Sem possuir sessão autenticada, tentar acessar diretamente uma rota ou funcionalidade interna protegida.
- **Resultado esperado:** As tentativas de login são negadas com mensagem apropriada, sem expor informação sensível. O acesso direto é negado, a funcionalidade protegida não pode ser visualizada nem utilizada e o sistema solicita autenticação.

### CT17 — Persistência após reinício da aplicação

- **Status:** Planejado.
- **RF/CA:** RF02, RF04; CA25. **RNF:** RNF04.
- **Pré-condições:** PostgreSQL de testes configurado; funcionário autenticado; dados fictícios disponíveis.
- **Passos:** Cadastrar um membro e atualizá-lo validamente; encerrar e reiniciar efetivamente a aplicação mantendo o banco; autenticar novamente e consultar o membro.
- **Resultado esperado:** Dados cadastrados e atualizados continuam disponíveis após o reinício.

## 4. Verificações planejadas de requisitos não funcionais

As verificações abaixo complementam os CT funcionais, sem receber identificadores CT na matriz. Permanecem planejadas e seus resultados deverão ser registrados em `docs/testing/evidencias.md`.

| ID | Requisito | Procedimento e resultado esperado | Status |
|---|---|---|---|
| VN01 | RNF01 | Usar CT15 e CT16 para verificar autenticação, bloqueio de acesso e senha armazenada por hash apropriado; não expor credenciais nas evidências. | Planejado |
| VN02 | RNF02 | Registrar ambiente, volume de dados, tempos observados nas consultas e ocorrência de travamentos/timeouts, sem presumir limite numérico. | Planejado |
| VN03 | RNF03 | Avaliar fluxos principais quanto a campos identificados, obrigatoriedade, clareza das mensagens e navegação; registrar dificuldades. | Planejado |
| VN04 | RNF04 | Usar os cenários funcionais e CT17 para verificar rejeição de inválidos, integridade dos vínculos, preservação em operações rejeitadas e persistência após reinício. | Planejado |
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

O CA13 é futuro e não se aplica aos testes desta entrega. A numeração CT01 a CT16 proposta na matriz foi preservada; CT17 foi definido para CA25. CT13 e CT14 cobrem, respectivamente, histórico com e sem atividades. Todos os casos permanecem planejados.

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

Os testes automatizados deverão poder ser executados com:

```bash
mvn test
```

A geração do relatório de cobertura dependerá da configuração do JaCoCo no projeto. Após essa configuração, o comando e o caminho do relatório deverão ser registrados neste documento.

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

Este documento representa o planejamento dos testes. Os testes descritos não devem ser considerados executados ou aprovados até que existam resultados e evidências correspondentes.

## 15. Pendências

- Implementar os testes automatizados.
- Configurar o JaCoCo.
- Confirmar a configuração do ambiente de integração com o banco de dados.
- Realizar os testes de usabilidade.
- Registrar os resultados e as evidências.
- Verificar a cobertura mínima planejada de 75%.
- Relacionar cada integrante às funcionalidades e aos respectivos arquivos de teste.

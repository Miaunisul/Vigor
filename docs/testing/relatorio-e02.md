# Revisão final E02 — Issue #36

Revisão realizada por Emely Santos da Silveira em 23/11/2026. Destinatária dos resultados e encaminhamentos documentais — Maria Eduarda Souza dos Santos Ferreira, `@dudahmesmo`.

## Versões e resultado

A base avaliada foi a `develop` no commit `0ed8b47e41d2e016be69bfbe8f5108de4bd3f4e6`. As dependências M06, M07, D05, M08, L01 e L02 estavam integradas e suas issues encerradas. Foram conferidos os PRs #42/#54, #46/#52, #49, #56, #38 e #41, respectivamente. A D04 e a D06 foram consideradas pelos PRs #48 e #51.

A suíte original executou 257 testes, sem falhas, erros ou ignorados. Após a revisão, o código de testes do commit `85a0128e3806324440e62624bcbcd3ecac411a35` executou **281 testes em 26 classes, com zero falhas, erros ou ignorados**. Foram acrescentados 24 cenários HTTP com sessão, CSRF, Spring Boot e PostgreSQL reais.

O JaCoCo aprovou o mínimo de 75% de instruções. A aprovação automatizada se aplica aos cenários e às asserções executados. A aceitação integral dos requisitos depende também das verificações parciais e dos encaminhamentos registrados neste relatório.

## Ambiente e reprodução

- Windows, Java 21.0.12.1, Spring Boot 4.1.2, Maven Wrapper e JaCoCo 0.8.16.
- PostgreSQL 18.3 temporário, acessível apenas por loopback na porta 55436. As migrations V1 a V5 foram aplicadas pelo Flyway em banco vazio.
- Banco `vigor_suite` para a suíte e banco `vigor` para a conferência de interface. Os dados foram fictícios e as credenciais temporárias ficaram fora dos artefatos versionados.
- Aplicação de interface em `127.0.0.1:8086`, executando o JAR produzido a partir da versão-base. Os ajustes desta revisão alteram testes.
- Comando local `mvnw.cmd --batch-mode --no-transfer-progress clean verify`, com `DB_PASSWORD` e `SPRING_DATASOURCE_URL` configuradas para o banco de testes.
- Execução local final concluída em 23/11/2026 às 09:01:20, horário de São Paulo, com `BUILD SUCCESS` e `All coverage checks have been met.`.

A suíte pode ignorar integrações quando `DB_PASSWORD` está ausente. Nesta revisão ela estava configurada e o Surefire registrou zero testes ignorados. Após os testes, as cinco tabelas de domínio do banco da suíte ficaram sem registros residuais.

## Testes conferidos e relação com a documentação

A conferência utilizou a [matriz existente](../planning/matriz-de-rastreabilidade.md), o [plano de testes](plano-de-testes.md), os [requisitos](../requirements/requisitos.md), as [regras](../requirements/regras-de-negocio.md) e os [critérios](../requirements/criterios-de-aceite.md). Os vínculos RF01–RF10, RN01–RN21, CA01–CA34, CT01–CT21 e VN01–VN06 foram confrontados com a implementação e com as asserções dos testes. As referências CT/CA presentes na matriz possuem definições, e os links locais dos sete documentos centrais revisados apontam para arquivos existentes.

Os grupos abaixo identificam os testes executados que sustentam a revisão. Os números dos grupos são disjuntos. Os 24 testes novos, compartilhados entre módulos, aparecem em linha própria. O arquivo [testes-por-classe.csv](e02/testes-por-classe.csv) preserva os totais extraídos do Surefire.

| Grupo e vínculos existentes | Classes conferidas | Resultado e limites |
|---|---|---|
| Membros e CPF — RF02–RF05, RN01–RN06/RN21, CT01–CT08 | `MembroControllerTest`, `MembroTemplateRenderingTest`, `MembroServiceTest`, `MembroCpfHttpPostgresIntegrationTest`, `MembroAtualizacaoPostgresIntegrationTest`, `MembroCpfPostgresIntegrationTest`, `MembroExclusaoPostgresIntegrationTest`, `CpfUtilsTest` | 89 testes aprovados. Cadastro e edição válidos, CPF próprio, preservação de ID/data, CPF inválido/duplicado e exclusão sem vínculos ou bloqueada por cada vínculo. As variantes HTTP de CPF não abrangem todas as combinações de campos ausentes, vazios e com espaços de CT02/CT07. |
| Treinos — RF06/RF07, RN07–RN09/RN21, CT09/CT10 | `TreinoControllerTest`, `TreinoTemplateRenderingTest`, `TreinoServiceTest`, `TreinoPostgresIntegrationTest`, `TreinoHttpPostgresIntegrationTest` | 50 testes aprovados. Cadastro/consulta e isolamento por membro, campos inválidos, duração não inteira na entrada web, duração não positiva, membro inexistente, restrições do banco e estados de interface. Conferência visual de cadastro e conjuntos com/sem resultados descrita abaixo. |
| Pagamentos — RF08/RF10, RN10–RN13/RN21, CT11/CT12/CT18–CT21 | `PagamentoControllerTest`, `PagamentoTemplateRenderingTest`, `PagamentoServiceTest`, `PagamentoPostgresIntegrationTest` | 68 testes aprovados. Atribuição de PAGO, tentativa de substituir status/ID, valores/data/membro inválidos, consultas vazias, isolamento e ordenação com desempate por ID. Os testes isolados do controller continuam identificados pelo seu nível. |
| Histórico — RF09, RN14–RN17/RN21, CT13/CT14 e lacunas CA28–CA30 | `AtividadeTemplateRenderingTest`, `HistoricoAtividadeServiceTest`, `HistoricoAtividadePostgresIntegrationTest` | 34 testes aprovados. Vínculos com membro/responsável, dados e tempo inválidos, filtros, período invertido, ordenação e isolamento por membro. A conversão de entradas da carga técnica e todas as variantes do CA29 precisam de procedimento próprio. |
| Autenticação — RF01/RNF01, RN18–RN20, CT15/CT16 | `FuncionarioSecurityFlowTest`, `FuncionarioValidationTest`, `FuncionarioServiceTest`, `FuncionarioPostgresIntegrationTest` | 15 testes aprovados. Dados obrigatórios, BCrypt, login normalizado/único, credenciais inválidas, proteção das rotas, sessão e logout. `FuncionarioSecurityFlowTest` usa MockMvc com serviço simulado. A integração real foi complementada nesta revisão. |
| Revisão HTTP autenticada — CT08/CT11/CT12/CT13/CT16/CT20 e CA30 | `RevisaoFinalHttpPostgresIntegrationTest` | 24 testes aprovados. Seis rotas sem login, usuário inexistente, senha incorreta, logout, pagamento PAGO apesar de status enviado pelo cliente, bloqueio de exclusão por pagamento, oito variantes inválidas de valor/data, membro inexistente e quatro referências malformadas verificadas em pagamentos e histórico. |
| Inicialização | `VigorApplicationTests` | 1 teste aprovado. Inicialização real do contexto e banco. |

Na classe nova, `pagamentoInvalidoPeloHttpNaoPersiste` verifica separadamente valor vazio, não numérico, zero, negativo, mais de dez dígitos inteiros, mais de duas casas decimais, data vazia e data impossível. Os demais campos permanecem válidos. Cada tentativa verifica resposta com erros de campo e ausência de pagamento no banco.

`historicoHttpMostraResponsavelEFiltraPeriodoSemResultados` verifica apresentação de atividade, responsável, data e minutos e a exclusão dessa atividade pelo filtro de período. O isolamento entre dois membros é sustentado pelos testes existentes de repositório e pela verificação visual E02-UI07.

## Verificações de interface executadas

A interface foi operada pelo navegador integrado, em uma janela de aproximadamente 1265 pixels de largura, com dados fictícios. Os resultados se referem à versão-base. A revisão de interação cobre os percursos abaixo e mantém explícitas as etapas ainda parciais.

| ID | Cenário e referência | Resultado observado |
|---|---|---|
| E02-UI01 | Login inválido, login válido e identificação — CT15/CT16 | Senha incorreta exibiu “Login ou senha inválidos.”. Login válido abriu membros e apresentou Revisora E02/Administradora. A tela de login e seus recursos estavam acessíveis. |
| E02-UI02 | Cadastro rejeitado e aceito — CT01/CT03/CT04 | `00000000000` foi rejeitado, com mensagem junto ao CPF e preservação dos demais campos. Após informar CPF válido pontuado, Carla Revisao E02 foi cadastrada com ID 47 e data 23/11/2026. |
| E02-UI03 | Edição e detalhes — CT05/CT07 | A edição abriu os dados preenchidos. O endereço foi alterado de Rua Ficticia E02 3 para Rua Ficticia E02 33 mantendo o CPF próprio. A confirmação, o ID, a data original e os dados dos detalhes foram conferidos. |
| E02-UI04 | Cadastro e consulta de treino — CT09/CT10 parcial | Treino Cardio E02 foi cadastrado para Carla com descrição, 30 minutos e data 23/11/2026. A lista preservou a seleção. Ao selecionar Bruno, exibiu ausência de treinos. A descrição foi informada no cadastro, mas a tela de detalhes de treino não foi aberta nesta verificação visual. |
| E02-UI05 | Pagamentos — CT11/CT18/CT19 | A listagem inicialmente vazia informou ausência. Pagamento de R$ 149,90 foi registrado para Carla em 23/11/2026 com status Pago. Bruno permaneceu sem pagamentos e com seleção preservada. “Limpar” restaurou a listagem geral. |
| E02-UI06 | Histórico com registros — CT13 parcial | O histórico apresentou Ana, Esteira E02, 23/11/2026, 30 min e Revisora E02. O registro foi previamente carregado no banco temporário com vínculos válidos. A carga completa de todas as variantes inválidas de CA29 ficou pendente. |
| E02-UI07 | Histórico vazio e período inválido — CT14/VN03 | Selecionar Bruno exibiu ausência de atividades e não apresentou a atividade de Ana. Data inicial 24/11/2026 e final 23/11/2026 produziram mensagem de período invertido, preservando as datas. |
| E02-UI08 | Reinício da aplicação — CT17/CT21 | O processo Java original foi encerrado e um novo processo iniciou o mesmo JAR com o mesmo banco. Após nova autenticação, membro editado, ID/data, endereço, telefone, treino e pagamento continuavam disponíveis. A comparação SQL antes/depois confirmou identidade, CPF, nome, endereço e data do membro e ID/valor/data/status do pagamento. |
| E02-UI09 | Exclusão com vínculos — CT08 parcial | Solicitar exclusão de Carla exibiu “existem treinos vinculados” e preservou a pessoa cadastrada. As demais modalidades de bloqueio são verificadas pelos testes automatizados, incluindo pagamento pelo HTTP nesta revisão. |
| E02-UI10 | Logout e acesso direto — CT16 | “Sair” exibiu sessão encerrada. Novo acesso direto a pagamentos retornou ao login. |

A [captura após reinício](e02/membro-apos-reinicio.jpg), a [captura de período inválido](e02/historico-periodo-invalido.jpg) e a [comparação de persistência](e02/persistencia-reinicio.csv) acompanham o relatório. Os CPFs são fictícios. A captura da interface mantém a máscara aplicada pelo sistema.

**Situações para consolidação dos CTs** — CT01, CT05, CT14 e CT16 possuem os percursos acima conferidos. CT17/CT21 tiveram o reinício efetivamente executado para o membro/pagamento identificados. CT02–CT04, CT07–CT13, CT15 e CT18–CT20 possuem evidências por cenário e por camada, com limites nas tabelas. CT06, com uma base inteira sem membros e uma sessão autenticada, não foi executado visualmente. A situação consolidada deve considerar todas as variantes previstas no plano.

## Cobertura e integração contínua

| Contador local JaCoCo | Coberto / total | Percentual |
|---|---|---|
| Instruções | 3050 / 3524 | 86,55% |
| Branches | 252 / 368 | 68,48% |
| Linhas | 747 / 864 | 86,46% |
| Métodos | 177 / 191 | 92,67% |
| Classes | 26 / 26 | 100% |

Os valores foram extraídos do elemento `report/counter` de `target/site/jacoco/jacoco.xml`. O [resumo dos contadores](e02/cobertura-local.csv) acompanha o relatório. O HTML está em `target/site/jacoco/index.html`.

O `pom.xml` exige `INSTRUCTION / COVEREDRATIO >= 0.75` para o bundle em `verify`. A pipeline executa `mvn --batch-mode --no-transfer-progress clean verify` em Java 21 com serviço PostgreSQL 18 e publica `target/site/jacoco/` no artefato `relatorio-jacoco`, com retenção de 14 dias. A configuração dispara em PRs para `develop`/main e pushes nas branches configuradas.

A CI da versão-base `0ed8b47` concluiu com sucesso. A [execução #101 do PR #58](https://github.com/Miaunisul/Vigor/actions/runs/45170884447), em 23/11/2026, avaliou o commit `9235847cb1557c659acc43b54b55059ddeede382` e aprovou 281 testes, com zero falhas, erros ou ignorados, incluindo os 24 cenários HTTP novos. O XML da CI confirmou os mesmos contadores da tabela local e o gate de 75% foi aprovado. O artefato foi publicado com retenção até 07/12/2026. O [resumo dos contadores da CI](e02/cobertura-ci.csv) está preservado neste PR. [Relatório HTML/XML da CI](https://github.com/Miaunisul/Vigor/actions/runs/45170884447/artifacts/14817580413) · [CI da versão-base](https://github.com/Miaunisul/Vigor/actions/runs/45075396121)

## Requisitos não funcionais

| Verificação existente | Resultado desta revisão |
|---|---|
| VN01 / RNF01 | BCrypt, autenticação, bloqueio de rotas e encerramento de sessão conferidos por testes e percursos acima. A revisão se limita aos controles e cenários executados. |
| VN02 / RNF02 | Quatro consultas autenticadas retornaram HTTP 200, sem timeout de 10 segundos. Tempos de uma observação por consulta, incluindo cliente local, ficaram em 24,87 ms para membros, 34,00 ms para treinos, 21,15 ms para pagamentos e 29,37 ms para atividades. O conjunto tinha quatro membros, um funcionário, um treino, um pagamento e uma atividade. Um membro era o resíduo identificado no teste original. Medições em Windows/loopback, sem carga concorrente. [Medições](e02/tempos-consultas.csv) |
| VN03 / RNF03 | Nos percursos observados, campos, indicação de obrigatoriedade, ações de retorno, mensagens de erro/sucesso e ausência de registros foram compreensíveis. Teclado completo, leitor de tela, diferentes navegadores e janela reduzida ficaram sem execução nesta revisão. |
| VN04 / RNF04 | Banco vazio/migrações, rejeições e relacionamentos conferidos. Reinício real comparado para o membro e pagamento identificados. O resultado não foi estendido às variantes de todas as entidades. |
| VN05 / RNF05 | Inspeção confirmou controller/service/repository/model e templates separados. O Maven compila o projeto. Não foi identificada configuração de ferramenta dedicada de análise estática no POM ou workflow. Encaminhamento E02-L02. |
| VN06 / RNF06 | Suíte local e gate JaCoCo aprovados. A CI #101 confirmou os totais e o gate. A referência e o artefato estão na seção anterior. |

## Falhas, inconsistências e encaminhamentos

| ID | Achado e evidência | Situação e encaminhamento |
|---|---|---|
| E02-T01 | O teste de duplicidade do CPF sempre criava o cadastro inicial sem pontuação. Uma variante repetia a forma original. | Corrigido. O cadastro inicial usa a forma oposta à tentativa duplicada e os dois sentidos são executados. |
| E02-T02 | O teste de edição alterava o nome para “Nome após edição”, perdendo o prefixo usado pelo `AfterEach`. A primeira execução deixou um membro residual. | Corrigido. O novo nome preserva o prefixo exclusivo. A suíte final deixou zero registros nas cinco tabelas de domínio. |
| E02-T03 | Pagamentos e consulta do histórico tinham verificações por camada, mas faltava conferência conjunta pelo HTTP autenticado e PostgreSQL no recorte examinado. | Complementado com os 24 cenários novos. Permanecem as limitações funcionais discriminadas neste relatório. |
| E02-D01 | Plano de testes, seção 9.2, ainda informa que os testes integrados de CPF não foram executados, embora EV17 e os relatórios atuais registrem execução. A matriz e os estados de CT11/CT15 ainda descrevem dependências anteriores à M08. | Encaminhar a Maria Eduarda a associação método → cenário → execução deste relatório e atualizar os estados somente até o que foi comprovado. Preservar o histórico de EV01–EV24. |
| E02-D02 | CA28/CA29 têm vínculos pendentes com casos específicos. Existem testes de serviço/repositório para carga e rejeições, mas falta o procedimento completo e a conversão de entradas da carga técnica. | Maria Eduarda deve consolidar a rastreabilidade com apoio de Millena e Luan e registrar as variantes ainda não executadas. A revisão da tabela de IDs não elimina essa lacuna de conteúdo. |
| E02-D03 | CA30 exige rejeitar referência ausente para consulta de treinos/histórico. As telas atuais oferecem “Todos os membros” e consultam todos quando a seleção está vazia. Os testes da aplicação também exercitam essa consulta geral. | Divergência entre requisito e comportamento registrada. Encaminhar a Maria Eduarda e Millena para decidir o comportamento esperado e então alinhar critério, implementação e testes. Referências malformadas/existência são cenários distintos. |
| E02-D04 | README relaciona as interfaces novas de Millena, mas a tabela de links ainda omite os PRs #42/#46/#52/#54/#56. A contribuição L02 de Luan no PR #41 também está ausente na tabela de acompanhamento. A linha de Emely usa “consolidação”, enquanto #36 atribui a consolidação documental a Maria Eduarda. | Maria Eduarda deve completar os links e delimitar revisão versus consolidação. As autorias verificadas estão abaixo. Não atribuir a implementação ao autor de um merge. |
| E02-D05 | `plano-de-tasks.md` reflete uma divisão inicial diferente das issues atuais. O relatório M07 descreve rotas e totais anteriores à identidade visual e à M08. | Maria Eduarda deve marcar a referência histórica e associar a versão de cada evidência. A primeira execução M06 com ignorados não deve ser convertida retroativamente em execução completa. |
| E02-L01 | CT02/CT07 possuem combinações extensas de campos ausentes/vazios/espaços. CA29 e as verificações de usabilidade abrangentes não foram integralmente executados nesta revisão. | Pendências registradas para Maria Eduarda coordenar a consolidação por variante com os responsáveis técnicos. Não declarar aprovação integral desses CTs pela contagem global. |
| E02-L02 | RNF05 prevê análise estática, mas a configuração atual contém compilação, testes e cobertura JaCoCo. | Encaminhar a Millena e Maria Eduarda a definição/execução da análise estática e a atualização da evidência correspondente. O gate de instruções já aprovado tem evidência própria. |

Os documentos consolidados por Maria Eduarda foram revisados por referência e mantidos como fonte dos identificadores. Os resultados e propostas de ajuste são fornecidos neste relatório de revisão para sua consolidação.

## Conferência de contribuições individuais

As identidades abaixo foram verificadas pelos autores dos PRs e pelo histórico dos arquivos. A autoria do merge pode ser de outra integrante. Arquivos compartilhados preservam contribuições sucessivas.

| Integrante e conta | Contribuições confirmadas e referência |
|---|---|
| Millena Ferreira Rodrigues — `Miaunisul` | Persistência base #26, edição de membros #42, treinos #46/#52, identidade visual de membros #54 e login/histórico #56. O commit `e9d7c42` também adapta os clientes HTTP para sessão e CSRF. |
| Maria Eduarda Souza dos Santos Ferreira — `dudahmesmo` | Requisitos/regras e CPF #16/#37, revisão documental D04 #48, implementação e testes de pagamentos #49 e responsabilidades/README #44/#51. Consolidará os resultados documentais desta revisão. |
| Luan Milian Martins — `Asuramaruu` | Relacionamentos/consultas #38 e testes de persistência dos módulos #41. As complementações posteriores em pagamentos/histórico não transferem a autoria desses testes anteriores. |
| Emely Santos da Silveira — `GreyPompom` | Integração HTTP de CPF e migrações #39. Nesta E02, revisão, correções de dois cenários no arquivo compartilhado, classe HTTP de revisão e organização dos resultados. As funcionalidades implementadas pelas demais integrantes permanecem atribuídas aos seus PRs. |

## Encaminhamento para Maria Eduarda

Os resultados e encaminhamentos foram enviados a `@dudahmesmo` pelo [comentário na issue #36](https://github.com/Miaunisul/Vigor/issues/36#issuecomment-7100512592), com os links da execução #101 e do relatório JaCoCo. O [PR #58](https://github.com/Miaunisul/Vigor/pull/58) também solicita sua revisão. O envio está registrado. O recebimento e a consolidação dependem do acompanhamento de Maria Eduarda.

Para consolidação, utilizar os totais e versões desta revisão, associar os resultados de cada E02-UI aos CTs aplicáveis e tratar os itens E02-D01–E02-D05/E02-L01–E02-L02 com os responsáveis indicados. Manter os registros históricos existentes e a distinção entre aprovado, reprovado, parcialmente verificado e não executado. A decisão sobre CA30 requer alinhamento da equipe.

A entrega desta revisão fornece resultados, correções de testes e encaminhamentos. A aprovação integral de todos os requisitos permanece dependente das lacunas documentadas.
# Arquitetura Geral — VIGOR

## 1. Visão geral

O VIGOR é uma aplicação web para apoiar o cadastro e o gerenciamento de informações de uma academia. Seu usuário interno é o funcionário, que deverá autenticar-se para utilizar as funcionalidades. Os membros são pessoas cadastradas no sistema e não acessam diretamente a aplicação nesta entrega.

O escopo final documentado inclui autenticação de funcionários, cadastro, consulta, atualização e exclusão de membros; cadastro e consulta de treinos; registro de pagamentos recebidos; e consulta do histórico de atividades. A existência desses requisitos não comprova sua implementação. No código atual, o fluxo funcional identificado é o cadastro e a consulta/listagem de membros; as demais funcionalidades estão planejadas.

## 2. Estilo arquitetural

O projeto está sendo organizado como aplicação web baseada em Spring Boot. A DEC03 define como diretriz a separação em camadas de controller, service e repository, para separar entrada, regras de negócio e acesso a dados; ela não afirma que a organização esteja concluída. Há componentes iniciais dessas camadas para membros. Não se atribuem ao sistema outros padrões arquiteturais que não estejam definidos nos documentos ou demonstrados no código.

A estrutura existente ainda é inicial: há uma aplicação Spring Boot e componentes para membros. A organização e a extensão das camadas às demais funcionalidades devem acompanhar sua implementação.

## 3. Tecnologias

| Tecnologia | Responsabilidade e situação |
|---|---|
| Java 21 | Linguagem da aplicação; definida no projeto e indicada em `pom.xml`. |
| Spring Boot | Estrutura principal e configuração da aplicação; presente no projeto. |
| Maven | Build e gerenciamento de dependências; projeto contém `pom.xml` e Maven Wrapper. |
| PostgreSQL | Banco relacional definido para persistência. O driver JDBC e as propriedades locais de conexão estão configurados; a persistência funcional da aplicação ainda precisa ser validada. |
| Thymeleaf | Renderização de páginas HTML no servidor; dependência e templates de membros estão presentes. |
| Bootstrap | Apresentado como tecnologia de interface na stack do projeto. Sua configuração/uso efetivo não foi confirmado na dependência ou estrutura consultada. |
| JUnit 5 | Base para testes automatizados, disponibilizada pela configuração de testes Spring Boot. Há testes em `src/test/java`; a cobertura das funcionalidades previstas ainda está em evolução. |

O código também inclui Spring Web MVC, Spring Data JPA e Jakarta Bean Validation. Esses componentes dão suporte, respectivamente, às rotas web, ao acesso a dados por JPA e à validação declarativa. Mockito está disponível pelo starter de testes. JaCoCo e GitHub Actions aparecem como planejamento nos documentos; sua configuração e resultados não foram confirmados na configuração consultada.

## 4. Organização em camadas

- **Apresentação:** páginas HTML renderizadas por Thymeleaf, com os templates `membros/lista.html` e `membros/formulario.html` existentes. Exibe dados e mensagens ao funcionário. Bootstrap é previsto para a interface, mas seu uso atual não foi confirmado.
- **Controle:** controllers recebem requisições web, vinculam e validam dados de entrada, chamam os serviços e escolhem a página ou redirecionamento de resposta. `MembroController` implementa listagem, apresentação do formulário e envio do cadastro.
- **Serviços:** coordenam operações com repositórios. `MembroService` cadastra, lista e busca por ID; antes do cadastro, verifica CPF por igualdade textual com `existsByCpf` e lança `IllegalArgumentException` em caso de duplicidade. Não implementa ainda as regras completas de CPF normalizado e válido.
- **Domínio:** representa os conceitos e restrições do sistema. `Membro` é uma entidade JPA com ID gerado, data de cadastro inicializada na criação e não atualizável, CPF obrigatório com padrão de 11 dígitos e restrição de unicidade mapeada. Nome também é obrigatório; telefone e endereço têm limites de tamanho, mas não estão marcados como obrigatórios no código, apesar de serem obrigatórios nos requisitos. A validação atual do CPF não verifica dígitos verificadores nem normaliza pontuação. Entidades de funcionários, treinos, pagamentos e atividades não foram encontradas no código atual.
- **Persistência:** `MembroRepository` estende `JpaRepository<Membro, Long>` e declara consultas por CPF. O PostgreSQL está indicado nas propriedades de conexão, mas a gravação e leitura da aplicação no banco ainda não estão validadas.

As camadas são evidentes nos pacotes `controller`, `service`, `model` e `repository` sob `br.edu.unisul.vigor`. A estrutura existente não deve ser interpretada como comprovação de que todos os requisitos já estão atendidos.

## 5. Módulos funcionais

Os módulos abaixo descrevem o escopo definido nos requisitos. Somente cadastro e consulta/listagem inicial de membros têm componentes correspondentes identificados no código consultado.

| Módulo | Comportamento previsto | Situação observada |
|---|---|---|
| Autenticação de funcionários | Validar login e senha de funcionários previamente cadastrados e restringir o acesso interno. Não há tela de cadastro de funcionários nesta entrega. | Planejado; mecanismo de autenticação e proteção de rotas não foram encontrados. |
| Cadastro e consulta de membros | Cadastrar, listar/consultar, atualizar e excluir membros. Os requisitos exigem campos obrigatórios, CPF válido e único, preservação da data/ID em atualizações e bloqueio de exclusão com vínculos. | Existem formulário e listagem, validação declarativa parcial, tentativa de prevenção de duplicidade textual e operações de cadastro/listagem/busca no serviço. A validação integral dos requisitos, atualização e exclusão permanecem pendentes. |
| Gerenciamento de treinos | Cadastrar treino para membro existente e consultar treinos do membro selecionado. | Planejado; sem componentes correspondentes identificados. |
| Registro de pagamentos | Registrar pagamento recebido de membro existente, com valor positivo, data válida e status Pago. | Planejado; sem componentes correspondentes identificados. |
| Histórico de atividades | Consultar atividades de um membro, incluindo atividade, data e tempo. Os dados serão inicialmente disponibilizados por carga técnica documentada; não haverá tela de registro nesta entrega. | Planejado; sem componentes correspondentes identificados. |

Funcionários serão previamente disponibilizados por procedimento técnico documentado, e os registros iniciais de atividades serão carregados tecnicamente, conforme os documentos de requisitos e regras de negócio. A interface não incluirá cadastro/gerenciamento de funcionários nem criação, atualização ou exclusão de atividades nesta entrega.

## 6. Fluxo geral de funcionamento

No fluxo web de membros atualmente implementado, a requisição chega ao `MembroController`. Na listagem, ele obtém os dados pelo serviço e os disponibiliza ao template `membros/lista`. Para cadastrar, apresenta o formulário `membros/formulario`; no envio, recebe o objeto com `@Valid` e verifica os erros de Bean Validation. Havendo erros, retorna o formulário. Caso contrário, chama `MembroService.cadastrar`, que faz a verificação textual de CPF duplicado e chama `MembroRepository.save`; a exceção de duplicidade é associada ao campo CPF. Após sucesso, o controller redireciona à listagem. Esse é o fluxo implementado no código, mas não comprova persistência validada no PostgreSQL.

Os fluxos de autenticação, treinos, pagamentos e atividades deverão ser implementados para cobrir o restante do escopo.

## 7. Persistência

O PostgreSQL foi definido como banco de dados relacional e o projeto inclui o driver JDBC e Spring Data JPA. A decisão técnica registra configuração local com a variável de ambiente `DB_PASSWORD`, evitando manter a senha do banco no código-fonte. O modelo prevê entidades de membros, funcionários, treinos, pagamentos e histórico de atividades, além de relacionamentos e restrições de integridade.

A conexão está configurada em `application.properties` para `localhost:5432/vigor`, usuário `postgres` e senha obtida de `DB_PASSWORD`; o projeto também inclui o driver PostgreSQL e o mapeamento JPA de `Membro`. A DEC04 registra banco local criado e teste de contexto bem-sucedido, mas afirma que a persistência das entidades ainda precisa ser implementada e validada. Portanto, configuração e teste de contexto não comprovam operações funcionais de leitura/gravação no PostgreSQL. Os atributos e relacionamentos previstos estão descritos em [Modelo de Dados](database/modelo-de-dados.md), documento preliminar com pendências de schema.

## 8. Segurança

Conforme RNF01 e as regras RN18–RN20, o acesso às funcionalidades internas deve ser limitado a funcionários autenticados; login inexistente ou senha incorreta devem negar acesso, sem expor informações sensíveis. As senhas de funcionários devem ser armazenadas por hash apropriado, nunca em texto puro. Os funcionários serão previamente cadastrados por procedimento técnico documentado.

Esses controles são requisitos do sistema, não evidência de implementação. No código consultado não foram encontrados componentes de autenticação ou autorização. A credencial do PostgreSQL deve ser fornecida pela variável `DB_PASSWORD`, conforme DEC05, e não versionada. No fluxo atual de membro há validação declarativa parcial; os limites em relação às regras de CPF e aos campos obrigatórios estão detalhados na seção de domínio.

## 9. Testes

JUnit 5, por meio das dependências de teste do Spring Boot, é a base adotada para testes automatizados. Testes unitários verificam comportamentos isolados, como regras do serviço; testes de integração verificam a colaboração entre componentes e, quando configurados, os limites web e de persistência. Existem `VigorApplicationTests`, que verifica o contexto da aplicação, e `MembroServiceTest`, com testes do serviço de membro, em `src/test/java`. A existência desses testes não comprova cobertura integral dos requisitos.

A presença dos arquivos não demonstra que todos os requisitos estejam cobertos ou aprovados. Os cenários devem abranger entradas válidas e inválidas, regras de integridade e funcionalidades conforme implementadas. Os testes de funcionalidades ainda não implementadas permanecem planejados. A geração de cobertura com JaCoCo (mínimo de 75% definido nos requisitos) e sua execução contínua estão planejadas, sem configuração/resultados confirmados nesta análise.

## 10. Referências

- [Requisitos do sistema](../requirements/requisitos.md)
- [Regras de negócio](../requirements/regras-de-negocio.md)
- [Critérios de aceitação](../requirements/criterios-de-aceite.md)
- [Decisões técnicas](decisoes.md)
- [Modelo de dados](database/modelo-de-dados.md)
- [README do projeto](../../README.md)
- Diagramas de arquitetura e de dados: não há arquivos de diagramas no repositório consultado; sua inclusão foi indicada como atividade separada.

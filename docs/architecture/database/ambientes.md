# Ambientes de Banco de Dados — VIGOR

## 1. Objetivo e visão geral

Este documento descreve os ambientes de banco de dados do VIGOR e a configuração pela qual a aplicação se conecta a eles. Distingue a configuração local encontrada no projeto das possibilidades ainda não configuradas, para que a existência de propriedades de conexão não seja confundida com persistência validada.

O projeto utiliza Java 21, Spring Boot, Maven e PostgreSQL. Na configuração consultada há um ambiente local; não foram encontrados arquivos de configuração por perfil ou configuração efetiva de banco remoto.

## 2. Ambiente local

### Pré-requisitos

- JDK 21, conforme `pom.xml` e a documentação do projeto.
- PostgreSQL local acessível pela aplicação.
- Banco de dados `vigor`, registrado como criado no ambiente local em [Decisões técnicas](../decisoes.md).
- Variável de ambiente `DB_PASSWORD` disponível no processo que executa a aplicação.

O projeto inclui Maven Wrapper (`mvnw` e `mvnw.cmd`), PostgreSQL JDBC driver e Spring Data JPA. Não há configuração Docker/Compose identificada.

### Configuração da conexão

O arquivo `src/main/resources/application.properties` contém estas propriedades:

| Propriedade | Configuração atual |
|---|---|
| URL JDBC | `jdbc:postgresql://localhost:5432/vigor` |
| Usuário | `postgres` |
| Senha | Referência à variável de ambiente `DB_PASSWORD` |
| Driver | `org.postgresql.Driver` |

A senha deve ser fornecida no ambiente de execução por `DB_PASSWORD`; seu valor não deve ser incluído no repositório, em código-fonte ou em documentação. O arquivo de propriedades não contém o valor da senha.

### Executar a aplicação

O README confirma o uso do Maven Wrapper para iniciar a aplicação. No Windows, a partir da raiz do projeto:

```bat
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
./mvnw spring-boot:run
```

Antes da execução, o PostgreSQL deve estar disponível com a base e as credenciais correspondentes à configuração. A inicialização do processo não comprova, por si só, que operações de leitura e gravação da aplicação no banco estejam validadas.

### Comportamento JPA atual

O perfil local não está separado em arquivo próprio. Em `application.properties`, `spring.jpa.hibernate.ddl-auto=validate` está configurado: o Hibernate valida o mapeamento sem atualizar o schema. Flyway e a migration inicial estão configurados para executar no início da aplicação. A estratégia de gerenciamento está descrita em [Migrations](migrations.md). A execução efetiva da migration e a compatibilidade com o banco local ainda precisam ser verificadas.

## 3. Ambiente remoto

Neon foi considerado como possibilidade de hospedagem remota, mas não foi encontrada configuração efetiva no projeto. Não há URL, credenciais, variáveis específicas, perfil remoto ou integração com Neon nos arquivos consultados. Portanto, Neon não é um ambiente configurado e não há instruções de conexão remota a registrar neste momento.

Para disponibilizar um ambiente remoto, será necessário decidir o provedor e provisionar a base, definir de forma segura a URL, o usuário e a senha, configurar como essas propriedades serão fornecidas à aplicação e validar conectividade, permissões, schema e persistência. Os valores e nomes de configurações remotas devem ser documentados após essa decisão, sem registrar segredos.

## 4. Configurações e segurança

- Não versionar senhas, tokens ou outras credenciais.
- Não inserir valores sensíveis diretamente no código ou nos arquivos rastreados pelo Git.
- Manter a configuração local existente separada de uma futura configuração remota, cuja forma ainda precisa ser definida.
- Fornecer `DB_PASSWORD` ao processo local por um mecanismo seguro do ambiente utilizado; este documento não define comandos específicos de configuração da variável.
- Antes de disponibilizar credenciais remotas, definir armazenamento e fornecimento seguros das credenciais e confirmar quais configurações devem variar entre ambientes.

Essas orientações são coerentes com a decisão de gerenciamento de credenciais registrada em [Decisões técnicas](../decisoes.md). A configuração atual fixa URL e usuário em `application.properties` e referencia somente a senha por variável de ambiente; não há configuração específica para diferentes ambientes.

## 5. Situação atual e pendências

### Configurado ou registrado

- Java 21, Spring Boot e Maven constam da configuração do projeto.
- O driver PostgreSQL e Spring Data JPA estão declarados no `pom.xml`.
- A conexão local aponta para `localhost:5432/vigor`, com usuário `postgres` e senha via `DB_PASSWORD`.
- A decisão técnica registra PostgreSQL local instalado e banco `vigor` criado.
- Flyway está configurado com uma migration inicial para a tabela `membros`.
- `spring.jpa.hibernate.ddl-auto=validate` está ativo.
- O README documenta a inicialização pelo Maven Wrapper.

Esses itens demonstram configuração e estado registrado; não comprovam persistência funcional nem atendimento integral dos requisitos de banco. Atualmente, `Membro` é a única entidade encontrada no código. O modelo das demais entidades e relacionamentos ainda é previsto, conforme [Modelo de dados](modelo-de-dados.md).

### Ainda pendente

- Validar operações reais de leitura e gravação no PostgreSQL e documentar o resultado.
- Definir se haverá configuração separada por perfil para desenvolvimento e outros ambientes.
- Decidir se Neon ou outro provedor será usado para ambiente remoto; não há configuração remota existente.
- Caso exista ambiente remoto, provisionar a base, definir parâmetros e gestão segura das credenciais, permissões e procedimento de conexão.
- Definir como incorporar ao histórico Flyway um banco local preexistente sem histórico, após comparar e validar o schema atual, conforme [Migrations](migrations.md).
- Validar a execução das migrations e o mapeamento JPA com PostgreSQL nos ambientes pretendidos.
- Definir e validar backup, recuperação, acesso e manutenção dos ambientes conforme forem disponibilizados.

## 6. Referências

- [Decisões técnicas](../decisoes.md)
- [Arquitetura geral](../arquitetura-geral.md)
- [Modelo de dados](modelo-de-dados.md)
- [Estratégia de migrations](migrations.md)
- [README do projeto](../../../README.md)

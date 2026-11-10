# Estratégia de Migrations do Banco de Dados — VIGOR

## 1. Objetivo

As migrations registram, versionam e aplicam de forma ordenada as alterações do schema do banco. Esse histórico permite revisar mudanças, reproduzi-las em diferentes ambientes e entender qual estrutura corresponde a cada versão da aplicação. O controle é especialmente relevante para preservar dados existentes e manter o schema consistente com as entidades e regras do VIGOR.

## 2. Situação atual

O projeto usa PostgreSQL com Spring Data JPA e adotou Flyway para versionar o schema. O `pom.xml` declara o starter do Flyway e o módulo PostgreSQL; a migration inicial `V1__cria_tabela_membros.sql` está em `src/main/resources/db/migration/`.

Em `application.properties`, a conexão está configurada para `jdbc:postgresql://localhost:5432/vigor`, usuário `postgres` e senha obtida da variável `DB_PASSWORD`. A propriedade `spring.jpa.hibernate.ddl-auto=validate` está ativa; o Hibernate valida o mapeamento, sem atualizar o schema automaticamente. Flyway executa as migrations encontradas no local padrão durante a inicialização da aplicação.

No código atual, a única entidade persistente encontrada é `Membro`, mapeada para a tabela `membros`, com ID gerado e restrição de unicidade para CPF. O repositório correspondente é `MembroRepository`. As demais entidades e relações pertencem ao modelo previsto e ainda não foram implementadas. A configuração de Flyway e a existência da migration não comprovam, por si sós, sua execução nem operações funcionais de leitura e gravação no PostgreSQL.

## 3. Estratégia de migrations

Flyway foi adotado como mecanismo de versionamento do schema e integrado ao projeto. A migration inicial cria a tabela `membros`; futuras alterações deverão ser registradas em novas migrations versionadas. O Hibernate está configurado com `ddl-auto=validate`, evitando que concorra com Flyway na alteração do schema.

A estratégia de transição para um banco local preexistente sem histórico Flyway ainda precisa ser definida. Não se deve presumir que executar Flyway sobre esse banco crie ou registre o histórico com segurança automaticamente.

## 4. Organização dos arquivos

Estrutura utilizada para as migrations:

```text
src/main/resources/
└── db/
    └── migration/
        ├── V1__descricao_da_alteracao.sql
        └── V2__descricao_da_alteracao.sql
```

`V1__cria_tabela_membros.sql` é a migration inicial existente. Os scripts devem conter apenas alterações justificadas e alinhadas ao modelo de dados aprovado.

## 5. Padrão de versionamento

As migrations versionadas usam o formato `V<versão>__<descricao_curta>.sql`: prefixo `V`, versão numérica, dois sublinhados e descrição curta em letras minúsculas com palavras separadas por sublinhado. A migration inicial é `V1__cria_tabela_membros.sql`.

As versões devem ser únicas e crescentes. O Flyway aplica migrations pendentes na ordem de versão e registra as já executadas em seu histórico no banco. Scripts versionados já aplicados não devem ser editados para alterar seu efeito; correções devem ser registradas em uma nova migration, preservando o histórico.

## 6. Cuidados de integridade

- Cada alteração deve respeitar o [modelo de dados](modelo-de-dados.md) e as regras de negócio, sem introduzir entidades, colunas ou relações não aprovadas.
- Antes de adicionar restrições de obrigatoriedade, unicidade ou referências, verificar os dados já armazenados e planejar como tratar valores incompatíveis. Não descartar ou transformar dados sem decisão documentada.
- A migration inicial cria somente a tabela `membros`, com os atributos e restrições de nulidade e unicidade definidos no mapeamento atual e no modelo aprovado. A criação de chaves estrangeiras deve aguardar a definição dos relacionamentos, atributos e multiplicidades pendentes no [modelo de dados](modelo-de-dados.md). A migration deve verificar a integridade dos dados existentes antes de ativar novas restrições.
- A regra de negócio exige bloquear a exclusão de membro quando existirem treinos, pagamentos ou atividades vinculados. A estratégia específica no banco e na aplicação, incluindo qualquer ação referencial de exclusão, ainda precisa ser decidida; migrations não devem introduzir exclusão em cascata que viole essa regra.
- Planejar backup e possibilidade de recuperação antes de alterações que possam afetar dados. Alterações destrutivas ou irreversíveis exigem revisão explícita e procedimento de recuperação adequado ao ambiente.
- Scripts devem ser revisados quanto a compatibilidade com PostgreSQL e aplicados primeiro em ambiente não produtivo com dados representativos, quando disponível.

## 7. Fluxo de atualização

Para uma mudança de schema, o fluxo é:

1. Identificar a necessidade e conferir sua coerência com requisitos, regras de negócio e modelo de dados.
2. Registrar a mudança proposta e seus impactos, inclusive sobre dados existentes e compatibilidade com a versão da aplicação.
3. Criar uma nova migration versionada, sem alterar scripts que já tenham sido aplicados em ambientes compartilhados.
4. Revisar a migration em conjunto com as alterações de entidades/código e os testes correspondentes.
5. Aplicar e verificar a migration em desenvolvimento e nos demais ambientes pela mesma sequência versionada; registrar a versão, o ambiente e o resultado.
6. Planejar backup e recuperação para o ambiente de destino, e não executar alteração de produção sem validação e procedimento operacional definidos.

O fluxo de execução automática no início da aplicação está configurado. A aplicação da migration inicial em bancos existentes e a validação em cada ambiente ainda precisam ser verificadas.

## 8. Pendências

- Validar a execução da migration inicial em banco PostgreSQL vazio e a validação do mapeamento pelo Hibernate.
- Determinar como será adotado o histórico de migrations em banco local que já exista sem histórico Flyway, incluindo eventual baseline e conferência do schema atual; não executar baseline sem comparação e validação.
- Confirmar entidades, atributos, tipos, constraints e relacionamentos no modelo de dados antes de escrever migrations correspondentes. A definição dos relacionamentos continua pendente nesse modelo.
- Definir a forma de aplicar migrations nos ambientes e a política de backup, recuperação e revisão.
- Validar as operações de leitura e gravação após a aplicação da migration e documentar os resultados nos ambientes pretendidos.

## Referências

- [Modelo de dados](modelo-de-dados.md)
- [Decisões técnicas](../decisoes.md)
- [Arquitetura geral](../arquitetura-geral.md)

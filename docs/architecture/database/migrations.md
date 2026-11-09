# Estratégia de Migrations do Banco de Dados — VIGOR

## 1. Objetivo

As migrations registram, versionam e aplicam de forma ordenada as alterações do schema do banco. Esse histórico permite revisar mudanças, reproduzi-las em diferentes ambientes e entender qual estrutura corresponde a cada versão da aplicação. O controle é especialmente relevante para preservar dados existentes e manter o schema consistente com as entidades e regras do VIGOR.

## 2. Situação atual

O projeto usa PostgreSQL com Spring Data JPA. O `pom.xml` contém o driver PostgreSQL e as dependências JPA, mas não declara Flyway, Liquibase ou outra ferramenta de migrations. Também não há arquivos de migration na estrutura atual de `src/main/resources`.

Em `application.properties`, a conexão está configurada para `jdbc:postgresql://localhost:5432/vigor`, usuário `postgres` e senha obtida da variável `DB_PASSWORD`. A propriedade `spring.jpa.hibernate.ddl-auto=update` está ativa; portanto, o Hibernate está configurado para atualizar o schema a partir do mapeamento JPA durante a execução. Isso não corresponde a um histórico versionado de migrations e não substitui uma estratégia revisável de evolução do banco.

No código atual, a única entidade persistente encontrada é `Membro`, mapeada para a tabela `membros`, com ID gerado e restrição de unicidade para CPF. O repositório correspondente é `MembroRepository`. As demais entidades e relações pertencem ao modelo previsto e ainda não foram implementadas. A configuração e o teste de contexto registrados nas decisões não comprovam leitura e gravação funcionais das entidades no PostgreSQL.

## 3. Estratégia de migrations

Não foi encontrada ferramenta de migrations configurada. Como estratégia proposta, o projeto pode adotar **Flyway** e manter scripts SQL versionados no repositório. Essa proposta ainda não foi aprovada nem implementada: Flyway não consta no `pom.xml`, não há scripts SQL versionados e nenhuma execução de migrations está configurada.

Caso a equipe adote Flyway, ele deverá ser a fonte explícita e revisável das alterações de schema. A configuração e o modo de transição a partir do atual `ddl-auto=update` precisam ser definidos antes de habilitar essa abordagem. Não se deve presumir que ativar Flyway sobre um banco já existente o inicialize ou o versione com segurança automaticamente.

## 4. Organização dos arquivos

Estrutura sugerida, caso Flyway seja adotado:

```text
src/main/resources/
└── db/
    └── migration/
        ├── V1__descricao_da_alteracao.sql
        └── V2__descricao_da_alteracao.sql
```

Os nomes são exemplos da convenção, não migrations existentes. Os scripts devem conter apenas alterações justificadas e alinhadas ao modelo de dados aprovado. Esta documentação não cria nem define o conteúdo de scripts.

## 5. Padrão de versionamento

Na convenção proposta do Flyway, migrations versionadas usam o formato `V<versão>__<descricao_curta>.sql`: prefixo `V`, versão numérica, dois sublinhados e descrição curta em letras minúsculas com palavras separadas por sublinhado. Por exemplo, `V1__descricao_da_alteracao.sql` ilustra apenas o formato.

As versões devem ser únicas e crescentes. O Flyway aplica migrations pendentes na ordem de versão e registra as já executadas em seu histórico no banco. Scripts versionados já aplicados não devem ser editados para alterar seu efeito; correções devem ser registradas em uma nova migration, preservando o histórico. A convenção e suas opções ainda dependem da adoção e configuração da ferramenta.

## 6. Cuidados de integridade

- Cada alteração deve respeitar o [modelo de dados](modelo-de-dados.md) e as regras de negócio, sem introduzir entidades, colunas ou relações não aprovadas.
- Antes de adicionar restrições de obrigatoriedade, unicidade ou referências, verificar os dados já armazenados e planejar como tratar valores incompatíveis. Não descartar ou transformar dados sem decisão documentada.
- A criação de chaves estrangeiras deve aguardar a definição dos relacionamentos, atributos e multiplicidades pendentes no [modelo de dados](modelo-de-dados.md). A migration deve verificar a integridade dos dados que já existam antes de ativar tais restrições.
- A regra de negócio exige bloquear a exclusão de membro quando existirem treinos, pagamentos ou atividades vinculados. A estratégia específica no banco e na aplicação, incluindo qualquer ação referencial de exclusão, ainda precisa ser decidida; migrations não devem introduzir exclusão em cascata que viole essa regra.
- Planejar backup e possibilidade de recuperação antes de alterações que possam afetar dados. Alterações destrutivas ou irreversíveis exigem revisão explícita e procedimento de recuperação adequado ao ambiente.
- Scripts devem ser revisados quanto a compatibilidade com PostgreSQL e aplicados primeiro em ambiente não produtivo com dados representativos, quando disponível.

## 7. Fluxo de atualização

Com a estratégia de migrations aprovada e implementada, o fluxo proposto para uma mudança de schema é:

1. Identificar a necessidade e conferir sua coerência com requisitos, regras de negócio e modelo de dados.
2. Registrar a mudança proposta e seus impactos, inclusive sobre dados existentes e compatibilidade com a versão da aplicação.
3. Criar uma nova migration versionada, sem alterar scripts que já tenham sido aplicados em ambientes compartilhados.
4. Revisar a migration em conjunto com as alterações de entidades/código e os testes correspondentes.
5. Aplicar e verificar a migration em desenvolvimento e nos demais ambientes pela mesma sequência versionada; registrar a versão, o ambiente e o resultado.
6. Planejar backup e recuperação para o ambiente de destino, e não executar alteração de produção sem validação e procedimento operacional definidos.

Até que Flyway ou outra ferramenta seja aprovada e configurada, esse fluxo é uma proposta documental, não um procedimento executável no projeto.

## 8. Pendências

- Decidir e registrar formalmente a ferramenta de migrations e sua configuração.
- Definir a estratégia de transição do `spring.jpa.hibernate.ddl-auto=update` para schema controlado por migrations, evitando concorrência entre mecanismos.
- Determinar como será adotado o histórico de migrations em banco local que já exista, incluindo a eventual baseline e a conferência do schema atual; não executar baseline sem comparação e validação.
- Confirmar entidades, atributos, tipos, constraints e relacionamentos no modelo de dados antes de escrever migrations correspondentes. A definição dos relacionamentos continua pendente nesse modelo.
- Definir a forma de aplicar migrations nos ambientes e a política de backup, recuperação e revisão.
- Implementar a ferramenta e os scripts necessários e validar sua execução nos ambientes pretendidos. Nenhuma migration SQL foi criada nesta tarefa.

## Referências

- [Modelo de dados](modelo-de-dados.md)
- [Decisões técnicas](../decisoes.md)
- [Arquitetura geral](../arquitetura-geral.md)

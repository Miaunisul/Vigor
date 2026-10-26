# VIGOR — Modelo de Dados

## 1. Objetivo

Documentar a estrutura inicial dos dados utilizados no cadastro e na consulta de alunos do VIGOR.

Este modelo é preliminar e deverá ser atualizado quando os campos definitivos do cadastro forem confirmados.

## 2. Entidade Aluno

A entidade `Aluno` representa uma pessoa cadastrada na academia.

| Campo | Tipo lógico sugerido | Obrigatório | Regra |
|---|---|---|---|
| id | Identificador numérico | Sim | Identificador único do registro |
| nome | Texto | Sim | Não pode ficar vazio |
| cpf | Texto | Sim | Deve ser válido e único |
| email | Texto | A confirmar | Se adotado como obrigatório, deve ser validado |
| telefone | Texto | A confirmar | Armazenado como texto para preservar DDD e zeros |
| dataNascimento | Data | A confirmar | Deve representar uma data válida |
| dataCadastro | Data e hora | Sim | Registra quando o aluno foi cadastrado |

Os campos opcionais e seus formatos definitivos devem ser confirmados antes da implementação.

## 3. Relacionamentos

Na versão inicial, a entidade `Aluno` não precisa de relacionamentos obrigatórios com outras entidades para permitir o cadastro e a consulta básicos.

Novas entidades ou relacionamentos só deverão ser incluídos quando forem necessários ao escopo aprovado.

## 4. Restrições de integridade

- Cada aluno deve possuir um identificador único.
- O CPF deve ser armazenado de maneira consistente.
- Não podem existir dois alunos com o mesmo CPF.
- Campos definidos como obrigatórios não podem ser nulos ou vazios.
- A validação realizada pela aplicação deve ser complementada por restrições adequadas no banco de dados.

## 5. Persistência

A tecnologia de banco prevista para o projeto é PostgreSQL, com acesso aos dados por meio do Spring Data JPA.

A configuração efetiva, o mapeamento da entidade e as restrições serão documentados após a implementação e os testes.

## 6. Pontos pendentes

- Confirmar todos os campos obrigatórios do cadastro.
- Confirmar se email e telefone serão obrigatórios.
- Definir o tratamento de CPFs com pontuação e sem pontuação.
- Definir se haverá edição ou inativação de alunos no MVP.
- Confirmar a estratégia de criação e atualização do schema do banco.

## 7. Situação do documento

Modelo preliminar. A estrutura deve ser revisada antes de ser considerada definitiva e precisa corresponder à entidade implementada no código.
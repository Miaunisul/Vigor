# VIGOR — Regras de Negócio

## 1. Objetivo

Este documento define as regras que o sistema VIGOR deve respeitar durante o cadastro e a consulta de alunos.

As regras servem como referência para a implementação e para a elaboração dos testes automatizados.

## 2. Regras

### RN01 — Campos obrigatórios

O sistema deve verificar se todos os campos definidos como obrigatórios foram preenchidos antes de aceitar um cadastro.

**Resultado esperado:** se faltar algum campo obrigatório, o sistema deve rejeitar o cadastro e informar o usuário.

### RN02 — Validação do CPF

O CPF informado deve possuir formato válido e dígitos verificadores válidos.

**Resultado esperado:** um CPF inválido deve ser rejeitado, sem criação de registro.

### RN03 — CPF único

Cada aluno deve possuir um CPF único no sistema.

**Resultado esperado:** se já existir um aluno com o CPF informado, o sistema deve impedir um segundo cadastro.

### RN04 — Não persistir cadastros inválidos

Dados que não atendam às regras de validação não devem ser gravados como um novo aluno.

**Resultado esperado:** após uma tentativa inválida, não deve existir um novo registro correspondente à tentativa.

### RN05 — Consulta de alunos cadastrados

Os alunos registrados devem poder ser consultados posteriormente.

**Resultado esperado:** ao consultar um aluno cadastrado, o sistema deve apresentar os dados armazenados de forma consistente.

### RN06 — Consistência dos dados

Os dados apresentados na consulta devem corresponder aos dados efetivamente armazenados, respeitando as validações aplicadas no cadastro.

**Resultado esperado:** a consulta não deve apresentar informações diferentes das persistidas sem que exista uma operação autorizada que as altere.

## 3. Relação com os testes

| Regra | Cenário mínimo de teste |
|---|---|
| RN01 | Tentar cadastrar um aluno sem preencher um campo obrigatório |
| RN02 | Tentar cadastrar um aluno com CPF inválido |
| RN03 | Tentar cadastrar dois alunos com o mesmo CPF |
| RN04 | Verificar que uma tentativa inválida não cria registro |
| RN05 | Cadastrar um aluno válido e consultá-lo posteriormente |
| RN06 | Comparar os dados apresentados com os dados cadastrados |

## 4. Observações

- Os campos obrigatórios definitivos serão estabelecidos no modelo de dados.
- A validação do CPF deve considerar os dígitos verificadores, e não apenas o formato.
- A prevenção de duplicidade deve ser tratada na aplicação e reforçada no banco de dados.
- As regras devem ser revisadas caso o escopo do MVP seja alterado.

## 5. Situação do documento

Documento inicial. As regras devem ser confirmadas com os requisitos do projeto e verificadas por meio dos testes automatizados.
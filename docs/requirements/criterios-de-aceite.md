# VIGOR — Critérios de Aceite

## 1. Objetivo

Definir as condições que devem ser verificadas para considerar os requisitos do MVP atendidos. Cada critério deverá ser validado por meio de testes ou execução documentada.

## 2. Critérios de aceite

### CA01 — Cadastro válido
**Requisito relacionado:** RF01

**Dado** que o usuário preenche todos os campos obrigatórios com dados válidos,  
**quando** solicita o cadastro,  
**então** o sistema deve registrar o aluno e informar que a operação foi concluída.

### CA02 — Campo obrigatório ausente
**Requisito relacionado:** RF02

**Dado** que existe pelo menos um campo obrigatório não preenchido,  
**quando** o usuário tenta cadastrar o aluno,  
**então** o sistema deve rejeitar a operação e indicar o campo que precisa ser preenchido.

### CA03 — CPF inválido
**Requisito relacionado:** RF02

**Dado** que o usuário informa um CPF inválido,  
**quando** tenta concluir o cadastro,  
**então** o sistema deve rejeitar a operação e não criar um novo registro.

### CA04 — CPF duplicado
**Requisito relacionado:** RF03

**Dado** que já existe um aluno cadastrado com determinado CPF,  
**quando** o usuário tenta cadastrar outro aluno com o mesmo CPF,  
**então** o sistema deve impedir a duplicidade e informar o motivo.

### CA05 — Consulta de aluno
**Requisito relacionado:** RF04

**Dado** que existe um aluno cadastrado,  
**quando** o usuário realiza a consulta correspondente,  
**então** o sistema deve apresentar os dados do aluno armazenados.

### CA06 — Persistência
**Requisito relacionado:** RF05

**Dado** que um aluno foi cadastrado com sucesso,  
**quando** o registro é consultado novamente,  
**então** os dados devem continuar disponíveis e consistentes com o que foi salvo.

## 3. Registro dos resultados

Para cada critério, registrar:

- Resultado esperado.
- Resultado obtido.
- Situação: aprovado, reprovado ou não executado.
- Evidência ou referência ao teste correspondente.

## 4. Observações

- Os critérios devem ser ajustados caso os requisitos ou os campos obrigatórios sejam alterados.
- Um critério só pode ser marcado como aprovado depois de ser efetivamente verificado.
- Os testes automatizados deverão cobrir as regras críticas de validação e prevenção de duplicidade.

## 5. Situação do documento

Documento inicial. Os critérios serão utilizados como referência para os testes funcionais, unitários e de integração do VIGOR.
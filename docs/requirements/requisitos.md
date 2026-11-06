# Requisitos do Sistema VIGOR

## 1. Objetivo

Documentar os requisitos funcionais e não funcionais do VIGOR, sistema web de gestão de academia desenvolvido para a A3 de Gestão e Qualidade de Software da UNISUL.

Este documento descreve o comportamento previsto para a entrega. A presença de um requisito não significa que ele já esteja implementado ou testado.

## 2. Ator e escopo da entrega

O funcionário da academia é o usuário da aplicação e acessa as funcionalidades internas mediante autenticação.

O membro é a pessoa cadastrada e vinculada aos treinos, pagamentos e registros de atividades. Não possui acesso direto à aplicação nesta entrega.

O escopo contempla:

- Autenticação de funcionários.
- Cadastro, consulta, atualização e exclusão de membros.
- Cadastro e consulta de treinos.
- Registro de pagamentos.
- Consulta do histórico de atividades.

Todos os requisitos funcionais RF01 a RF09 integram a entrega final da A3. As etapas de implementação indicam somente a ordem de desenvolvimento; autenticação, exclusão de membros, treinos, pagamentos e consulta do histórico não estão fora do escopo final.

A atualização e a exclusão são operações previstas para membros. Para treinos, as operações previstas são cadastro e consulta.

O registro de atividades pela interface está fora desta entrega, conforme CA13. A consulta do histórico permanece obrigatória na entrega final, conforme RF09.

A consulta do histórico utilizará registros previamente disponibilizados no banco de dados. Os funcionários também serão previamente cadastrados por procedimento técnico documentado. Esses procedimentos não exigem novas telas.

As decisões de implementação estão explicitadas em `regras-de-negocio.md` e devem ser revisadas pela equipe no Pull Request.



## 3. Requisitos funcionais

| ID | Requisito | Especificação |
|---|---|---|
| RF01 | Autenticar funcionários | O sistema deve permitir a autenticação de funcionários previamente cadastrados mediante login e senha, restringindo as funcionalidades internas a usuários autenticados. |
| RF02 | Cadastrar membros | O sistema deve permitir cadastrar membros com nome, CPF, telefone e endereço, gerando automaticamente o identificador e a data de cadastro. |
| RF03 | Consultar membros | O sistema deve permitir listar os membros cadastrados e consultar suas informações. |
| RF04 | Atualizar membros | O sistema deve permitir atualizar nome, CPF, telefone e endereço de um membro, validando os novos dados antes de salvá-los. |
| RF05 | Excluir membros | O sistema deve permitir excluir membros sem registros vinculados e bloquear a exclusão quando houver treinos, pagamentos ou atividades associados. |
| RF06 | Cadastrar treinos | O sistema deve permitir cadastrar um treino associado a um membro existente, informando tipo, descrição, duração em minutos e data de início. |
| RF07 | Consultar treinos | O sistema deve permitir consultar os treinos associados a um membro, apresentando os dados cadastrados. |
| RF08 | Registrar pagamentos | O sistema deve permitir registrar um pagamento recebido de um membro existente, informando valor e data do pagamento e atribuindo o status Pago. |
| RF09 | Consultar histórico de atividades | O sistema deve permitir consultar as atividades registradas para um membro, apresentando atividade, data e tempo de execução. |

## 4. Requisitos não funcionais

| ID | Requisito | Especificação |
|---|---|---|
| RNF01 | Segurança | O sistema deve restringir o acesso às funcionalidades internas a funcionários autenticados e armazenar senhas por meio de hash apropriado para senhas, sem persistência em texto puro. |
| RNF02 | Desempenho | O sistema deve concluir os fluxos previstos sem travamentos ou erros de tempo limite no ambiente de testes documentado. Os tempos observados nas consultas devem ser registrados. |
| RNF03 | Usabilidade | O sistema deve apresentar campos identificados, indicação de obrigatoriedade e mensagens claras de validação, sucesso e ausência de registros. Os fluxos principais devem passar por avaliação de usabilidade. |
| RNF04 | Integridade e persistência | O sistema deve persistir os dados válidos no PostgreSQL, mantê-los disponíveis após reiniciar a aplicação e impedir gravações que violem as regras de validação e os relacionamentos. |
| RNF05 | Manutenibilidade | O código deve separar as responsabilidades de apresentação, regras de negócio e persistência, permitindo localizar e alterar cada responsabilidade e executar análise estática. |
| RNF06 | Testabilidade | O projeto deve permitir executar testes automatizados com Maven, contemplando cenários válidos e inválidos, gerar relatório de cobertura com JaCoCo e executar os testes na integração contínua. |

O RNF02 não estabelece um limite numérico de tempo de resposta, pois esse limite ainda não foi definido. Os resultados devem indicar o ambiente, o volume de dados e os tempos medidos, sem alegar atendimento a uma meta numérica inexistente.

## 5. Dados previstos

| Entidade | Dados |
|---|---|
| Membro | ID, nome, CPF, telefone, endereço e data de cadastro. |
| Treino | ID, referência ao membro, tipo, descrição, duração e data de início. |
| Pagamento | ID, referência ao membro, valor, data do pagamento e status. |
| Histórico de atividades | ID, referência ao membro, atividade, data, tempo de execução e referência ao funcionário responsável. |
| Funcionário | ID, nome, cargo, login e hash da senha. |

O endereço pertence ao cadastro de membros. Não é previsto endereço para funcionários.

A referência ao funcionário responsável representa o relacionamento entre funcionários e atividades.

O detalhamento das entidades e dos relacionamentos deve permanecer consistente com `docs/architecture/modelo-de-dados.md`.

## 6. Critérios de aceitação e rastreabilidade

Os critérios de aceitação são mantidos em documento próprio, preservando seus identificadores.

A consistência entre requisitos funcionais, requisitos não funcionais, regras de negócio e critérios de aceitação deve ser conferida sempre que houver alterações.

A matriz de rastreabilidade deve relacionar os requisitos às regras aplicáveis, aos critérios de aceitação e aos testes ou verificações correspondentes.

A existência de um critério documentado não significa que ele já tenha sido implementado, testado ou aprovado.


## 7. Funcionalidades fora desta entrega

Os itens abaixo não fazem parte dos critérios de aceitação desta entrega. São possibilidades futuras, sem compromisso de implementação:

- Acesso direto de membros ao sistema.
- Perfil de gestor com permissões específicas.
- Tela de cadastro e gerenciamento de funcionários.
- Atualização e exclusão de treinos.
- Tela de registro, atualização e exclusão de atividades.
- Consulta financeira específica, edição e exclusão de pagamentos.
- Controle de pendências, vencimentos, inadimplência e cobranças.
- Integração com Pix, boletos ou outros meios de pagamento.
- Dashboard, gráficos e relatórios gerenciais.
- Recuperação de senha e cadastro público de usuários.
- Busca textual, filtros avançados e paginação.
- Aplicativo móvel separado.

A seleção de um membro para consultar treinos ou atividades faz parte do escopo e não constitui filtro avançado.

A adaptação visual da aplicação web a diferentes tamanhos de tela é uma escolha técnica da equipe. Não representa a criação de um aplicativo móvel separado nem deve ser apresentada como exigência específica do professor sem confirmação.

## 8. Tecnologias adotadas

- Linguagem: Java 21.
- Framework: Spring Boot.
- Interface: Thymeleaf, HTML, Bootstrap e CSS.
- Banco de dados: PostgreSQL.
- Persistência: Spring Data JPA.
- Validação: Bean Validation e validações de negócio.
- Testes: JUnit 5, Mockito e MockMvc, conforme o tipo de teste.
- Cobertura: JaCoCo.
- Build e dependências: Maven.
- Integração contínua: GitHub Actions.
- Controle de versão: Git e GitHub.

A ferramenta de análise estática e seu quality gate devem ser documentados na configuração de qualidade do projeto.

## 9. Planejamento de testes e evidências

A verificação do projeto deve contemplar testes unitários, de integração, de validação, de sistema e de usabilidade, conforme o plano de testes.

Os testes automatizados devem permanecer em `src/test/java`. Os planos, resultados e referências às evidências devem ser registrados em `docs/testing`.

A rastreabilidade deve seguir:

Requisito → Regra de negócio aplicável → Critério de aceitação → Teste ou verificação correspondente.

Quando não houver uma regra de negócio específica, o requisito deve ser relacionado diretamente ao critério de aceitação e à verificação correspondente.

Nem todo requisito exige um teste unitário. Interface, usabilidade, análise estática e integração contínua podem exigir outras formas de verificação.

Não é obrigatório criar um teste por regra: um teste pode verificar várias regras, e uma regra pode exigir vários cenários.

Enquanto uma funcionalidade não estiver implementada, seus testes devem ser identificados como planejados. Não devem ser apresentados resultados simulados como evidência de execução.

Esta atualização documental não implementa funcionalidades nem comprova a aprovação dos critérios de aceitação.

A cobertura de código deve atingir o mínimo de 75% previsto para o projeto, com relatório gerado pelo JaCoCo. A análise estática e a aprovação no quality gate devem ter suas evidências registradas em `docs/testing`.
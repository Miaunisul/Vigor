# VIGOR — Requisitos do Sistema

## 1. Objetivo

O VIGOR é um sistema de cadastro e consulta de alunos para uma academia, desenvolvido como projeto acadêmico da A3 de Gestão e Qualidade de Software.

O sistema tem como objetivo organizar os dados dos alunos, permitindo que sejam cadastrados e posteriormente consultados de maneira consistente.

## 2. Escopo do MVP

A primeira versão do sistema prioriza o cadastro e a consulta de alunos. Funcionalidades adicionais serão consideradas conforme os requisitos definidos pela equipe e o escopo acordado com o professor.

## 3. Atores

- **Funcionário da academia:** utiliza o sistema para cadastrar e consultar alunos.
- **Responsável pela gestão:** acompanha os registros e verifica a consistência dos dados.

Os perfis acima representam os atores previstos para o MVP; permissões específicas ainda precisam ser definidas.

## 4. Requisitos funcionais

- **RF01 — Cadastrar aluno:** o sistema deve permitir registrar um aluno mediante o preenchimento dos campos obrigatórios.
- **RF02 — Validar dados:** o sistema deve verificar os dados obrigatórios e validar o CPF informado.
- **RF03 — Impedir duplicidade:** o sistema não deve permitir mais de um cadastro com o mesmo CPF.
- **RF04 — Consultar alunos:** o sistema deve permitir localizar e visualizar alunos cadastrados.
- **RF05 — Persistir registros:** os cadastros válidos devem ser armazenados para consulta posterior.

## 5. Requisitos não funcionais

- **RNF01 — Usabilidade:** as telas devem apresentar campos e mensagens compreensíveis.
- **RNF02 — Integridade:** dados inválidos ou cadastros duplicados não devem gerar registros indevidos.
- **RNF03 — Manutenibilidade:** o código deve ser organizado em componentes com responsabilidades definidas.
- **RNF04 — Testabilidade:** as regras de negócio devem permitir a execução de testes automatizados.
- **RNF05 — Reprodutibilidade:** o projeto deve documentar os procedimentos necessários para executar a aplicação e os testes.

## 6. Restrições e pontos pendentes

- O MVP deve priorizar cadastro e consulta de alunos.
- A tecnologia e a estrutura técnica devem ser registradas na documentação de arquitetura.
- A inclusão de planos de treino, pagamentos e histórico depende de confirmação do escopo com o professor.
- Os campos definitivos do cadastro serão documentados após a definição do modelo de dados.

## 7. Relação com os testes

Os requisitos RF02 e RF03 devem ser verificados por testes automatizados de validação de CPF e prevenção de duplicidade. RF01, RF04 e RF05 também deverão ser verificados durante os testes funcionais e de integração.

## 8. Situação do documento

Documento inicial de requisitos. Deve ser atualizado quando houver decisões de escopo ou alterações aprovadas pela equipe.
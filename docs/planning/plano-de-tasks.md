# VIGOR — Plano de Tasks da Equipe

## 1. Objetivo

Organizar as atividades de desenvolvimento, documentação, testes e validação do VIGOR, definindo responsáveis, dependências e critérios de conclusão.

Este planejamento pode ser atualizado conforme o progresso real do projeto e os alinhamentos com o professor.

## 2. Responsabilidades

| Integrante | Foco principal | Tasks principais |
|---|---|---|
| Millena Ferreira Rodrigues | Interface web, banco de dados e integração | M01–M05; participação em E02, E03 e E05 |
| Emely Santos da Silveira | Backend, regras técnicas, testes e qualidade | E01–E07; participação em E02, E03 e E05 |
| Maria Eduarda Souza dos Santos Ferreira | Requisitos, regras de negócio e validação de uso | M01, M02, D01, D02 e participação em L02/T01 |
| Luan Milian Martins | Diagramas, documentação técnica e evidências | Apoio em M03, L01–L03 e participação em L02/T01 |

As responsabilidades compartilhadas devem ser combinadas entre os envolvidos. Cada integrante deve registrar sua contribuição real.

## 3. Backlog

### M01 — Confirmar escopo e requisitos
**Responsáveis:** Millena e Maria Eduarda  
**Dependências:** nenhuma  
**Critério de conclusão:** requisitos priorizados, dúvidas abertas registradas e critérios de aceite revisados.

### M02 — Mapear fluxo de cadastro e consulta
**Responsáveis:** Maria Eduarda e Millena  
**Dependências:** M01  
**Critério de conclusão:** fluxo documentado, incluindo cadastro válido, erros de validação e consulta.

### M03 — Revisar modelo de domínio e dados
**Responsáveis:** Millena e Luan  
**Dependências:** M01  
**Critério de conclusão:** modelo de dados e dicionário coerentes com o escopo.

### M04 — Preparar interface web
**Responsável:** Millena  
**Dependências:** M01 e M02  
**Critério de conclusão:** telas de cadastro e consulta navegáveis, com mensagens claras e layout responsivo.

### M05 — Preparar PostgreSQL e persistência
**Responsável:** Millena  
**Dependências:** M03  
**Critério de conclusão:** aplicação salva e recupera alunos no banco de desenvolvimento, sem credenciais versionadas.

### E01 — Estrutura Spring Boot e arquitetura mínima
**Responsável:** Emely  
**Dependências:** M01 e M03  
**Critério de conclusão:** projeto compila e possui camadas com responsabilidades claras.

### E02 — Regras de negócio e validação
**Responsáveis:** Emely e Millena  
**Dependências:** M03 e E01  
**Critério de conclusão:** campos obrigatórios, CPF válido e CPF único são verificados; dados inválidos não são persistidos.

### E03 — Integração do fluxo web com backend
**Responsáveis:** Millena e Emely  
**Dependências:** M04, M05, E01 e E02  
**Critério de conclusão:** cadastro e consulta funcionam ponta a ponta localmente.

### E04 — Testes unitários
**Responsável:** Emely  
**Dependências:** E02  
**Critério de conclusão:** testes automatizados verificam os resultados esperados para as regras críticas.

### E05 — Testes de integração e persistência
**Responsáveis:** Emely e Millena  
**Dependências:** M05 e E02  
**Critério de conclusão:** cadastro, consulta, duplicidade e persistência são testados em ambiente controlado.

### E06 — Cobertura JaCoCo
**Responsável:** Emely  
**Dependências:** E04 e E05  
**Critério de conclusão:** relatório de cobertura gerado e percentual real registrado. Meta do projeto: pelo menos 75%.

### E07 — GitHub Actions / CI
**Responsável:** Emely  
**Dependências:** E01 e E04  
**Critério de conclusão:** workflow executado no GitHub para build e testes, com resultado verificável.

### D01 — Documentar regras e critérios de aceite
**Responsável:** Maria Eduarda  
**Dependências:** M01 e M02  
**Critério de conclusão:** regras e critérios verificáveis, coerentes com os requisitos.

### D02 — Revisar formulários e mensagens
**Responsável:** Maria Eduarda  
**Dependências:** M02 e M04  
**Critério de conclusão:** checklist de usabilidade preenchido e problemas registrados.

### L01 — Diagramas e documentação técnica
**Responsável:** Luan  
**Dependências:** M03 e E01  
**Critério de conclusão:** diagramas de domínio/classes e fluxo coerentes com o código real.

### L02 — Matriz de rastreabilidade
**Responsáveis:** Luan e Maria Eduarda  
**Dependências:** D01, E04 e E05  
**Critério de conclusão:** requisitos relacionados aos critérios de aceite e testes correspondentes.

### L03 — Evidências e roteiro de execução
**Responsável:** Luan  
**Dependências:** E04, E05, E06 e E07  
**Critério de conclusão:** evidências e comandos de reprodução organizados com resultados reais.

### T01 — Executar casos de aceitação
**Responsáveis:** Maria Eduarda e Luan, com apoio de Millena  
**Dependências:** E03 e E05  
**Critério de conclusão:** cenários executados e resultados registrados com evidências.

### T02 — Analisar defeitos e fazer regressão
**Responsável:** Emely, com participação da equipe  
**Dependências:** T01  
**Critério de conclusão:** defeitos importantes reproduzidos, corrigidos e testados novamente.

### T03 — Revisão final e demonstração
**Responsáveis:** todos os integrantes  
**Dependências:** L01, L02, L03 e T02  
**Critério de conclusão:** documentação, execução, testes e evidências revisados; cada integrante consegue explicar sua contribuição.

## 4. Ordem de execução

1. Alinhar escopo, requisitos e fluxo: M01, M02 e D01.
2. Preparar modelo de dados, interface e estrutura técnica: M03, M04, M05 e E01.
3. Implementar regras e integrar o fluxo: E02 e E03.
4. Executar testes e preparar qualidade: E04, E05, E06 e E07.
5. Finalizar documentação, rastreabilidade e evidências: D02, L01, L02 e L03.
6. Validar o MVP, corrigir defeitos e preparar a demonstração: T01, T02 e T03.

## 5. Regras de acompanhamento

- Toda task deve ter responsável, dependências e critério de conclusão.
- Uma task não deve ser marcada como concluída sem evidência do trabalho realizado.
- Código deve compilar e ter os testes relevantes executados antes do fechamento da task.
- Documentação deve refletir o comportamento real do sistema.
- Contribuições devem ser registradas com autoria correta.
- A cobertura mínima de 75% é uma meta; só pode ser declarada atingida após gerar e verificar o relatório.
- O escopo de planos de treino, pagamentos e histórico deve ser confirmado com o professor antes de ampliar o MVP.

## 6. Situação do plano

Este documento registra a divisão planejada de trabalho. Os status devem ser atualizados conforme as atividades forem efetivamente realizadas.
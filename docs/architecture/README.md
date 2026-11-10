# Documentação de Arquitetura — VIGOR

## Visão geral

Este diretório reúne a documentação que descreve a organização técnica do VIGOR, suas decisões arquiteturais, diagramas e modelo de dados. Os documentos devem acompanhar o estado real do projeto, distinguindo definições e planos do que já está implementado.

## Arquitetura geral

- [Arquitetura geral do sistema](arquitetura-geral.md)

## Decisões arquiteturais

- [Registro de decisões técnicas](decisoes.md)

## Diagramas

Os diagramas documentam a arquitetura e os fluxos previstos; não comprovam, por si só, que as funcionalidades estejam implementadas.

| Diagrama | Arquivo |
|---|---|
| Diagrama de casos de uso | [01_caso_de_uso.png](../diagrams/01_caso_de_uso.png) |
| BPMN de gestão de membros | [04_bpmn_cadastro_gestao_membros.png](../diagrams/04_bpmn_cadastro_gestao_membros.png) |
| Diagrama de sequência do cadastro de membro | [03_seq_rf01_cadastrar_membro.png](../diagrams/03_seq_rf01_cadastrar_membro.png) |
| Diagrama de classes de domínio | [02a_classes_dominio.png](../diagrams/02a_classes_dominio.png) |
| Diagrama de classes da aplicação | [02b_classes_camadas.png](../diagrams/02b_classes_camadas.png) |
## Banco de dados

- [Modelo de dados](database/modelo-de-dados.md)

Este é o documento existente em `database/` no momento da consulta.

## Relação com os requisitos

A arquitetura deve atender aos requisitos funcionais e não funcionais e às regras de negócio documentados em `../requirements/`. Esses documentos definem o comportamento e as restrições esperados; sua presença não significa que todas as funcionalidades estejam implementadas ou validadas.

- [Requisitos do sistema](../requirements/requisitos.md)
- [Regras de negócio](../requirements/regras-de-negocio.md)
- [Critérios de aceitação](../requirements/criterios-de-aceite.md)

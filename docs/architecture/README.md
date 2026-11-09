# Documentação de Arquitetura — VIGOR

## Visão geral

Este diretório reúne a documentação que descreve a organização técnica do VIGOR, suas decisões arquiteturais, diagramas e modelo de dados. Os documentos devem acompanhar o estado real do projeto, distinguindo definições e planos do que já está implementado.

## Arquitetura geral

- [Arquitetura geral do sistema](arquitetura-geral.md)

## Decisões arquiteturais

- [Registro de decisões técnicas](decisoes.md)

## Diagramas

Os cinco diagramas previstos para a documentação são:

| Diagrama | Situação |
|---|---|
| Diagrama de casos de uso | Pendente; imagem ainda não encontrada em `diagrams/`. |
| BPMN de gestão de membros | Pendente; imagem ainda não encontrada em `diagrams/`. |
| Diagrama de sequência do cadastro de membro | Pendente; imagem ainda não encontrada em `diagrams/`. |
| Diagrama de classes de domínio | Pendente; imagem ainda não encontrada em `diagrams/`. |
| Diagrama de classes da aplicação | Pendente; imagem ainda não encontrada em `diagrams/`. |

O diretório `diagrams/` ainda não existe nesta estrutura; por isso, não são incluídos links para imagens ausentes.

## Banco de dados

- [Modelo de dados](database/modelo-de-dados.md)

Este é o documento existente em `database/` no momento da consulta.

## Relação com os requisitos

A arquitetura deve atender aos requisitos funcionais e não funcionais e às regras de negócio documentados em `../requirements/`. Esses documentos definem o comportamento e as restrições esperados; sua presença não significa que todas as funcionalidades estejam implementadas ou validadas.

- [Requisitos do sistema](../requirements/requisitos.md)
- [Regras de negócio](../requirements/regras-de-negocio.md)
- [Critérios de aceitação](../requirements/criterios-de-aceite.md)

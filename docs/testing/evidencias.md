# VIGOR — Registro de Evidências

## 1. Objetivo

Registrar as evidências de execução, testes, cobertura de código e integração contínua do VIGOR, permitindo acompanhar a qualidade do projeto de forma verificável.

## 2. Estado atual

A documentação dos testes está em preparação. Os resultados abaixo devem ser preenchidos somente após a execução efetiva das verificações.

## 3. Evidências de execução

| ID | Verificação | CT/VN e cenário a registrar | Evidência esperada | Situação |
|---|---|---|---|---|
| EV01 | Compilação do projeto | Não se aplica: build Maven | Resultado do build Maven | Pendente |
| EV02 | Testes unitários | CT/VN e cenário/variante efetivamente executados; pendente até execução | Relatório ou saída de `mvn test` | Pendente |
| EV03 | Testes de integração | CT/VN e cenário/variante efetivamente executados; pendente até execução | Relatório dos testes de integração | Pendente |
| EV04 | Testes funcionais | CT/VN e cenário/variante efetivamente executados; pendente até execução | Capturas ou registro do teste de aceitação | Pendente |
| EV05 | Cobertura de código | VN06 — geração e análise do relatório; pendente até execução | Relatório HTML do JaCoCo | Pendente |
| EV06 | Integração contínua | VN06 — execução do workflow; pendente até execução | Link da execução do GitHub Actions | Pendente |

## 4. Como registrar uma evidência

Para cada verificação realizada, registrar:

- Identificador da evidência.
- Identificador do CT ou VN relacionado e cenário/variante efetivamente executados.
- Se o cenário ainda não foi executado, manter a evidência como pendente e não preencher resultado como aprovado.
- Data da execução.
- Comando ou procedimento utilizado.
- Resultado obtido.
- Situação: aprovado, reprovado ou bloqueado.
- Caminho do arquivo ou link para a evidência.
- Defeito relacionado, quando aplicável.

## 5. Organização dos arquivos

Quando existirem evidências reais, organizar os arquivos de forma consistente. Exemplos:

- Capturas de tela dos testes de aceitação.
- Relatórios de testes automatizados.
- Relatório HTML de cobertura.
- Links para execuções do GitHub Actions.
- Registros de defeitos e respectivas verificações.

Não adicionar capturas fictícias nem declarar resultados que ainda não foram verificados.

## 6. Critério de cobertura

A meta definida para o projeto é de pelo menos 75% de cobertura de testes. O percentual efetivo deverá ser registrado após executar o JaCoCo.

## 7. Histórico

| Data | Alteração | Responsável |
|---|---|---|
| 26/10/2026 | Criação do modelo inicial de registro de evidências | Millena |

Atualizar este histórico conforme novas evidências forem produzidas.

## 8. Situação do documento

Modelo inicial. As evidências serão adicionadas à medida que a aplicação e os testes forem executados.
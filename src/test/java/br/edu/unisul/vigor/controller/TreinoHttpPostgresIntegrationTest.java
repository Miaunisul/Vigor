package br.edu.unisul.vigor.controller;

import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.service.MembroService;
import br.edu.unisul.vigor.validation.CpfUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnabledIfEnvironmentVariable(named = "DB_PASSWORD", matches = ".+")
class TreinoHttpPostgresIntegrationTest {

    private final String nomeTeste = "Treino HTTP M07 " + UUID.randomUUID();
    private final HttpClient cliente = HttpClient.newBuilder()
            .cookieHandler(new CookieManager())
            .build();

    @LocalServerPort
    private int porta;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MembroService membroService;

    @AfterEach
    void removerDadosCriadosPeloTeste() {
        jdbcTemplate.update(
                "DELETE FROM treinos WHERE membro_id IN "
                        + "(SELECT id FROM membros WHERE nome LIKE ?)",
                nomeTeste + "%");
        jdbcTemplate.update("DELETE FROM membros WHERE nome LIKE ?", nomeTeste + "%");
        cliente.close();
    }

    @Test
    void formularioHttpCarregaMembrosCadastrados() throws IOException, InterruptedException {
        Membro membro = membroParaTeste(" membro do formulário");

        HttpResponse<String> redirecionamento = get("/treinos/novo");

        assertEquals(302, redirecionamento.statusCode(), redirecionamento::body);
        String destino = redirecionamento.headers().firstValue("Location").orElseThrow();
        URI uriDestino = URI.create(destino);
        assertEquals("/treinos", uriDestino.getPath());
        assertEquals("acao=novo", uriDestino.getRawQuery());

        HttpResponse<String> resposta = get(
                uriDestino.getRawPath() + "?" + uriDestino.getRawQuery());
        assertEquals(200, resposta.statusCode(), resposta::body);
        assertTrue(resposta.body().contains("Cadastrar treino"), resposta::body);
        assertTrue(resposta.body().contains("value=\"" + membro.getId() + "\""), resposta::body);
        assertTrue(resposta.body().contains(membro.getNome()), resposta::body);
    }

    @Test
    void cadastroHttpPersisteNoPostgresEConsultaSomenteTreinosDoMembro()
            throws IOException, InterruptedException {
        Membro primeiro = membroParaTeste(" membro um");
        Membro segundo = membroParaTeste(" membro dois");

        HttpResponse<String> respostaPrimeiro = cadastrarTreino(
                primeiro.getId(), "Força", "Treino exclusivo do primeiro membro", 50);
        assertRedirecionaParaMembro(respostaPrimeiro, primeiro.getId());

        assertEquals(1, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM treinos WHERE membro_id = ? AND tipo = ? "
                        + "AND descricao = ? AND duracao_minutos = ? AND data_inicio = ?",
                Integer.class, primeiro.getId(), "Força",
                "Treino exclusivo do primeiro membro", 50,
                java.sql.Date.valueOf("2026-11-20")));

        HttpResponse<String> listaPrimeiro = get("/treinos?membroId=" + primeiro.getId());
        HttpResponse<String> listaSegundo = get("/treinos?membroId=" + segundo.getId());
        assertEquals(200, listaPrimeiro.statusCode(), listaPrimeiro::body);
        assertEquals(200, listaSegundo.statusCode(), listaSegundo::body);
        assertMembroSelecionado(listaPrimeiro.body(), primeiro.getId());
        assertMembroSelecionado(listaSegundo.body(), segundo.getId());
        assertTrue(listaPrimeiro.body().contains(
                "data-label=\"Tipo de treino\">Força</td>"), listaPrimeiro::body);
        assertFalse(listaPrimeiro.body().contains(
                "data-label=\"Tipo de treino\">Mobilidade</td>"), listaPrimeiro::body);
        assertTrue(listaSegundo.body().contains("Nenhum treino cadastrado para este membro"),
                listaSegundo::body);
        assertFalse(listaSegundo.body().contains(
                "data-label=\"Tipo de treino\">Força</td>"), listaSegundo::body);

        HttpResponse<String> respostaSegundo = cadastrarTreino(
                segundo.getId(), "Mobilidade", "Treino exclusivo do segundo membro", 35);
        assertRedirecionaParaMembro(respostaSegundo, segundo.getId());
        assertEquals(1, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM treinos WHERE membro_id = ? AND tipo = ? "
                        + "AND descricao = ? AND duracao_minutos = ? AND data_inicio = ?",
                Integer.class, segundo.getId(), "Mobilidade",
                "Treino exclusivo do segundo membro", 35,
                java.sql.Date.valueOf("2026-11-20")));
        assertEquals(0, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM treinos WHERE membro_id = ? AND descricao = ?",
                Integer.class, primeiro.getId(), "Treino exclusivo do segundo membro"));

        listaPrimeiro = get("/treinos?membroId=" + primeiro.getId());
        listaSegundo = get("/treinos?membroId=" + segundo.getId());
        assertEquals(200, listaPrimeiro.statusCode(), listaPrimeiro::body);
        assertEquals(200, listaSegundo.statusCode(), listaSegundo::body);
        assertMembroSelecionado(listaPrimeiro.body(), primeiro.getId());
        assertMembroSelecionado(listaSegundo.body(), segundo.getId());
        assertTrue(listaPrimeiro.body().contains(
                "data-label=\"Tipo de treino\">Força</td>"), listaPrimeiro::body);
        assertFalse(listaPrimeiro.body().contains(
                "data-label=\"Tipo de treino\">Mobilidade</td>"), listaPrimeiro::body);
        assertTrue(listaSegundo.body().contains(
                "data-label=\"Tipo de treino\">Mobilidade</td>"), listaSegundo::body);
        assertFalse(listaSegundo.body().contains(
                "data-label=\"Tipo de treino\">Força</td>"), listaSegundo::body);
    }

    @Test
    void cadastroHttpRejeitaMembroInexistenteSemPersistirTreino()
            throws IOException, InterruptedException {
        Membro membro = membroParaTeste(" membro para validação");

        HttpResponse<String> resposta = post("/treinos", formulario(
                Long.MAX_VALUE, "Funcional", "Não deve ser persistido", 40));

        assertEquals(200, resposta.statusCode(), resposta::body);
        assertTrue(resposta.body().contains("Membro não encontrado."), resposta::body);
        assertEquals(0, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM treinos WHERE membro_id = ?",
                Integer.class, membro.getId()));
    }

    private Membro membroParaTeste(String sufixo) {
        Membro membro = new Membro();
        membro.setNome(nomeTeste + sufixo);
        membro.setCpf(cpfDisponivel());
        membro.setTelefone("(48) 99999-0000");
        membro.setEndereco("Rua de teste, 1");
        return membroService.cadastrar(membro);
    }

    private String cpfDisponivel() {
        for (int tentativa = 0; tentativa < 100; tentativa++) {
            String base = String.format("%09d", ThreadLocalRandom.current()
                    .nextInt(100_000_000, 1_000_000_000));
            int primeiroDigito = calcularDigito(base, 10);
            int segundoDigito = calcularDigito(base + primeiroDigito, 11);
            String cpf = base + primeiroDigito + segundoDigito;
            Integer quantidade = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM membros WHERE cpf = ?", Integer.class, cpf);
            if (CpfUtils.ehValido(cpf) && quantidade != null && quantidade == 0) {
                return cpf;
            }
        }
        throw new IllegalStateException("Não foi possível gerar CPF de teste disponível.");
    }

    private int calcularDigito(String base, int pesoInicial) {
        int soma = 0;
        for (int indice = 0; indice < base.length(); indice++) {
            soma += (base.charAt(indice) - '0') * (pesoInicial - indice);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private HttpResponse<String> cadastrarTreino(
            Long membroId,
            String tipo,
            String descricao,
            int duracao) throws IOException, InterruptedException {
        return post("/treinos", formulario(membroId, tipo, descricao, duracao));
    }

    private String formulario(Long membroId, String tipo, String descricao, int duracao) {
        return "membroId=" + codificar(membroId.toString())
                + "&tipo=" + codificar(tipo)
                + "&descricao=" + codificar(descricao)
                + "&duracaoMinutos=" + codificar(Integer.toString(duracao))
                + "&dataInicio=" + codificar("2026-11-20");
    }

    private HttpResponse<String> post(String caminho, String formulario)
            throws IOException, InterruptedException {
        HttpRequest requisicao = HttpRequest.newBuilder(
                        URI.create("http://localhost:" + porta + caminho))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.ofString(formulario, StandardCharsets.UTF_8))
                .build();
        return cliente.send(requisicao, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> get(String caminho) throws IOException, InterruptedException {
        HttpRequest requisicao = HttpRequest.newBuilder(
                        URI.create("http://localhost:" + porta + caminho))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        return cliente.send(requisicao, HttpResponse.BodyHandlers.ofString());
    }

    private String codificar(String valor) {
        return URLEncoder.encode(valor, StandardCharsets.UTF_8);
    }

    private void assertRedirecionaParaMembro(HttpResponse<String> resposta, Long membroId) {
        assertEquals(302, resposta.statusCode(), resposta::body);
        String destino = resposta.headers().firstValue("Location").orElseThrow();
        URI uri = URI.create(destino);
        assertEquals("/treinos", uri.getPath().split(";jsessionid=", 2)[0]);
        assertEquals("membroId=" + membroId, uri.getRawQuery());
    }

    private void assertMembroSelecionado(String html, Long membroId) {
        int indiceValor = html.indexOf("value=\"" + membroId + "\"");
        assertTrue(indiceValor >= 0, html);
        int inicioOpcao = html.lastIndexOf("<option", indiceValor);
        int fimOpcao = html.indexOf('>', indiceValor);
        assertTrue(inicioOpcao >= 0 && fimOpcao > indiceValor, html);
        assertTrue(html.substring(inicioOpcao, fimOpcao).contains("selected"), html);
    }
}

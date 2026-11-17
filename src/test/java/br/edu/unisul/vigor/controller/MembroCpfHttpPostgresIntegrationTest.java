package br.edu.unisul.vigor.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MembroCpfHttpPostgresIntegrationTest {

    private final String nomeTeste = "Membro teste CPF HTTP " + UUID.randomUUID();

    @LocalServerPort
    private int porta;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void removerMembrosDoTeste() {
        jdbcTemplate.update("DELETE FROM membros WHERE nome IN (?, ?)",
                nomeTeste, nomeTeste + " duplicado");
    }

    @ParameterizedTest
    @CsvSource({
            "52998224725, 52998224725",
            "168.995.350-09, 16899535009"
    })
    void cadastraCpfValidoEPersisteSomenteDigitos(String cpfInformado, String cpfEsperado)
            throws IOException, InterruptedException {
        HttpResponse<String> resposta = cadastrar(nomeTeste, cpfInformado);

        assertCadastroAceito(resposta);
        assertEquals(List.of(new MembroPersistido(nomeTeste, cpfEsperado)), consultarMembrosDoTeste());
    }

    @ParameterizedTest
    @ValueSource(strings = {"52998224724", "529.982.247-24", "00000000000", "11111111111"})
    void rejeitaCpfInvalidoSemPersistir(String cpf) throws IOException, InterruptedException {
        HttpResponse<String> resposta = cadastrar(nomeTeste, cpf);

        assertCadastroRejeitado(resposta, "Informe um CPF válido com dígitos verificadores corretos.");
        assertTrue(consultarMembrosDoTeste().isEmpty());
    }

    @ParameterizedTest
    @CsvSource({
            "52998224725, 529.982.247-25, 52998224725",
            "168.995.350-09, 16899535009, 16899535009"
    })
    void rejeitaCpfDuplicadoAposNormalizacaoEPreservaOriginal(
            String cpfOriginal, String cpfDuplicado, String cpfEsperado)
            throws IOException, InterruptedException {
        assertCadastroAceito(cadastrar(nomeTeste, cpfOriginal));

        HttpResponse<String> resposta = cadastrar(nomeTeste + " duplicado", cpfDuplicado);

        assertCadastroRejeitado(resposta, "Já existe um membro cadastrado com este CPF.");
        assertEquals(List.of(new MembroPersistido(nomeTeste, cpfEsperado)), consultarMembrosDoTeste());
    }

    private HttpResponse<String> cadastrar(String nome, String cpf)
            throws IOException, InterruptedException {
        String formulario = "nome=" + URLEncoder.encode(nome, StandardCharsets.UTF_8)
                + "&cpf=" + URLEncoder.encode(cpf, StandardCharsets.UTF_8)
                + "&telefone=48999990000&endereco=Rua+de+teste";
        HttpRequest requisicao = HttpRequest.newBuilder(URI.create("http://localhost:" + porta + "/membros"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.ofString(formulario))
                .build();

        try (HttpClient cliente = HttpClient.newHttpClient()) {
            return cliente.send(requisicao, HttpResponse.BodyHandlers.ofString());
        }
    }

    private void assertCadastroAceito(HttpResponse<String> resposta) {
        assertEquals(302, resposta.statusCode(), resposta::body);
        String destino = resposta.headers().firstValue("Location").orElseThrow();
        assertEquals("/membros", URI.create(destino).getPath().split(";jsessionid=", 2)[0]);
    }

    private void assertCadastroRejeitado(HttpResponse<String> resposta, String mensagem) {
        assertEquals(200, resposta.statusCode(), resposta::body);
        assertTrue(resposta.body().contains(mensagem), resposta::body);
    }

    private List<MembroPersistido> consultarMembrosDoTeste() {
        return jdbcTemplate.query("SELECT nome, cpf FROM membros WHERE nome IN (?, ?)",
                (linha, indice) -> new MembroPersistido(linha.getString("nome"), linha.getString("cpf")),
                nomeTeste, nomeTeste + " duplicado");
    }

    private record MembroPersistido(String nome, String cpf) {
    }
}

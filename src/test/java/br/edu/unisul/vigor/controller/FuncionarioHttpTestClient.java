package br.edu.unisul.vigor.controller;

import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Cliente HTTP de integração que autentica pela tela real e mantém sessão e CSRF. */
final class FuncionarioHttpTestClient implements AutoCloseable {

    private static final Pattern TOKEN_CSRF = Pattern.compile(
            "<input\\b[^>]*\\bname=\"_csrf\"[^>]*\\bvalue=\"([^\"]+)\"[^>]*>");

    private final String origem;
    private final HttpClient cliente;
    private String tokenCsrf;

    FuncionarioHttpTestClient(int porta) {
        origem = "http://localhost:" + porta;
        cliente = HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .followRedirects(HttpClient.Redirect.NEVER)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    void autenticar(String login, String senha) throws IOException, InterruptedException {
        HttpResponse<String> formularioLogin = get("/login");
        if (formularioLogin.statusCode() != 200) {
            throw new IllegalStateException(
                    "A tela de login respondeu HTTP " + formularioLogin.statusCode());
        }
        tokenCsrf = extrairTokenCsrf(formularioLogin.body());

        String corpo = "login=" + codificar(login)
                + "&senha=" + codificar(senha)
                + "&_csrf=" + codificar(tokenCsrf);
        HttpResponse<String> respostaLogin = enviarPost("/login", corpo);
        String destino = respostaLogin.headers().firstValue(HttpHeaders.LOCATION).orElse("");
        if (respostaLogin.statusCode() != 302 || !URI.create(destino).getPath().equals("/membros")) {
            throw new IllegalStateException(
                    "O login de integração não autenticou o funcionário: HTTP "
                            + respostaLogin.statusCode() + ", destino=" + destino);
        }

        // O Spring Security invalida o token CSRF no login; obtenha o novo token da sessão.
        tokenCsrf = extrairTokenCsrf(get("/login").body());
    }

    HttpResponse<String> get(String caminho) throws IOException, InterruptedException {
        HttpRequest requisicao = HttpRequest.newBuilder(URI.create(origem + caminho))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        return cliente.send(requisicao, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    HttpResponse<String> post(String caminho, String formulario)
            throws IOException, InterruptedException {
        if (tokenCsrf == null) {
            throw new IllegalStateException("O cliente HTTP precisa autenticar antes de enviar POST.");
        }
        String separador = formulario == null || formulario.isEmpty() ? "" : "&";
        return enviarPost(caminho,
                (formulario == null ? "" : formulario)
                        + separador + "_csrf=" + codificar(tokenCsrf));
    }

    private HttpResponse<String> enviarPost(String caminho, String formulario)
            throws IOException, InterruptedException {
        HttpRequest requisicao = HttpRequest.newBuilder(URI.create(origem + caminho))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.ofString(formulario, StandardCharsets.UTF_8))
                .build();
        return cliente.send(requisicao, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private String extrairTokenCsrf(String html) {
        Matcher matcher = TOKEN_CSRF.matcher(html);
        if (!matcher.find()) {
            throw new IllegalStateException("A resposta HTTP não contém o campo CSRF do formulário.");
        }
        return matcher.group(1);
    }

    private String codificar(String valor) {
        return java.net.URLEncoder.encode(valor, StandardCharsets.UTF_8);
    }

    @Override
    public void close() {
        cliente.close();
    }
}

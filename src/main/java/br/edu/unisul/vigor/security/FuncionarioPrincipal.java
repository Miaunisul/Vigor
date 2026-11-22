package br.edu.unisul.vigor.security;

import java.io.Serial;
import java.io.Serializable;
import java.security.Principal;

/** Identidade mínima da sessão; não contém nem expõe o hash da senha. */
public final class FuncionarioPrincipal implements Principal, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Long id;
    private final String nome;
    private final String cargo;
    private final String login;

    public FuncionarioPrincipal(Long id, String nome, String cargo, String login) {
        this.id = id;
        this.nome = nome;
        this.cargo = cargo;
        this.login = login;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCargo() {
        return cargo;
    }

    public String getLogin() {
        return login;
    }

    @Override
    public String getName() {
        return nome;
    }
}

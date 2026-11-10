package br.edu.unisul.vigor.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "funcionarios")
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do funcionário é obrigatório.")
    @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
    @Column(nullable = false, length = 150)
    private String nome;

    @NotBlank(message = "O cargo do funcionário é obrigatório.")
    @Column(nullable = false, columnDefinition = "text")
    private String cargo;

    @NotBlank(message = "O login do funcionário é obrigatório.")
    // A unicidade normalizada é garantida pelo índice funcional criado na V2.
    @Column(nullable = false, columnDefinition = "text")
    private String login;

    @NotBlank(message = "A senha protegida do funcionário é obrigatória.")
    @Column(name = "senha_hash", nullable = false, columnDefinition = "text")
    private String senhaHash;

    protected Funcionario() {
    }

    public Funcionario(String nome, String cargo, String login, String senhaHash) {
        this.nome = nome;
        this.cargo = cargo;
        this.login = login;
        this.senhaHash = senhaHash;
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

    public String getSenhaHash() {
        return senhaHash;
    }
}


package br.edu.unisul.vigor.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import br.edu.unisul.vigor.validation.CpfValido;

import java.time.LocalDate;

@Entity
@Table(name = "membros", uniqueConstraints = {
    @UniqueConstraint(name = "uk_membros_cpf", columnNames = "cpf")
})
public class Membro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
    @Column(nullable = false, length = 150)
    private String nome;

    @NotBlank(message = "O CPF é obrigatório.")
    @CpfValido
    @Column(nullable = false, length = 11)
    private String cpf;

    @NotBlank(message = "O telefone é obrigatório.")
    @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres.")
    @Column(nullable = false, length = 20)
    private String telefone;

    @NotBlank(message = "O endereço é obrigatório.")
    @Size(max = 255, message = "O endereço deve ter no máximo 255 caracteres.")
    @Column(nullable = false, length = 255)
    private String endereco;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDate dataCadastro = LocalDate.now();

    public Membro() {
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }
}

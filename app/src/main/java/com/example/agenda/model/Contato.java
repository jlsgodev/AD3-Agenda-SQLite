package com.example.agenda.model;

import java.io.Serializable;

// Entidade (Bean): representa uma linha da tabela "contato"
public class Contato implements Serializable {
    private int id;
    private String nome;
    private String telefone;
    private String email;
    private String foto; // URI da imagem escolhida na galeria

    public Contato() {
    }

    // usado na inserção (o id é gerado pelo banco)
    public Contato(String nome, String email, String telefone, String foto) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.foto = foto;
    }

    // usado na alteração (precisa do id) — o livro chama este construtor, mas não o declara
    public Contato(int id, String nome, String email, String telefone, String foto) {
        this(nome, email, telefone, foto);
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
}

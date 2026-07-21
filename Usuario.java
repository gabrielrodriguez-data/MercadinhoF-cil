package com.mercadinhofacil.model;

/**
 * Representa um usuário do sistema (login e controle de permissões - RNF002).
 */
public class Usuario {

    public enum Permissao { GERENTE, ATENDENTE, FINANCEIRO, CAIXA }

    private int id;
    private String nome;
    private String login;
    private String senha;
    private Permissao permissao;

    public Usuario(int id, String nome, String login, String senha, Permissao permissao) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.senha = senha;
        this.permissao = permissao;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Permissao getPermissao() { return permissao; }
    public void setPermissao(Permissao permissao) { this.permissao = permissao; }

    @Override
    public String toString() {
        return nome;
    }
}

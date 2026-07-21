package com.mercadinhofacil.model;

import java.time.LocalDate;

/**
 * Representa um registro de auditoria de estoque (entrada, saída ou ajuste).
 * Corresponde à tabela "movimentacaoestoque" prevista no projeto.
 */
public class MovimentacaoEstoque {

    public enum Tipo { ENTRADA, SAIDA, AJUSTE }

    private int id;
    private Produto produto;
    private Tipo tipo;
    private int quantidade;
    private LocalDate data;
    private Usuario usuario;
    private String observacao;

    public MovimentacaoEstoque(int id, Produto produto, Tipo tipo, int quantidade,
                                LocalDate data, Usuario usuario, String observacao) {
        this.id = id;
        this.produto = produto;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.data = data;
        this.usuario = usuario;
        this.observacao = observacao;
    }

    public int getId() { return id; }
    public Produto getProduto() { return produto; }
    public Tipo getTipo() { return tipo; }
    public int getQuantidade() { return quantidade; }
    public LocalDate getData() { return data; }
    public Usuario getUsuario() { return usuario; }
    public String getObservacao() { return observacao; }
}

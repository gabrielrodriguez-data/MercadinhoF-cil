package com.mercadinhofacil.model;

/**
 * Representa um item dentro de uma venda (grade de itens da tela "Nova Venda").
 * Corresponde à tabela "itens_venda" do banco de dados (etapa_3.sql).
 */
public class ItemVenda {

    private Produto produto;
    private int quantidade;
    private double precoUnitario;

    public ItemVenda(Produto produto, int quantidade, double precoUnitario) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public Produto getProduto() { return produto; }
    public int getQuantidade() { return quantidade; }
    public double getPrecoUnitario() { return precoUnitario; }

    public double getSubtotal() {
        return quantidade * precoUnitario;
    }
}

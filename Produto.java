package com.mercadinhofacil.model;

/**
 * Representa um produto do inventário.
 * Corresponde à tabela "produto" do banco de dados (etapa_3.sql).
 */
public class Produto {

    private int id;
    private String nome;
    private String categoria;
    private double preco;
    private int estoqueAtual;
    private int estoqueMinimo;
    private Fornecedor fornecedor;

    public Produto(int id, String nome, String categoria, double preco,
                    int estoqueAtual, int estoqueMinimo, Fornecedor fornecedor) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.preco = preco;
        this.estoqueAtual = estoqueAtual;
        this.estoqueMinimo = estoqueMinimo;
        this.fornecedor = fornecedor;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    public int getEstoqueAtual() { return estoqueAtual; }
    public void setEstoqueAtual(int estoqueAtual) { this.estoqueAtual = estoqueAtual; }

    public int getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(int estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }

    public Fornecedor getFornecedor() { return fornecedor; }
    public void setFornecedor(Fornecedor fornecedor) { this.fornecedor = fornecedor; }

    /** Usado pelo Dashboard e pela tela de Estoque para sinalizar alerta (RNF004). */
    public boolean isEstoqueBaixo() {
        return estoqueAtual <= estoqueMinimo;
    }

    @Override
    public String toString() {
        return nome;
    }
}

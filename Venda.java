package com.mercadinhofacil.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma venda finalizada (RF006).
 * Corresponde à tabela "venda" do banco de dados (etapa_3.sql).
 */
public class Venda {

    private int id;
    private LocalDate data;
    private Cliente cliente;
    private Usuario vendedor;
    private List<ItemVenda> itens;
    private String tipoPagamento;

    public Venda(int id, LocalDate data, Cliente cliente, Usuario vendedor, String tipoPagamento) {
        this.id = id;
        this.data = data;
        this.cliente = cliente;
        this.vendedor = vendedor;
        this.tipoPagamento = tipoPagamento;
        this.itens = new ArrayList<>();
    }

    public int getId() { return id; }
    public LocalDate getData() { return data; }
    public Cliente getCliente() { return cliente; }
    public Usuario getVendedor() { return vendedor; }
    public List<ItemVenda> getItens() { return itens; }
    public String getTipoPagamento() { return tipoPagamento; }

    public double getValorTotal() {
        double total = 0;
        for (ItemVenda item : itens) {
            total += item.getSubtotal();
        }
        return total;
    }
}

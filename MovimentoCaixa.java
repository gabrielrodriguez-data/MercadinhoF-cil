package com.mercadinhofacil.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Representa uma linha do log de transações do módulo Caixa (Fechamento do Dia).
 */
public class MovimentoCaixa {

    public enum Tipo { ENTRADA, SAIDA }

    private LocalDate data;
    private LocalTime hora;
    private Tipo tipo;
    private String descricao;
    private double valor;

    public MovimentoCaixa(LocalDate data, LocalTime hora, Tipo tipo, String descricao, double valor) {
        this.data = data;
        this.hora = hora;
        this.tipo = tipo;
        this.descricao = descricao;
        this.valor = valor;
    }

    public LocalDate getData() { return data; }
    public LocalTime getHora() { return hora; }
    public Tipo getTipo() { return tipo; }
    public String getDescricao() { return descricao; }
    public double getValor() { return valor; }
}

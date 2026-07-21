package com.mercadinhofacil.ui;

/**
 * Implementada pelos painéis que exibem dados que podem ter sido alterados
 * em outra tela (ex.: uma venda alterou o estoque, então o Dashboard e a
 * tela de Produtos precisam recarregar suas tabelas ao serem exibidos).
 */
public interface Refreshable {
    void refresh();
}

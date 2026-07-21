package com.mercadinhofacil.dao;

import com.mercadinhofacil.model.ItemVenda;
import com.mercadinhofacil.model.Venda;
import com.mercadinhofacil.util.Conexao;
import java.sql.*;

public class VendaDAO {

    public int adicionar(Venda v) {
        String sqlVenda = "INSERT INTO venda (data, id_cliente, id_usuario, tipo_pagamento, valor_total) VALUES (?, ?, ?, ?, ?)";
        String sqlItem = "INSERT INTO itens_venda (id_venda, id_produto, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";
        int idVendaGerado = -1;

        try (Connection conn = Conexao.conectar()) {
            conn.setAutoCommit(false); // Inicia transação

            try (PreparedStatement stmtVenda = conn.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS)) {
                // CORRIGIDO AQUI: usando 'v' em vez de 'venda'
                stmtVenda.setDate(1, Date.valueOf(v.getData())); 
                stmtVenda.setInt(2, v.getCliente().getId());
                stmtVenda.setInt(3, v.getVendedor().getId());
                stmtVenda.setString(4, v.getTipoPagamento());
                stmtVenda.setDouble(5, v.getValorTotal());
                stmtVenda.executeUpdate();

                // Pega o ID da venda recém criada
                ResultSet rs = stmtVenda.getGeneratedKeys();
                if (rs.next()) {
                    idVendaGerado = rs.getInt(1);
                }

                // Salva cada item da venda
                try (PreparedStatement stmtItem = conn.prepareStatement(sqlItem)) {
                    for (ItemVenda item : v.getItens()) {
                        stmtItem.setInt(1, idVendaGerado);
                        stmtItem.setInt(2, item.getProduto().getId());
                        stmtItem.setInt(3, item.getQuantidade());
                        stmtItem.setDouble(4, item.getPrecoUnitario());
                        stmtItem.executeUpdate();
                    }
                }
                
                conn.commit(); // Confirma tudo se deu certo
                

            } catch (SQLException e) {
                conn.rollback(); // Desfaz tudo se der erro
                throw new RuntimeException("Erro ao processar venda: " + e.getMessage());
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro de conexão: " + e.getMessage());
        }
        return idVendaGerado;
    }
}
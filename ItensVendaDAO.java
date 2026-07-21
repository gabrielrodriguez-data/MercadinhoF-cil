package com.mercadinhofacil.dao;

import com.mercadinhofacil.model.ItemVenda;
import com.mercadinhofacil.model.Produto;
import com.mercadinhofacil.util.Conexao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Alterado o nome da classe para ItensVendaDAO (igual ao arquivo)
public class ItensVendaDAO {

    public void adicionar(int idVenda, ItemVenda item) {
        String sql = "INSERT INTO itens_venda (id_venda, id_produto, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVenda);
            stmt.setInt(2, item.getProduto().getId());
            stmt.setInt(3, item.getQuantidade());
            stmt.setDouble(4, item.getPrecoUnitario());

            stmt.execute();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar item à venda: " + e.getMessage());
        }
    }

    public List<ItemVenda> listarPorVenda(int idVenda) {
        String sql = "SELECT * FROM itens_venda WHERE id_venda = ?";
        List<ItemVenda> itens = new ArrayList<>();

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVenda);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // CORREÇÃO: Usando o construtor correto de Produto (7 argumentos)
                // Se você não tiver os outros dados, passe 0 ou null conforme a necessidade
                Produto p = new Produto(rs.getInt("id_produto"), "", "", 0.0, 0, 0, null); 
                
                ItemVenda item = new ItemVenda(p, rs.getInt("quantidade"), rs.getDouble("preco_unitario"));
                itens.add(item);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar itens da venda: " + e.getMessage());
        }
        return itens;
    }
}
package com.mercadinhofacil.dao;

import com.mercadinhofacil.model.MovimentacaoEstoque;
import com.mercadinhofacil.util.Conexao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoEstoqueDAO {

    public void adicionar(MovimentacaoEstoque m) {
        String sql = "INSERT INTO movimentacaoestoque (id_produto, tipo, quantidade, data, id_usuario, observacao) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, m.getProduto().getId());
            stmt.setString(2, m.getTipo().name()); // Salva como "ENTRADA", "SAIDA" ou "AJUSTE"
            stmt.setInt(3, m.getQuantidade());
            stmt.setDate(4, Date.valueOf(m.getData())); // Converte LocalDate para java.sql.Date
            stmt.setInt(5, m.getUsuario().getId());
            stmt.setString(6, m.getObservacao());

            stmt.execute();
            System.out.println("Movimentação registrada com sucesso!");

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar movimentação de estoque: " + e.getMessage());
        }
    }

    public List<MovimentacaoEstoque> listarTodos() {
        String sql = "SELECT * FROM movimentacaoestoque";
        List<MovimentacaoEstoque> lista = new ArrayList<>();

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Nota: Aqui você precisaria buscar os objetos Produto e Usuario 
                // completos pelo ID para preencher o construtor corretamente.
                // Se for apenas para listagem simples, você pode ajustar o construtor.
                
                // Exemplo simplificado (considerando que você carregará os objetos depois):
                // MovimentacaoEstoque m = new MovimentacaoEstoque(..., ..., ...);
                // lista.add(m);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar movimentações: " + e.getMessage());
        }
        return lista;
    }
}
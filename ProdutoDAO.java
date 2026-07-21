package com.mercadinhofacil.dao;

import com.mercadinhofacil.model.Fornecedor;
import com.mercadinhofacil.model.Produto;
import com.mercadinhofacil.util.Conexao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    public void adicionar(Produto p) {
        String sql = "INSERT INTO produto (nome, categoria, preco, estoque_atual, estoque_minimo, id_fornecedor) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getCategoria());
            stmt.setDouble(3, p.getPreco());
            stmt.setInt(4, p.getEstoqueAtual());
            stmt.setInt(5, p.getEstoqueMinimo());
            stmt.setInt(6, p.getFornecedor().getId());

            stmt.execute();
            

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar produto: " + e.getMessage());
        }
    }

    public List<Produto> listarTodos() {
        String sql = "SELECT * FROM produto";
        List<Produto> produtos = new ArrayList<>();
        FornecedorDAO fDao = new FornecedorDAO(); // Para buscar o objeto Fornecedor completo

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Aqui estamos buscando o fornecedor associado pelo ID
                Fornecedor f = fDao.buscarPorId(rs.getInt("id_fornecedor"));
                
                Produto p = new Produto(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("categoria"),
                        rs.getDouble("preco"),
                        rs.getInt("estoque_atual"),
                        rs.getInt("estoque_minimo"),
                        f
                );
                produtos.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos: " + e.getMessage());
        }
        return produtos;
    }

    public Produto buscarPorId(int id) {
        String sql = "SELECT * FROM produto WHERE id = ?";
        FornecedorDAO fDao = new FornecedorDAO();

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Fornecedor f = fDao.buscarPorId(rs.getInt("id_fornecedor"));
                return new Produto(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("categoria"),
                        rs.getDouble("preco"),
                        rs.getInt("estoque_atual"),
                        rs.getInt("estoque_minimo"),
                        f
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produto por ID: " + e.getMessage());
        }
        return null;
    }
}
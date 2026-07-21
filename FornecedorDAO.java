package com.mercadinhofacil.dao;

import com.mercadinhofacil.model.Fornecedor;
import com.mercadinhofacil.util.Conexao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FornecedorDAO {

    public void adicionar(Fornecedor fornecedor) {
        String sql = "INSERT INTO fornecedor (nome, cnpj, contato) VALUES (?, ?, ?)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fornecedor.getNome());
            stmt.setString(2, fornecedor.getCnpj());
            stmt.setString(3, fornecedor.getContato());
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar fornecedor: " + e.getMessage());
        }
    }

    public List<Fornecedor> listarTodos() {
        String sql = "SELECT * FROM fornecedor";
        List<Fornecedor> fornecedores = new ArrayList<>();
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                fornecedores.add(new Fornecedor(
                        rs.getInt("id_fornecedor"), 
                        rs.getString("nome"),
                        rs.getString("cnpj"), 
                        rs.getString("contato")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar fornecedores: " + e.getMessage());
        }
        return fornecedores;
    }

    public Fornecedor buscarPorId(int id) {
        String sql = "SELECT * FROM fornecedor WHERE id = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Fornecedor(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cnpj"),
                        rs.getString("contato")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar fornecedor por ID: " + e.getMessage());
        }
        return null;
    }
}
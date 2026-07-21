package com.mercadinhofacil.dao;

import com.mercadinhofacil.model.MovimentoCaixa;
import com.mercadinhofacil.util.Conexao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovimentoCaixaDAO {

    public void adicionar(MovimentoCaixa m) {
        String sql = "INSERT INTO movimento_caixa (data, hora, tipo, descricao, valor) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(m.getData()));
            stmt.setTime(2, Time.valueOf(m.getHora()));
            stmt.setString(3, m.getTipo().name());
            stmt.setString(4, m.getDescricao());
            stmt.setDouble(5, m.getValor());

            stmt.execute();
            System.out.println("Movimento de caixa registrado com sucesso!");

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar movimento de caixa: " + e.getMessage());
        }
    }

    public List<MovimentoCaixa> listarTodos() {
        String sql = "SELECT * FROM movimento_caixa ORDER BY data DESC, hora DESC";
        List<MovimentoCaixa> lista = new ArrayList<>();

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                MovimentoCaixa m = new MovimentoCaixa(
                        rs.getDate("data").toLocalDate(),
                        rs.getTime("hora").toLocalTime(),
                        MovimentoCaixa.Tipo.valueOf(rs.getString("tipo")),
                        rs.getString("descricao"),
                        rs.getDouble("valor")
                );
                lista.add(m);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar movimentos de caixa: " + e.getMessage());
        }
        return lista;
    }
}
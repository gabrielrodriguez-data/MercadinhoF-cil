package com.mercadinhofacil;

import com.mercadinhofacil.ui.LoginFrame;

import javax.swing.*;

/**
 * Ponto de entrada do sistema "Mercadinho Fácil".
 *
 * Etapa 3 do Projeto Integrador: implementação das telas em Java (Swing) e
 * de todas as interações que não dependem de um banco de dados real — os
 * dados são mantidos em memória pela classe DataStore, simulando as
 * tabelas definidas em etapa_3.sql.
 */
public class Main {

    public static void main(String[] args) {
        // Usa o visual nativo do sistema operacional quando disponível.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignorado) {
            // Se não for possível aplicar o Look and Feel do sistema, segue com o padrão do Swing.
        }

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}

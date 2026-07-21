package com.mercadinhofacil.ui.paineis;

import com.mercadinhofacil.ui.UIConstants;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

/** Métodos utilitários para deixar os 8 módulos com a mesma identidade visual. */
final class EstiloUI {

    private EstiloUI() { }

    static JLabel tituloPagina(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(UIConstants.FONTE_TITULO);
        label.setBorder(BorderFactory.createEmptyBorder(20, 25, 15, 25));
        return label;
    }

    static JButton botaoPrimario(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(UIConstants.VERDE);
        botao.setForeground(Color.WHITE);
        botao.setFont(UIConstants.FONTE_BOTAO);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return botao;
    }

    static JButton botaoSecundario(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(UIConstants.BRANCO);
        botao.setForeground(Color.DARK_GRAY);
        botao.setFont(UIConstants.FONTE_BOTAO);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createLineBorder(UIConstants.BORDA));
        return botao;
    }

    static JButton botaoPerigo(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(UIConstants.VERMELHO);
        botao.setForeground(Color.WHITE);
        botao.setFont(UIConstants.FONTE_BOTAO);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        return botao;
    }

    static void estilizarTabela(JTable tabela) {
        tabela.setRowHeight(30);
        tabela.setFont(UIConstants.FONTE_NORMAL);
        tabela.setSelectionBackground(new Color(0xE9, 0xF7, 0xEF));
        tabela.setSelectionForeground(Color.BLACK);
        tabela.setGridColor(UIConstants.BORDA);
        tabela.setShowVerticalLines(false);
        JTableHeader cabecalho = tabela.getTableHeader();
        cabecalho.setFont(UIConstants.FONTE_SUBTITULO.deriveFont(12f));
        cabecalho.setBackground(UIConstants.FUNDO_TELA);
        cabecalho.setForeground(UIConstants.TEXTO_CINZA);
        cabecalho.setPreferredSize(new Dimension(0, 34));
    }

    static JPanel painelCartao() {
        JPanel painel = new JPanel();
        painel.setBackground(UIConstants.BRANCO);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDA),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)));
        return painel;
    }

    static JScrollPane semBorda(Component conteudo) {
        JScrollPane scroll = new JScrollPane(conteudo);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        return scroll;
    }

    /**
     * Configura a coluna "Ações" de uma JTable de CRUD para exibir os ícones
     * de Editar (verde) e Excluir (vermelho), disparando os callbacks
     * informados com o índice do modelo (linha) correspondente. Evita
     * repetir a mesma lógica de TableCellRenderer/MouseListener nos módulos
     * de Produtos, Clientes, Fornecedores e Usuários.
     */
    static void configurarColunaAcoes(JTable tabela, String nomeColuna,
                                       java.util.function.IntConsumer aoEditar,
                                       java.util.function.IntConsumer aoExcluir) {
        int indiceColuna = tabela.getColumn(nomeColuna).getModelIndex();

        tabela.getColumn(nomeColuna).setCellRenderer((t, value, isSelected, hasFocus, row, column) -> {
            JPanel painel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
            painel.setOpaque(true);
            painel.setBackground(isSelected ? new Color(0xE9, 0xF7, 0xEF) : Color.WHITE);
            JLabel editar = new JLabel("\u270E");
            editar.setForeground(UIConstants.VERDE);
            editar.setFont(editar.getFont().deriveFont(Font.BOLD, 14f));
            JLabel excluir = new JLabel("\uD83D\uDDD1");
            excluir.setForeground(UIConstants.VERMELHO);
            excluir.setFont(excluir.getFont().deriveFont(Font.BOLD, 14f));
            painel.add(editar);
            painel.add(excluir);
            return painel;
        });

        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int coluna = tabela.columnAtPoint(e.getPoint());
                int linhaView = tabela.rowAtPoint(e.getPoint());
                if (linhaView < 0 || coluna != tabela.convertColumnIndexToView(indiceColuna)) return;

                int linhaModelo = tabela.convertRowIndexToModel(linhaView);
                Rectangle celula = tabela.getCellRect(linhaView, coluna, false);
                int xRelativo = e.getX() - celula.x;
                if (xRelativo < celula.width / 2) {
                    aoEditar.accept(linhaModelo);
                } else {
                    aoExcluir.accept(linhaModelo);
                }
            }
        });
    }
}

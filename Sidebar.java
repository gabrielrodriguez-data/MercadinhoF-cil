package com.mercadinhofacil.ui;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Menu Lateral (Navegação) descrito na Etapa 2: organiza os módulos do
 * sistema (Dashboard, Vendas, Produtos, Clientes, Fornecedores, Caixa,
 * Movimentação de Estoque, Usuários) e dispara a troca de tela no CardLayout
 * do MainFrame.
 */
public class Sidebar extends JPanel {

    private final Map<String, JButton> botoes = new LinkedHashMap<>();
    private String selecionado;

    public Sidebar(Consumer<String> aoSelecionar, Runnable aoSair) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UIConstants.MENU_FUNDO);
        setPreferredSize(new Dimension(210, 0));

        JLabel logo = new JLabel("\uD83D\uDED2  Mercadinho Fácil");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("SansSerif", Font.BOLD, 15));
        logo.setBorder(BorderFactory.createEmptyBorder(20, 18, 20, 10));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(logo);

        adicionarItem("Dashboard", aoSelecionar);
        adicionarItem("Vendas", aoSelecionar);
        adicionarItem("Produtos", aoSelecionar);
        adicionarItem("Clientes", aoSelecionar);
        adicionarItem("Fornecedores", aoSelecionar);
        adicionarItem("Caixa", aoSelecionar);
        adicionarItem("Movimentação de Estoque", aoSelecionar);
        adicionarItem("Usuários", aoSelecionar);

        add(Box.createVerticalGlue());

        JButton sair = criarBotao("Sair");
        sair.setForeground(new Color(0xE7, 0x4C, 0x3C));
        sair.addActionListener(e -> aoSair.run());
        add(sair);
        add(Box.createVerticalStrut(15));

        selecionar("Dashboard");
    }

    private void adicionarItem(String nome, Consumer<String> aoSelecionar) {
        JButton botao = criarBotao(nome);
        botao.addActionListener(e -> {
            selecionar(nome);
            aoSelecionar.accept(nome);
        });
        botoes.put(nome, botao);
        add(botao);
    }

    private JButton criarBotao(String texto) {
        JButton botao = new JButton(texto);
        botao.setHorizontalAlignment(SwingConstants.LEFT);
        botao.setForeground(Color.WHITE);
        botao.setBackground(UIConstants.MENU_FUNDO);
        botao.setFont(UIConstants.FONTE_MENU);
        botao.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 10));
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setOpaque(true);
        botao.setAlignmentX(Component.LEFT_ALIGNMENT);
        botao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return botao;
    }

    public void selecionar(String nome) {
        if (selecionado != null && botoes.containsKey(selecionado)) {
            botoes.get(selecionado).setBackground(UIConstants.MENU_FUNDO);
        }
        selecionado = nome;
        if (botoes.containsKey(nome)) {
            botoes.get(nome).setBackground(UIConstants.MENU_SELECIONADO);
        }
    }
}

package com.mercadinhofacil.ui;

import com.mercadinhofacil.data.DataStore;
import com.mercadinhofacil.model.Usuario;
import com.mercadinhofacil.ui.paineis.*;

import javax.swing.*;
import java.awt.*;

/**
 * Janela principal do sistema. Contém o cabeçalho, o menu lateral (Sidebar)
 * e a área central com CardLayout, onde cada módulo (Dashboard, Vendas,
 * Produtos, Clientes, Fornecedores, Caixa, Estoque, Usuários) é um JPanel
 * independente — a navegação troca apenas o "card" visível, sem abrir
 * novas janelas, conforme previsto na Etapa 2.
 */
public class MainFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel painelCartoes = new JPanel(cardLayout);
    private final JLabel labelUsuarioLogado = new JLabel();

    private final DashboardPanel dashboardPanel = new DashboardPanel(this);
    private final VendaPanel vendaPanel = new VendaPanel();
    private final ProdutoPanel produtoPanel = new ProdutoPanel();
    private final ClientePanel clientePanel = new ClientePanel();
    private final FornecedorPanel fornecedorPanel = new FornecedorPanel();
    private final CaixaPanel caixaPanel = new CaixaPanel();
    private final EstoquePanel estoquePanel = new EstoquePanel();
    private final UsuarioPanel usuarioPanel = new UsuarioPanel();

    public MainFrame() {
        super("Mercadinho Fácil");
        montarTela();
    }

    private void montarTela() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 760);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);

        Sidebar sidebar = new Sidebar(this::navegarPara, this::sair);
        add(sidebar, BorderLayout.WEST);

        JPanel areaCentral = new JPanel(new BorderLayout());
        areaCentral.setBackground(UIConstants.FUNDO_TELA);
        areaCentral.add(criarCabecalho(), BorderLayout.NORTH);

        painelCartoes.setBackground(UIConstants.FUNDO_TELA);
        painelCartoes.add(dashboardPanel, "Dashboard");
        painelCartoes.add(vendaPanel, "Vendas");
        painelCartoes.add(produtoPanel, "Produtos");
        painelCartoes.add(clientePanel, "Clientes");
        painelCartoes.add(fornecedorPanel, "Fornecedores");
        painelCartoes.add(caixaPanel, "Caixa");
        painelCartoes.add(estoquePanel, "Movimentação de Estoque");
        painelCartoes.add(usuarioPanel, "Usuários");

        areaCentral.add(painelCartoes, BorderLayout.CENTER);
        add(areaCentral, BorderLayout.CENTER);

        atualizarCabecalho();
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(UIConstants.BRANCO);
        cabecalho.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDA),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)));

        labelUsuarioLogado.setFont(UIConstants.FONTE_NORMAL);
        labelUsuarioLogado.setHorizontalAlignment(SwingConstants.RIGHT);
        cabecalho.add(labelUsuarioLogado, BorderLayout.EAST);
        return cabecalho;
    }

    private void atualizarCabecalho() {
        Usuario u = DataStore.getInstancia().getUsuarioLogado();
        if (u != null) {
            labelUsuarioLogado.setText("<html><div style='text-align:right'>Olá, "
                    + u.getNome() + "<br><small style='color:gray'>" + u.getPermissao() + "</small></div></html>");
        }
    }

    /** Troca o card exibido e recarrega os dados do painel de destino. */
    public void navegarPara(String nomeTela) {
        cardLayout.show(painelCartoes, nomeTela);
        Component atual = obterComponentePorNome(nomeTela);
        if (atual instanceof Refreshable refreshable) {
            refreshable.refresh();
        }
    }

    private Component obterComponentePorNome(String nome) {
        return switch (nome) {
            case "Dashboard" -> dashboardPanel;
            case "Vendas" -> vendaPanel;
            case "Produtos" -> produtoPanel;
            case "Clientes" -> clientePanel;
            case "Fornecedores" -> fornecedorPanel;
            case "Caixa" -> caixaPanel;
            case "Movimentação de Estoque" -> estoquePanel;
            case "Usuários" -> usuarioPanel;
            default -> null;
        };
    }

    private void sair() {
        int opcao = JOptionPane.showConfirmDialog(this,
                "Deseja realmente sair do sistema?", "Sair",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opcao == JOptionPane.YES_OPTION) {
            DataStore.getInstancia().setUsuarioLogado(null);
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }
}

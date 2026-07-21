package com.mercadinhofacil.ui.paineis;

import com.mercadinhofacil.data.DataStore;
import com.mercadinhofacil.model.*;
import com.mercadinhofacil.ui.Refreshable;
import com.mercadinhofacil.ui.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Interface de Vendas/Pedido (RF006). Permite montar a grade de itens em
 * memória e, ao clicar em "Finalizar Venda", persiste a venda, dá baixa
 * automática no estoque e lança a entrada no caixa do dia — fluxo descrito
 * na Etapa 2 entre as tabelas venda, itens_venda, produto e
 * movimentacaoestoque.
 */
public class VendaPanel extends JPanel implements Refreshable {

    private final DataStore dataStore = DataStore.getInstancia();
    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private final JComboBox<Usuario> comboVendedor = new JComboBox<>();
    private final JComboBox<Cliente> comboCliente = new JComboBox<>();
    private final JComboBox<Produto> comboProduto = new JComboBox<>();
    private final JSpinner spinnerQuantidade = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
    private final JComboBox<String> comboPagamento =
            new JComboBox<>(new String[]{"À Vista", "Cartão", "Débito", "Fiado"});

    private final DefaultTableModel modeloItens = new DefaultTableModel(
            new Object[]{"Produto", "Qtd", "Preço Unit.", "Subtotal", "Ação"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tabelaItens = new JTable(modeloItens);
    private final JLabel labelTotal = new JLabel("R$ 0,00");

    private final List<ItemVenda> itensAtuais = new ArrayList<>();

    public VendaPanel() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.FUNDO_TELA);
        add(EstiloUI.tituloPagina("Vendas"), BorderLayout.NORTH);

        JPanel cartao = EstiloUI.painelCartao();
        cartao.setLayout(new BorderLayout(0, 15));

        JLabel subtitulo = new JLabel("Nova Venda");
        subtitulo.setFont(UIConstants.FONTE_SUBTITULO);
        cartao.add(subtitulo, BorderLayout.NORTH);

        JPanel corpo = new JPanel();
        corpo.setOpaque(false);
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        corpo.add(criarLinhaCabecalho());
        corpo.add(Box.createVerticalStrut(15));
        corpo.add(criarLinhaLancamento());
        corpo.add(Box.createVerticalStrut(15));

        EstiloUI.estilizarTabela(tabelaItens);
        configurarColunaExcluir();
        JScrollPane scrollTabela = new JScrollPane(tabelaItens);
        scrollTabela.setPreferredSize(new Dimension(0, 200));
        corpo.add(scrollTabela);
        corpo.add(Box.createVerticalStrut(15));
        corpo.add(criarRodape());

        cartao.add(EstiloUI.semBorda(corpo), BorderLayout.CENTER);

        JPanel margem = new JPanel(new BorderLayout());
        margem.setOpaque(false);
        margem.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));
        margem.add(cartao, BorderLayout.CENTER);
        add(margem, BorderLayout.CENTER);

        refresh();
    }

    private JPanel criarLinhaCabecalho() {
        JPanel linha = new JPanel(new GridLayout(1, 2, 20, 0));
        linha.setOpaque(false);
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        JPanel painelVendedor = new JPanel(new BorderLayout());
        painelVendedor.setOpaque(false);
        painelVendedor.add(new JLabel("Vendedor"), BorderLayout.NORTH);
        painelVendedor.add(comboVendedor, BorderLayout.CENTER);

        JPanel painelCliente = new JPanel(new BorderLayout(6, 0));
        painelCliente.setOpaque(false);
        JPanel labelCliente = new JPanel(new BorderLayout());
        labelCliente.setOpaque(false);
        labelCliente.add(new JLabel("Cliente"), BorderLayout.NORTH);
        labelCliente.add(comboCliente, BorderLayout.CENTER);
        JButton addCliente = EstiloUI.botaoPrimario("+");
        addCliente.addActionListener(e -> adicionarClienteRapido());
        JPanel painelBotaoCliente = new JPanel(new BorderLayout());
        painelBotaoCliente.setOpaque(false);
        painelBotaoCliente.add(Box.createVerticalStrut(18), BorderLayout.NORTH);
        painelBotaoCliente.add(addCliente, BorderLayout.CENTER);
        painelCliente.add(labelCliente, BorderLayout.CENTER);
        painelCliente.add(painelBotaoCliente, BorderLayout.EAST);

        linha.add(painelVendedor);
        linha.add(painelCliente);
        return linha;
    }

    private JPanel criarLinhaLancamento() {
        JPanel linha = new JPanel(new BorderLayout(10, 0));
        linha.setOpaque(false);
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        JPanel painelProduto = new JPanel(new BorderLayout());
        painelProduto.setOpaque(false);
        painelProduto.add(new JLabel("Produto"), BorderLayout.NORTH);
        painelProduto.add(comboProduto, BorderLayout.CENTER);

        JPanel painelQuantidade = new JPanel(new BorderLayout());
        painelQuantidade.setOpaque(false);
        painelQuantidade.setPreferredSize(new Dimension(110, 50));
        painelQuantidade.add(new JLabel("Quantidade"), BorderLayout.NORTH);
        painelQuantidade.add(spinnerQuantidade, BorderLayout.CENTER);

        JButton adicionar = EstiloUI.botaoPrimario("+ Adicionar Item");
        adicionar.addActionListener(e -> adicionarItem());
        JPanel painelBotao = new JPanel(new BorderLayout());
        painelBotao.setOpaque(false);
        painelBotao.add(Box.createVerticalStrut(18), BorderLayout.NORTH);
        painelBotao.add(adicionar, BorderLayout.CENTER);

        linha.add(painelProduto, BorderLayout.CENTER);
        linha.add(painelQuantidade, BorderLayout.EAST);

        JPanel envoltorio = new JPanel(new BorderLayout(10, 0));
        envoltorio.setOpaque(false);
        envoltorio.add(linha, BorderLayout.CENTER);
        envoltorio.add(painelBotao, BorderLayout.SOUTH);
        return envoltorio;
    }

    private JPanel criarRodape() {
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setOpaque(false);
        rodape.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        esquerda.setOpaque(false);
        esquerda.add(new JLabel("Forma de Pagamento:"));
        esquerda.add(comboPagamento);

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        direita.setOpaque(false);
        JLabel rotuloTotal = new JLabel("Total:  ");
        rotuloTotal.setFont(UIConstants.FONTE_SUBTITULO);
        labelTotal.setFont(new Font("SansSerif", Font.BOLD, 20));
        labelTotal.setForeground(UIConstants.VERDE);
        direita.add(rotuloTotal);
        direita.add(labelTotal);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.setOpaque(false);
        JButton cancelar = EstiloUI.botaoSecundario("Cancelar");
        cancelar.addActionListener(e -> cancelarVenda());
        JButton finalizar = EstiloUI.botaoPrimario("\u2713 Finalizar Venda");
        finalizar.addActionListener(e -> finalizarVenda());
        botoes.add(cancelar);
        botoes.add(finalizar);

        JPanel base = new JPanel(new BorderLayout());
        base.setOpaque(false);
        base.add(esquerda, BorderLayout.WEST);
        base.add(direita, BorderLayout.CENTER);

        rodape.add(base, BorderLayout.NORTH);
        rodape.add(botoes, BorderLayout.SOUTH);
        return rodape;
    }

    private void configurarColunaExcluir() {
        int indice = tabelaItens.getColumn("Ação").getModelIndex();
        tabelaItens.getColumn("Ação").setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JLabel lixeira = new JLabel("\uD83D\uDDD1", SwingConstants.CENTER);
            lixeira.setForeground(UIConstants.VERMELHO);
            return lixeira;
        });
        tabelaItens.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int coluna = tabelaItens.columnAtPoint(e.getPoint());
                int linha = tabelaItens.rowAtPoint(e.getPoint());
                if (linha >= 0 && coluna == tabelaItens.convertColumnIndexToView(indice)) {
                    itensAtuais.remove(linha);
                    atualizarTabelaItens();
                }
            }
        });
    }

    private void adicionarItem() {
        Produto produto = (Produto) comboProduto.getSelectedItem();
        if (produto == null) {
            JOptionPane.showMessageDialog(this, "Cadastre ou selecione um produto primeiro.",
                    "Nenhum produto disponível", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int quantidade = (int) spinnerQuantidade.getValue();
        if (quantidade > produto.getEstoqueAtual()) {
            JOptionPane.showMessageDialog(this,
                    "Estoque insuficiente para \"" + produto.getNome() + "\" (disponível: "
                            + produto.getEstoqueAtual() + ").",
                    "Estoque insuficiente", JOptionPane.WARNING_MESSAGE);
            return;
        }
        itensAtuais.add(new ItemVenda(produto, quantidade, produto.getPreco()));
        atualizarTabelaItens();
    }

    private void atualizarTabelaItens() {
        modeloItens.setRowCount(0);
        double total = 0;
        for (ItemVenda item : itensAtuais) {
            modeloItens.addRow(new Object[]{
                    item.getProduto().getNome(), item.getQuantidade(),
                    moeda.format(item.getPrecoUnitario()), moeda.format(item.getSubtotal()), ""});
            total += item.getSubtotal();
        }
        labelTotal.setText(moeda.format(total));
    }

    private void adicionarClienteRapido() {
        JTextField campoNome = new JTextField();
        JTextField campoCpf = new JTextField();
        JTextField campoTelefone = new JTextField();
        JPanel painel = new JPanel(new GridLayout(0, 2, 8, 8));
        painel.add(new JLabel("Nome:")); painel.add(campoNome);
        painel.add(new JLabel("CPF:")); painel.add(campoCpf);
        painel.add(new JLabel("Telefone:")); painel.add(campoTelefone);

        int opcao = JOptionPane.showConfirmDialog(this, painel, "Novo Cliente",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcao != JOptionPane.OK_OPTION || campoNome.getText().trim().isEmpty()) return;

        Cliente novo = new Cliente(dataStore.proximoIdCliente(), campoNome.getText().trim(),
                campoCpf.getText().trim(), campoTelefone.getText().trim());
        dataStore.getClientes().add(novo);
        atualizarCombos();
        comboCliente.setSelectedItem(novo);
    }

    private void cancelarVenda() {
        if (!itensAtuais.isEmpty()) {
            int opcao = JOptionPane.showConfirmDialog(this,
                    "Deseja realmente cancelar esta venda? Os itens adicionados serão perdidos.",
                    "Cancelar venda", JOptionPane.YES_NO_OPTION);
            if (opcao != JOptionPane.YES_OPTION) return;
        }
        itensAtuais.clear();
        atualizarTabelaItens();
    }

    private void finalizarVenda() {
        if (itensAtuais.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Adicione ao menos um item para finalizar a venda.",
                    "Venda vazia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Usuario vendedor = (Usuario) comboVendedor.getSelectedItem();
        Cliente cliente = (Cliente) comboCliente.getSelectedItem();
        String pagamento = (String) comboPagamento.getSelectedItem();

        Venda venda = new Venda(dataStore.getVendas().size() + 1, LocalDate.now(), cliente, vendedor, pagamento);
        for (ItemVenda item : itensAtuais) {
            venda.getItens().add(item);
        }
        dataStore.registrarVenda(venda);

        JOptionPane.showMessageDialog(this,
                "Venda finalizada com sucesso!\nTotal: " + moeda.format(venda.getValorTotal()),
                "Venda concluída", JOptionPane.INFORMATION_MESSAGE);

        itensAtuais.clear();
        atualizarTabelaItens();
        atualizarCombos();
    }

    private void atualizarCombos() {
        comboVendedor.removeAllItems();
        for (Usuario u : dataStore.getUsuarios()) comboVendedor.addItem(u);
        if (dataStore.getUsuarioLogado() != null) comboVendedor.setSelectedItem(dataStore.getUsuarioLogado());

        Cliente clienteSelecionado = (Cliente) comboCliente.getSelectedItem();
        comboCliente.removeAllItems();
        for (Cliente c : dataStore.getClientes()) comboCliente.addItem(c);
        if (clienteSelecionado != null) comboCliente.setSelectedItem(clienteSelecionado);

        comboProduto.removeAllItems();
        for (Produto p : dataStore.getProdutos()) {
            if (p.getEstoqueAtual() > 0) comboProduto.addItem(p);
        }
    }

    @Override
    public void refresh() {
        atualizarCombos();
    }
}

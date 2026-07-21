package com.mercadinhofacil.ui.paineis;

import com.mercadinhofacil.data.DataStore;
import com.mercadinhofacil.model.MovimentacaoEstoque;
import com.mercadinhofacil.model.Produto;
import com.mercadinhofacil.ui.Refreshable;
import com.mercadinhofacil.ui.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Interface de Movimentação de Estoque: registro manual de entradas, saídas
 * e ajustes, com filtros por produto, tipo e período, servindo como trilha
 * de auditoria (RF/RNF de rastreabilidade descritos na Etapa 2).
 */
public class EstoquePanel extends JPanel implements Refreshable {

    private final DataStore dataStore = DataStore.getInstancia();
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final JComboBox<String> filtroProduto = new JComboBox<>();
    private final JComboBox<String> filtroTipo = new JComboBox<>(new String[]{"Todos", "Entrada", "Saída", "Ajuste"});

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Produto", "Tipo", "Quantidade", "Data", "Usuário", "Observação"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    public EstoquePanel() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.FUNDO_TELA);
        add(EstiloUI.tituloPagina("Movimentação de Estoque"), BorderLayout.NORTH);

        JPanel conteudo = new JPanel(new BorderLayout(0, 15));
        conteudo.setOpaque(false);
        conteudo.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));

        JPanel topo = new JPanel();
        topo.setOpaque(false);
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        topo.add(criarLinhaFiltros());
        topo.add(Box.createVerticalStrut(10));
        topo.add(criarLinhaAcoes());
        conteudo.add(topo, BorderLayout.NORTH);

        JPanel painelTabela = EstiloUI.painelCartao();
        painelTabela.setLayout(new BorderLayout());
        EstiloUI.estilizarTabela(tabela);
        painelTabela.add(new JScrollPane(tabela), BorderLayout.CENTER);
        conteudo.add(painelTabela, BorderLayout.CENTER);

        add(conteudo, BorderLayout.CENTER);
        refresh();
    }

    private JPanel criarLinhaFiltros() {
        JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        linha.setOpaque(false);
        linha.add(new JLabel("Produto:"));
        linha.add(filtroProduto);
        linha.add(new JLabel("Tipo:"));
        linha.add(filtroTipo);
        JButton filtrar = EstiloUI.botaoPrimario("Filtrar");
        filtrar.addActionListener(e -> carregarTabela());
        linha.add(filtrar);
        return linha;
    }

    private JPanel criarLinhaAcoes() {
        JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        linha.setOpaque(false);

        JButton entrada = EstiloUI.botaoPrimario("+ Entrada");
        entrada.addActionListener(e -> abrirFormulario(MovimentacaoEstoque.Tipo.ENTRADA));

        JButton saida = EstiloUI.botaoPerigo("\u2212 Saída");
        saida.addActionListener(e -> abrirFormulario(MovimentacaoEstoque.Tipo.SAIDA));

        JButton ajuste = new JButton("\u21C4 Ajuste");
        ajuste.setBackground(UIConstants.LARANJA);
        ajuste.setForeground(Color.WHITE);
        ajuste.setFont(UIConstants.FONTE_BOTAO);
        ajuste.setFocusPainted(false);
        ajuste.addActionListener(e -> abrirFormulario(MovimentacaoEstoque.Tipo.AJUSTE));

        linha.add(entrada);
        linha.add(saida);
        linha.add(ajuste);
        return linha;
    }

    private void abrirFormulario(MovimentacaoEstoque.Tipo tipo) {
        JComboBox<Produto> campoProduto = new JComboBox<>(dataStore.getProdutos().toArray(new Produto[0]));
        JSpinner campoQuantidade = new JSpinner(new SpinnerNumberModel(1, tipo == MovimentacaoEstoque.Tipo.AJUSTE ? -100000 : 1, 100000, 1));
        JTextField campoObservacao = new JTextField();

        JPanel painel = new JPanel(new GridLayout(0, 2, 8, 8));
        painel.add(new JLabel("Produto:"));
        painel.add(campoProduto);
        painel.add(new JLabel(tipo == MovimentacaoEstoque.Tipo.AJUSTE ? "Ajuste (+/-):" : "Quantidade:"));
        painel.add(campoQuantidade);
        painel.add(new JLabel("Observação:"));
        painel.add(campoObservacao);

        String titulo = switch (tipo) {
            case ENTRADA -> "Registrar Entrada de Estoque";
            case SAIDA -> "Registrar Saída de Estoque";
            case AJUSTE -> "Registrar Ajuste de Estoque";
        };

        int opcao = JOptionPane.showConfirmDialog(this, painel, titulo,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcao != JOptionPane.OK_OPTION) return;

        Produto produto = (Produto) campoProduto.getSelectedItem();
        int quantidade = (int) campoQuantidade.getValue();
        String observacao = campoObservacao.getText().trim();

        if (observacao.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Informe uma observação para justificar a movimentação (boa prática de auditoria).",
                    "Observação obrigatória", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (tipo == MovimentacaoEstoque.Tipo.SAIDA && quantidade > produto.getEstoqueAtual()) {
            JOptionPane.showMessageDialog(this,
                    "Quantidade de saída maior que o estoque atual (" + produto.getEstoqueAtual() + ").",
                    "Estoque insuficiente", JOptionPane.WARNING_MESSAGE);
            return;
        }

        MovimentacaoEstoque mov = new MovimentacaoEstoque(dataStore.proximoIdMovimentacao(), produto, tipo,
                quantidade, LocalDate.now(), dataStore.getUsuarioLogado(), observacao);
        dataStore.registrarMovimentacao(mov);
        carregarTabela();
    }

    private void carregarTabela() {
        String produtoSelecionado = (String) filtroProduto.getSelectedItem();
        String tipoSelecionado = (String) filtroTipo.getSelectedItem();

        List<MovimentacaoEstoque> lista = dataStore.getMovimentacoes().stream()
                .filter(m -> produtoSelecionado == null || "Todos".equals(produtoSelecionado)
                        || m.getProduto().getNome().equals(produtoSelecionado))
                .filter(m -> tipoSelecionado == null || "Todos".equals(tipoSelecionado)
                        || traduzirTipo(m.getTipo()).equals(tipoSelecionado))
                .collect(Collectors.toList());

        modelo.setRowCount(0);
        for (MovimentacaoEstoque m : lista) {
            modelo.addRow(new Object[]{
                    String.format("%03d", m.getId()), m.getProduto().getNome(), traduzirTipo(m.getTipo()),
                    m.getQuantidade(), m.getData().format(FORMATO_DATA),
                    m.getUsuario() != null ? m.getUsuario().getNome() : "-", m.getObservacao()});
        }
    }

    private String traduzirTipo(MovimentacaoEstoque.Tipo tipo) {
        return switch (tipo) {
            case ENTRADA -> "Entrada";
            case SAIDA -> "Saída";
            case AJUSTE -> "Ajuste";
        };
    }

    private void atualizarFiltroProduto() {
        String selecionadoAntes = (String) filtroProduto.getSelectedItem();
        filtroProduto.removeAllItems();
        filtroProduto.addItem("Todos");
        for (Produto p : dataStore.getProdutos()) {
            filtroProduto.addItem(p.getNome());
        }
        if (selecionadoAntes != null) filtroProduto.setSelectedItem(selecionadoAntes);
    }

    @Override
    public void refresh() {
        atualizarFiltroProduto();
        carregarTabela();
    }
}

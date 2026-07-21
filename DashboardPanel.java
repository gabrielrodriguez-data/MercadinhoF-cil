package com.mercadinhofacil.ui.paineis;

import com.mercadinhofacil.data.DataStore;
import com.mercadinhofacil.model.Produto;
import com.mercadinhofacil.ui.MainFrame;
import com.mercadinhofacil.ui.Refreshable;
import com.mercadinhofacil.ui.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Interface Dashboard (RF010 / RNF004): painel gerencial com indicadores,
 * gráfico de vendas dos últimos 7 dias, resumo do caixa e alerta de estoque
 * baixo, conforme especificado na Etapa 2.
 */
public class DashboardPanel extends JPanel implements Refreshable {

    private final DataStore dataStore = DataStore.getInstancia();
    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private final MainFrame mainFrame;

    private final JLabel valorVendasHoje = new JLabel();
    private final JLabel valorCaixaHoje = new JLabel();
    private final JLabel valorProdutosCadastrados = new JLabel();
    private final JLabel valorEstoqueBaixo = new JLabel();

    private final JLabel labelSaldoInicial = new JLabel();
    private final JLabel labelEntradas = new JLabel();
    private final JLabel labelSaidas = new JLabel();
    private final JLabel labelSaldoAtual = new JLabel();

    private final MiniGraficoLinha grafico = new MiniGraficoLinha();
    private final DefaultTableModel modeloTabela = new DefaultTableModel(
            new Object[]{"Código", "Produto", "Estoque Atual", "Estoque Mínimo", "Ação"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tabelaEstoqueBaixo = new JTable(modeloTabela);

    public DashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(UIConstants.FUNDO_TELA);

        add(EstiloUI.tituloPagina("Dashboard"), BorderLayout.NORTH);

        JPanel conteudo = new JPanel();
        conteudo.setOpaque(false);
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));

        conteudo.add(criarLinhaCartoes());
        conteudo.add(Box.createVerticalStrut(20));
        conteudo.add(criarLinhaGraficoResumo());
        conteudo.add(Box.createVerticalStrut(20));
        conteudo.add(criarTabelaEstoqueBaixo());

        add(EstiloUI.semBorda(conteudo), BorderLayout.CENTER);

        refresh();
    }

    private JPanel criarLinhaCartoes() {
        JPanel linha = new JPanel(new GridLayout(1, 4, 15, 0));
        linha.setOpaque(false);
        linha.add(criarCartaoIndicador("Vendas Hoje", valorVendasHoje, UIConstants.VERDE));
        linha.add(criarCartaoIndicador("Caixa Hoje", valorCaixaHoje, UIConstants.AZUL));
        linha.add(criarCartaoIndicador("Produtos Cadastrados", valorProdutosCadastrados, UIConstants.ROXO));
        linha.add(criarCartaoIndicador("Estoque Baixo", valorEstoqueBaixo, UIConstants.LARANJA));
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);
        return linha;
    }

    private JPanel criarCartaoIndicador(String titulo, JLabel labelValor, Color cor) {
        JPanel cartao = EstiloUI.painelCartao();
        cartao.setLayout(new BorderLayout());

        JLabel icone = new JLabel("\u25CF");
        icone.setForeground(cor);

        JLabel labelTitulo = new JLabel(titulo);
        labelTitulo.setForeground(UIConstants.TEXTO_CINZA);
        labelTitulo.setFont(UIConstants.FONTE_NORMAL);

        labelValor.setFont(new Font("SansSerif", Font.BOLD, 20));
        labelValor.setForeground(cor);

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        topo.setOpaque(false);
        topo.add(icone);
        topo.add(labelTitulo);

        cartao.add(topo, BorderLayout.NORTH);
        cartao.add(labelValor, BorderLayout.CENTER);
        return cartao;
    }

    private JPanel criarLinhaGraficoResumo() {
        JPanel linha = new JPanel(new GridLayout(1, 2, 15, 0));
        linha.setOpaque(false);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel painelGrafico = EstiloUI.painelCartao();
        painelGrafico.setLayout(new BorderLayout());
        JLabel tituloGrafico = new JLabel("Vendas dos últimos 7 dias");
        tituloGrafico.setFont(UIConstants.FONTE_SUBTITULO);
        painelGrafico.add(tituloGrafico, BorderLayout.NORTH);
        painelGrafico.add(grafico, BorderLayout.CENTER);

        JPanel painelResumo = EstiloUI.painelCartao();
        painelResumo.setLayout(new GridLayout(4, 2, 8, 10));
        JLabel tituloResumo = new JLabel("Resumo do Caixa");
        tituloResumo.setFont(UIConstants.FONTE_SUBTITULO);

        JPanel painelResumoCompleto = new JPanel(new BorderLayout());
        painelResumoCompleto.setOpaque(false);
        painelResumoCompleto.add(tituloResumo, BorderLayout.NORTH);

        JPanel grade = new JPanel(new GridLayout(4, 2, 8, 12));
        grade.setOpaque(false);
        grade.add(rotuloCinza("Saldo Inicial"));
        grade.add(labelSaldoInicial);
        grade.add(rotuloCinza("Entradas"));
        grade.add(labelEntradas);
        grade.add(rotuloCinza("Saídas"));
        grade.add(labelSaidas);
        grade.add(rotuloCinza("Saldo Atual"));
        grade.add(labelSaldoAtual);
        painelResumoCompleto.add(grade, BorderLayout.CENTER);

        linha.add(painelGrafico);
        linha.add(painelResumoCompleto);
        return linha;
    }

    private JLabel rotuloCinza(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(UIConstants.TEXTO_CINZA);
        return label;
    }

    private JPanel criarTabelaEstoqueBaixo() {
        JPanel painel = EstiloUI.painelCartao();
        painel.setLayout(new BorderLayout());
        painel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titulo = new JLabel("Produtos com Estoque Baixo");
        titulo.setFont(UIConstants.FONTE_SUBTITULO);
        painel.add(titulo, BorderLayout.NORTH);

        EstiloUI.estilizarTabela(tabelaEstoqueBaixo);
        tabelaEstoqueBaixo.getColumn("Ação").setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JLabel link = new JLabel("Ver", SwingConstants.CENTER);
            link.setForeground(UIConstants.AZUL);
            return link;
        });
        tabelaEstoqueBaixo.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (tabelaEstoqueBaixo.getSelectedRow() >= 0) {
                    mainFrame.navegarPara("Produtos");
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabelaEstoqueBaixo);
        scroll.setPreferredSize(new Dimension(0, 190));
        scroll.setBorder(BorderFactory.createEmptyBorder());
        painel.add(scroll, BorderLayout.CENTER);
        return painel;
    }

    @Override
    public void refresh() {
        valorVendasHoje.setText(moeda.format(dataStore.getVendasHoje()));
        valorCaixaHoje.setText(moeda.format(dataStore.getSaldoAtualCaixa()));
        valorProdutosCadastrados.setText(String.valueOf(dataStore.getProdutos().size()));

        List<Produto> estoqueBaixo = dataStore.getProdutosEstoqueBaixo();
        valorEstoqueBaixo.setText(estoqueBaixo.size() + " itens");

        labelSaldoInicial.setText(moeda.format(dataStore.getSaldoInicialCaixa()));
        labelEntradas.setText(moeda.format(dataStore.getTotalEntradasHoje()));
        labelSaidas.setText(moeda.format(dataStore.getTotalSaidasHoje()));
        labelSaldoAtual.setText(moeda.format(dataStore.getSaldoAtualCaixa()));

        grafico.setDados(dataStore.getValoresUltimos7Dias(), dataStore.getRotulosUltimos7Dias());

        modeloTabela.setRowCount(0);
        for (Produto p : estoqueBaixo) {
            modeloTabela.addRow(new Object[]{
                    String.format("%03d", p.getId()), p.getNome(),
                    p.getEstoqueAtual(), p.getEstoqueMinimo(), "Ver"});
        }
    }
}

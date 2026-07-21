package com.mercadinhofacil.ui.paineis;

import com.mercadinhofacil.data.DataStore;
import com.mercadinhofacil.model.MovimentoCaixa;
import com.mercadinhofacil.ui.Refreshable;
import com.mercadinhofacil.ui.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Módulo de Caixa e Fechamento do Dia: consolidação financeira diária,
 * exibindo saldo inicial, entradas, saídas e saldo atual, além do log de
 * transações do dia (RNF002 - ação sensível restrita a Gerente/Caixa).
 */
public class CaixaPanel extends JPanel implements Refreshable {

    private final DataStore dataStore = DataStore.getInstancia();
    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final JLabel labelData = new JLabel();
    private final JLabel labelSaldoInicial = new JLabel();
    private final JLabel labelEntradas = new JLabel();
    private final JLabel labelSaidas = new JLabel();
    private final JLabel labelSaldoAtual = new JLabel();

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Hora", "Tipo", "Descrição", "Valor (R$)"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tabela = new JTable(modelo);
    private boolean caixaFechado = false;

    public CaixaPanel() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.FUNDO_TELA);
        add(EstiloUI.tituloPagina("Caixa - Fechamento do Dia"), BorderLayout.NORTH);

        JPanel cartao = EstiloUI.painelCartao();
        cartao.setLayout(new BorderLayout(0, 15));

        cartao.add(criarLinhaData(), BorderLayout.NORTH);

        JPanel meio = new JPanel();
        meio.setOpaque(false);
        meio.setLayout(new BoxLayout(meio, BoxLayout.Y_AXIS));
        meio.add(criarLinhaIndicadores());
        meio.add(Box.createVerticalStrut(15));

        JLabel tituloLista = new JLabel("Movimentações do Dia");
        tituloLista.setFont(UIConstants.FONTE_SUBTITULO);
        tituloLista.setAlignmentX(Component.LEFT_ALIGNMENT);
        meio.add(tituloLista);
        meio.add(Box.createVerticalStrut(8));

        EstiloUI.estilizarTabela(tabela);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setPreferredSize(new Dimension(0, 260));
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        meio.add(scroll);

        cartao.add(EstiloUI.semBorda(meio), BorderLayout.CENTER);
        cartao.add(criarRodape(), BorderLayout.SOUTH);

        JPanel margem = new JPanel(new BorderLayout());
        margem.setOpaque(false);
        margem.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));
        margem.add(cartao, BorderLayout.CENTER);
        add(margem, BorderLayout.CENTER);

        refresh();
    }

    private JPanel criarLinhaData() {
        JPanel linha = new JPanel(new BorderLayout());
        linha.setOpaque(false);
        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        esquerda.setOpaque(false);
        esquerda.add(new JLabel("Data:"));
        labelData.setFont(UIConstants.FONTE_SUBTITULO);
        esquerda.add(labelData);

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        direita.setOpaque(false);
        direita.add(new JLabel("Saldo Inicial: "));
        labelSaldoInicial.setFont(UIConstants.FONTE_SUBTITULO);
        direita.add(labelSaldoInicial);

        linha.add(esquerda, BorderLayout.WEST);
        linha.add(direita, BorderLayout.EAST);
        return linha;
    }

    private JPanel criarLinhaIndicadores() {
        JPanel linha = new JPanel(new GridLayout(1, 3, 15, 0));
        linha.setOpaque(false);
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        linha.add(criarIndicador("Entradas", labelEntradas, UIConstants.VERDE));
        linha.add(criarIndicador("Saídas", labelSaidas, UIConstants.VERMELHO));
        linha.add(criarIndicador("Saldo Atual", labelSaldoAtual, UIConstants.AZUL));
        return linha;
    }

    private JPanel criarIndicador(String titulo, JLabel labelValor, Color cor) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(UIConstants.FUNDO_TELA);
        painel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        JLabel labelTitulo = new JLabel(titulo, SwingConstants.CENTER);
        labelTitulo.setForeground(UIConstants.TEXTO_CINZA);
        labelValor.setHorizontalAlignment(SwingConstants.CENTER);
        labelValor.setForeground(cor);
        labelValor.setFont(new Font("SansSerif", Font.BOLD, 18));
        painel.add(labelTitulo, BorderLayout.NORTH);
        painel.add(labelValor, BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarRodape() {
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rodape.setOpaque(false);

        JButton imprimir = EstiloUI.botaoSecundario("\uD83D\uDDA8 Imprimir Relatório");
        imprimir.addActionListener(e -> imprimirRelatorio());

        JButton fechar = EstiloUI.botaoPrimario("\uD83D\uDD12 Fechar Caixa");
        fechar.addActionListener(e -> fecharCaixa());

        rodape.add(imprimir);
        rodape.add(fechar);
        return rodape;
    }

    private void imprimirRelatorio() {
        StringBuilder relatorio = new StringBuilder();
        relatorio.append("Fechamento de Caixa - ").append(labelData.getText()).append("\n\n");
        relatorio.append("Saldo Inicial: ").append(labelSaldoInicial.getText()).append("\n");
        relatorio.append("Entradas: ").append(labelEntradas.getText()).append("\n");
        relatorio.append("Saídas: ").append(labelSaidas.getText()).append("\n");
        relatorio.append("Saldo Atual: ").append(labelSaldoAtual.getText()).append("\n");

        JTextArea area = new JTextArea(relatorio.toString());
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                "Relatório de Caixa (pré-visualização)", JOptionPane.PLAIN_MESSAGE);
    }

    private void fecharCaixa() {
        if (caixaFechado) {
            JOptionPane.showMessageDialog(this, "O caixa de hoje já está fechado.",
                    "Caixa fechado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int opcao = JOptionPane.showConfirmDialog(this,
                "Confirmar o fechamento do caixa do dia?\nSaldo final: " + labelSaldoAtual.getText(),
                "Fechar Caixa", JOptionPane.YES_NO_OPTION);
        if (opcao == JOptionPane.YES_OPTION) {
            caixaFechado = true;
            JOptionPane.showMessageDialog(this, "Caixa fechado com sucesso!",
                    "Fechamento concluído", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void refresh() {
        labelData.setText(java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        labelSaldoInicial.setText(moeda.format(dataStore.getSaldoInicialCaixa()));
        labelEntradas.setText(moeda.format(dataStore.getTotalEntradasHoje()));
        labelSaidas.setText(moeda.format(dataStore.getTotalSaidasHoje()));
        labelSaldoAtual.setText(moeda.format(dataStore.getSaldoAtualCaixa()));

        modelo.setRowCount(0);
        for (MovimentoCaixa m : dataStore.getCaixa()) {
            String tipo = m.getTipo() == MovimentoCaixa.Tipo.ENTRADA ? "Entrada" : "Saída";
            String valor = (m.getTipo() == MovimentoCaixa.Tipo.SAIDA ? "-" : "") + moeda.format(m.getValor());
            modelo.addRow(new Object[]{m.getHora().format(FORMATO_HORA), tipo, m.getDescricao(), valor});
        }
    }
}

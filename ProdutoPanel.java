package com.mercadinhofacil.ui.paineis;

import com.mercadinhofacil.data.DataStore;
import com.mercadinhofacil.model.Fornecedor;
import com.mercadinhofacil.model.MovimentacaoEstoque;
import com.mercadinhofacil.model.Produto;
import com.mercadinhofacil.ui.Refreshable;
import com.mercadinhofacil.ui.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Módulo de Cadastro de Produtos (RF001). Permite Criar, Ler, Atualizar e
 * Excluir produtos, com o campo Fornecedor implementado como ComboBox
 * (carregado da lista de fornecedores) para preservar a integridade
 * referencial, conforme a Nota Técnica da Etapa 2.
 */
public class ProdutoPanel extends JPanel implements Refreshable {

    private final DataStore dataStore = DataStore.getInstancia();
    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private final JTextField campoBusca = new JTextField();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Código", "Produto", "Categoria", "Preço", "Estoque", "Ações"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tabela = new JTable(modelo);
    private List<Produto> exibidos;

    public ProdutoPanel() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.FUNDO_TELA);
        add(EstiloUI.tituloPagina("Produtos"), BorderLayout.NORTH);

        JPanel conteudo = new JPanel(new BorderLayout(0, 15));
        conteudo.setOpaque(false);
        conteudo.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));

        conteudo.add(criarBarraSuperior(), BorderLayout.NORTH);

        JPanel painelTabela = EstiloUI.painelCartao();
        painelTabela.setLayout(new BorderLayout());
        EstiloUI.estilizarTabela(tabela);
        EstiloUI.configurarColunaAcoes(tabela, "Ações", this::editar, this::excluir);
        painelTabela.add(new JScrollPane(tabela), BorderLayout.CENTER);
        conteudo.add(painelTabela, BorderLayout.CENTER);

        add(conteudo, BorderLayout.CENTER);

        campoBusca.addCaretListener(e -> carregarTabela());
        refresh();
    }

    private JPanel criarBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setOpaque(false);

        campoBusca.setToolTipText("Pesquisar produto...");
        campoBusca.setPreferredSize(new Dimension(260, 32));
        JPanel painelBusca = new JPanel(new BorderLayout());
        painelBusca.setOpaque(false);
        painelBusca.add(new JLabel("\uD83D\uDD0D  "), BorderLayout.WEST);
        painelBusca.add(campoBusca, BorderLayout.CENTER);

        JButton novo = EstiloUI.botaoPrimario("+ Novo Produto");
        novo.addActionListener(e -> abrirFormulario(null));

        barra.add(painelBusca, BorderLayout.WEST);
        barra.add(novo, BorderLayout.EAST);
        return barra;
    }

    private void carregarTabela() {
        String filtro = campoBusca.getText().trim().toLowerCase();
        exibidos = dataStore.getProdutos().stream()
                .filter(p -> filtro.isEmpty() || p.getNome().toLowerCase().contains(filtro))
                .collect(Collectors.toList());

        modelo.setRowCount(0);
        for (Produto p : exibidos) {
            modelo.addRow(new Object[]{
                    String.format("%03d", p.getId()), p.getNome(), p.getCategoria(),
                    moeda.format(p.getPreco()), p.getEstoqueAtual(), ""});
        }
    }

    private void editar(int linha) {
        abrirFormulario(exibidos.get(linha));
    }

    private void excluir(int linha) {
        Produto produto = exibidos.get(linha);

        boolean possuiHistorico = dataStore.getMovimentacoes().stream()
                .anyMatch(m -> m.getProduto() == produto)
                || dataStore.getVendas().stream()
                .anyMatch(v -> v.getItens().stream().anyMatch(i -> i.getProduto() == produto));

        if (possuiHistorico) {
            JOptionPane.showMessageDialog(this,
                    "Este produto possui movimentações ou vendas associadas e não pode ser excluído,\n"
                            + "para preservar a integridade referencial do banco de dados.",
                    "Não foi possível excluir", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opcao = JOptionPane.showConfirmDialog(this,
                "Excluir o produto \"" + produto.getNome() + "\"?", "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION);
        if (opcao == JOptionPane.YES_OPTION) {
            dataStore.getProdutos().remove(produto);
            carregarTabela();
        }
    }

    private void abrirFormulario(Produto produtoExistente) {
        JTextField campoNome = new JTextField(produtoExistente != null ? produtoExistente.getNome() : "");
        JTextField campoCategoria = new JTextField(produtoExistente != null ? produtoExistente.getCategoria() : "");
        JTextField campoPreco = new JTextField(produtoExistente != null ? String.valueOf(produtoExistente.getPreco()) : "");
        JSpinner campoEstoqueAtual = new JSpinner(new SpinnerNumberModel(
                produtoExistente != null ? produtoExistente.getEstoqueAtual() : 0, 0, 100000, 1));
        JSpinner campoEstoqueMinimo = new JSpinner(new SpinnerNumberModel(
                produtoExistente != null ? produtoExistente.getEstoqueMinimo() : 0, 0, 100000, 1));

        JComboBox<Fornecedor> campoFornecedor = new JComboBox<>(
                dataStore.getFornecedores().toArray(new Fornecedor[0]));
        if (produtoExistente != null) {
            campoFornecedor.setSelectedItem(produtoExistente.getFornecedor());
        }

        JPanel painel = new JPanel(new GridLayout(0, 2, 8, 8));
        painel.add(new JLabel("Nome:"));
        painel.add(campoNome);
        painel.add(new JLabel("Categoria:"));
        painel.add(campoCategoria);
        painel.add(new JLabel("Preço (R$):"));
        painel.add(campoPreco);
        painel.add(new JLabel("Estoque Atual:"));
        painel.add(campoEstoqueAtual);
        painel.add(new JLabel("Estoque Mínimo:"));
        painel.add(campoEstoqueMinimo);
        painel.add(new JLabel("Fornecedor:"));
        painel.add(campoFornecedor);

        String titulo = produtoExistente == null ? "Novo Produto" : "Editar Produto";
        int opcao = JOptionPane.showConfirmDialog(this, painel, titulo,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opcao != JOptionPane.OK_OPTION) return;

        if (campoNome.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome do produto é obrigatório.",
                    "Campo obrigatório", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double preco;
        try {
            preco = Double.parseDouble(campoPreco.getText().trim().replace(",", "."));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Informe um preço numérico válido (ex.: 9.90).",
                    "Preço inválido", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Fornecedor fornecedor = (Fornecedor) campoFornecedor.getSelectedItem();

        if (produtoExistente == null) {
            Produto novo = new Produto(dataStore.proximoIdProduto(), campoNome.getText().trim(),
                    campoCategoria.getText().trim(), preco,
                    (int) campoEstoqueAtual.getValue(), (int) campoEstoqueMinimo.getValue(), fornecedor);
            dataStore.getProdutos().add(novo);
        } else {
            produtoExistente.setNome(campoNome.getText().trim());
            produtoExistente.setCategoria(campoCategoria.getText().trim());
            produtoExistente.setPreco(preco);
            produtoExistente.setEstoqueAtual((int) campoEstoqueAtual.getValue());
            produtoExistente.setEstoqueMinimo((int) campoEstoqueMinimo.getValue());
            produtoExistente.setFornecedor(fornecedor);
        }
        carregarTabela();
    }

    @Override
    public void refresh() {
        carregarTabela();
    }
}

package com.mercadinhofacil.ui.paineis;

import com.mercadinhofacil.data.DataStore;
import com.mercadinhofacil.model.Fornecedor;
import com.mercadinhofacil.ui.Refreshable;
import com.mercadinhofacil.ui.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Módulo de Gestão de Fornecedores (RF005).
 */
public class FornecedorPanel extends JPanel implements Refreshable {

    private final DataStore dataStore = DataStore.getInstancia();
    private final JTextField campoBusca = new JTextField();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Código", "Nome", "CNPJ", "Contato", "Ações"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tabela = new JTable(modelo);
    private List<Fornecedor> exibidos;

    public FornecedorPanel() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.FUNDO_TELA);
        add(EstiloUI.tituloPagina("Fornecedores"), BorderLayout.NORTH);

        JPanel conteudo = new JPanel(new BorderLayout(0, 15));
        conteudo.setOpaque(false);
        conteudo.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));

        JPanel barra = new JPanel(new BorderLayout());
        barra.setOpaque(false);
        JPanel painelBusca = new JPanel(new BorderLayout());
        painelBusca.setOpaque(false);
        campoBusca.setPreferredSize(new Dimension(260, 32));
        painelBusca.add(new JLabel("\uD83D\uDD0D  "), BorderLayout.WEST);
        painelBusca.add(campoBusca, BorderLayout.CENTER);
        JButton novo = EstiloUI.botaoPrimario("+ Novo Fornecedor");
        novo.addActionListener(e -> abrirFormulario(null));
        barra.add(painelBusca, BorderLayout.WEST);
        barra.add(novo, BorderLayout.EAST);
        conteudo.add(barra, BorderLayout.NORTH);

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

    private void carregarTabela() {
        String filtro = campoBusca.getText().trim().toLowerCase();
        exibidos = dataStore.getFornecedores().stream()
                .filter(f -> filtro.isEmpty() || f.getNome().toLowerCase().contains(filtro))
                .collect(Collectors.toList());

        modelo.setRowCount(0);
        for (Fornecedor f : exibidos) {
            modelo.addRow(new Object[]{
                    String.format("%03d", f.getId()), f.getNome(), f.getCnpj(), f.getContato(), ""});
        }
    }

    private void editar(int linha) {
        abrirFormulario(exibidos.get(linha));
    }

    private void excluir(int linha) {
        Fornecedor fornecedor = exibidos.get(linha);
        boolean possuiProdutos = dataStore.getProdutos().stream()
                .anyMatch(p -> p.getFornecedor() == fornecedor);
        if (possuiProdutos) {
            JOptionPane.showMessageDialog(this,
                    "Este fornecedor possui produtos vinculados e não pode ser excluído.",
                    "Não foi possível excluir", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int opcao = JOptionPane.showConfirmDialog(this,
                "Excluir o fornecedor \"" + fornecedor.getNome() + "\"?", "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION);
        if (opcao == JOptionPane.YES_OPTION) {
            dataStore.getFornecedores().remove(fornecedor);
            carregarTabela();
        }
    }

    private void abrirFormulario(Fornecedor existente) {
        JTextField campoNome = new JTextField(existente != null ? existente.getNome() : "");
        JTextField campoCnpj = new JTextField(existente != null ? existente.getCnpj() : "");
        JTextField campoContato = new JTextField(existente != null ? existente.getContato() : "");

        JPanel painel = new JPanel(new GridLayout(0, 2, 8, 8));
        painel.add(new JLabel("Nome:"));
        painel.add(campoNome);
        painel.add(new JLabel("CNPJ:"));
        painel.add(campoCnpj);
        painel.add(new JLabel("Contato:"));
        painel.add(campoContato);

        String titulo = existente == null ? "Novo Fornecedor" : "Editar Fornecedor";
        int opcao = JOptionPane.showConfirmDialog(this, painel, titulo,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcao != JOptionPane.OK_OPTION) return;

        if (campoNome.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome do fornecedor é obrigatório.",
                    "Campo obrigatório", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (existente == null) {
            Fornecedor novo = new Fornecedor(dataStore.proximoIdFornecedor(), campoNome.getText().trim(),
                    campoCnpj.getText().trim(), campoContato.getText().trim());
            dataStore.getFornecedores().add(novo);
        } else {
            existente.setNome(campoNome.getText().trim());
            existente.setCnpj(campoCnpj.getText().trim());
            existente.setContato(campoContato.getText().trim());
        }
        carregarTabela();
    }

    @Override
    public void refresh() {
        carregarTabela();
    }
}

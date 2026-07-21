package com.mercadinhofacil.ui.paineis;

import com.mercadinhofacil.data.DataStore;
import com.mercadinhofacil.model.Cliente;
import com.mercadinhofacil.ui.Refreshable;
import com.mercadinhofacil.ui.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Módulo de Cadastro de Clientes (RF004).
 */
public class ClientePanel extends JPanel implements Refreshable {

    private final DataStore dataStore = DataStore.getInstancia();
    private final JTextField campoBusca = new JTextField();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Código", "Nome", "CPF", "Telefone", "Ações"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tabela = new JTable(modelo);
    private List<Cliente> exibidos;

    public ClientePanel() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.FUNDO_TELA);
        add(EstiloUI.tituloPagina("Clientes"), BorderLayout.NORTH);

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
        JButton novo = EstiloUI.botaoPrimario("+ Novo Cliente");
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
        exibidos = dataStore.getClientes().stream()
                .filter(c -> filtro.isEmpty() || c.getNome().toLowerCase().contains(filtro))
                .collect(Collectors.toList());

        modelo.setRowCount(0);
        for (Cliente c : exibidos) {
            modelo.addRow(new Object[]{
                    String.format("%03d", c.getId()), c.getNome(), c.getCpf(), c.getTelefone(), ""});
        }
    }

    private void editar(int linha) {
        abrirFormulario(exibidos.get(linha));
    }

    private void excluir(int linha) {
        Cliente cliente = exibidos.get(linha);
        boolean possuiVendas = dataStore.getVendas().stream().anyMatch(v -> v.getCliente() == cliente);
        if (possuiVendas) {
            JOptionPane.showMessageDialog(this,
                    "Este cliente possui vendas registradas e não pode ser excluído.",
                    "Não foi possível excluir", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int opcao = JOptionPane.showConfirmDialog(this,
                "Excluir o cliente \"" + cliente.getNome() + "\"?", "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION);
        if (opcao == JOptionPane.YES_OPTION) {
            dataStore.getClientes().remove(cliente);
            carregarTabela();
        }
    }

    private void abrirFormulario(Cliente existente) {
        JTextField campoNome = new JTextField(existente != null ? existente.getNome() : "");
        JTextField campoCpf = new JTextField(existente != null ? existente.getCpf() : "");
        JTextField campoTelefone = new JTextField(existente != null ? existente.getTelefone() : "");

        JPanel painel = new JPanel(new GridLayout(0, 2, 8, 8));
        painel.add(new JLabel("Nome:"));
        painel.add(campoNome);
        painel.add(new JLabel("CPF:"));
        painel.add(campoCpf);
        painel.add(new JLabel("Telefone:"));
        painel.add(campoTelefone);

        String titulo = existente == null ? "Novo Cliente" : "Editar Cliente";
        int opcao = JOptionPane.showConfirmDialog(this, painel, titulo,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcao != JOptionPane.OK_OPTION) return;

        if (campoNome.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome do cliente é obrigatório.",
                    "Campo obrigatório", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (existente == null) {
            Cliente novo = new Cliente(dataStore.proximoIdCliente(), campoNome.getText().trim(),
                    campoCpf.getText().trim(), campoTelefone.getText().trim());
            dataStore.getClientes().add(novo);
        } else {
            existente.setNome(campoNome.getText().trim());
            existente.setCpf(campoCpf.getText().trim());
            existente.setTelefone(campoTelefone.getText().trim());
        }
        carregarTabela();
    }

    @Override
    public void refresh() {
        carregarTabela();
    }
}

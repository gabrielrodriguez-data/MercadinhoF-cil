package com.mercadinhofacil.ui.paineis;

import com.mercadinhofacil.data.DataStore;
import com.mercadinhofacil.model.Usuario;
import com.mercadinhofacil.ui.Refreshable;
import com.mercadinhofacil.ui.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Módulo de Gestão de Usuários e Controle de Acesso (RNF002).
 */
public class UsuarioPanel extends JPanel implements Refreshable {

    private final DataStore dataStore = DataStore.getInstancia();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nome", "Login", "Permissão", "Ações"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tabela = new JTable(modelo);
    private List<Usuario> exibidos;

    public UsuarioPanel() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.FUNDO_TELA);
        add(EstiloUI.tituloPagina("Usuários"), BorderLayout.NORTH);

        JPanel conteudo = new JPanel(new BorderLayout(0, 15));
        conteudo.setOpaque(false);
        conteudo.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));

        JPanel barra = new JPanel(new BorderLayout());
        barra.setOpaque(false);
        JButton novo = EstiloUI.botaoPrimario("+ Novo Usuário");
        novo.addActionListener(e -> abrirFormulario(null));
        barra.add(novo, BorderLayout.EAST);
        conteudo.add(barra, BorderLayout.NORTH);

        JPanel painelTabela = EstiloUI.painelCartao();
        painelTabela.setLayout(new BorderLayout());
        EstiloUI.estilizarTabela(tabela);
        EstiloUI.configurarColunaAcoes(tabela, "Ações", this::editar, this::excluir);
        painelTabela.add(new JScrollPane(tabela), BorderLayout.CENTER);
        conteudo.add(painelTabela, BorderLayout.CENTER);

        add(conteudo, BorderLayout.CENTER);
        refresh();
    }

    private void carregarTabela() {
        exibidos = dataStore.getUsuarios();
        modelo.setRowCount(0);
        for (Usuario u : exibidos) {
            modelo.addRow(new Object[]{
                    String.format("%03d", u.getId()), u.getNome(), u.getLogin(), u.getPermissao(), ""});
        }
    }

    private void editar(int linha) {
        abrirFormulario(exibidos.get(linha));
    }

    private void excluir(int linha) {
        Usuario usuario = exibidos.get(linha);
        if (usuario == dataStore.getUsuarioLogado()) {
            JOptionPane.showMessageDialog(this,
                    "Não é possível excluir o usuário atualmente logado no sistema.",
                    "Operação não permitida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int opcao = JOptionPane.showConfirmDialog(this,
                "Excluir o usuário \"" + usuario.getNome() + "\"?", "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION);
        if (opcao == JOptionPane.YES_OPTION) {
            dataStore.getUsuarios().remove(usuario);
            carregarTabela();
        }
    }

    private void abrirFormulario(Usuario existente) {
        JTextField campoNome = new JTextField(existente != null ? existente.getNome() : "");
        JTextField campoLogin = new JTextField(existente != null ? existente.getLogin() : "");
        JPasswordField campoSenha = new JPasswordField(existente != null ? existente.getSenha() : "");
        JComboBox<Usuario.Permissao> campoPermissao = new JComboBox<>(Usuario.Permissao.values());
        if (existente != null) campoPermissao.setSelectedItem(existente.getPermissao());

        JPanel painel = new JPanel(new GridLayout(0, 2, 8, 8));
        painel.add(new JLabel("Nome:"));
        painel.add(campoNome);
        painel.add(new JLabel("Login:"));
        painel.add(campoLogin);
        painel.add(new JLabel("Senha:"));
        painel.add(campoSenha);
        painel.add(new JLabel("Permissão:"));
        painel.add(campoPermissao);

        String titulo = existente == null ? "Novo Usuário" : "Editar Usuário";
        int opcao = JOptionPane.showConfirmDialog(this, painel, titulo,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcao != JOptionPane.OK_OPTION) return;

        if (campoNome.getText().trim().isEmpty() || campoLogin.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e login são obrigatórios.",
                    "Campos obrigatórios", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String senha = new String(campoSenha.getPassword());
        Usuario.Permissao permissao = (Usuario.Permissao) campoPermissao.getSelectedItem();

        if (existente == null) {
            Usuario novo = new Usuario(dataStore.proximoIdUsuario(), campoNome.getText().trim(),
                    campoLogin.getText().trim(), senha, permissao);
            dataStore.getUsuarios().add(novo);
        } else {
            existente.setNome(campoNome.getText().trim());
            existente.setLogin(campoLogin.getText().trim());
            existente.setSenha(senha);
            existente.setPermissao(permissao);
        }
        carregarTabela();
    }

    @Override
    public void refresh() {
        carregarTabela();
    }
}

package com.mercadinhofacil.ui;

import com.mercadinhofacil.data.DataStore;
import com.mercadinhofacil.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Interface de Autenticação de Usuário (tela "Acesso ao Sistema" da Etapa 2).
 * Porta de entrada do sistema Mercadinho Fácil.
 */
public class LoginFrame extends JFrame {

    private final JTextField campoUsuario = new JTextField();
    private final JPasswordField campoSenha = new JPasswordField();

    public LoginFrame() {
        super("Mercadinho Fácil - Acesso ao Sistema");
        montarTela();
    }

    private void montarTela() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel fundo = new JPanel(new GridBagLayout());
        fundo.setBackground(new Color(0xD8, 0xDC, 0xE0));
        setContentPane(fundo);

        JLabel marca = new JLabel("Mercadinho Fácil", SwingConstants.RIGHT);
        marca.setFont(new Font("SansSerif", Font.PLAIN, 18));
        GridBagConstraints gbcMarca = new GridBagConstraints();
        gbcMarca.gridx = 0;
        gbcMarca.gridy = 0;
        gbcMarca.anchor = GridBagConstraints.NORTHEAST;
        gbcMarca.insets = new Insets(20, 0, 0, 40);
        fundo.add(marca, gbcMarca);

        JPanel cartao = new JPanel();
        cartao.setBackground(UIConstants.BRANCO);
        cartao.setLayout(new BoxLayout(cartao, BoxLayout.Y_AXIS));
        cartao.setBorder(BorderFactory.createEmptyBorder(35, 45, 35, 45));
        cartao.setPreferredSize(new Dimension(340, 300));

        JLabel titulo = new JLabel("Acesso ao Sistema");
        titulo.setFont(UIConstants.FONTE_SUBTITULO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        cartao.add(titulo);
        cartao.add(Box.createVerticalStrut(25));
        cartao.add(criarCampo("Usuário", campoUsuario));
        cartao.add(Box.createVerticalStrut(15));
        cartao.add(criarCampo("Senha", campoSenha));
        cartao.add(Box.createVerticalStrut(25));

        JButton botaoEntrar = new JButton("ENTRAR");
        botaoEntrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        botaoEntrar.setBackground(UIConstants.VERDE);
        botaoEntrar.setForeground(Color.WHITE);
        botaoEntrar.setFont(UIConstants.FONTE_BOTAO);
        botaoEntrar.setFocusPainted(false);
        botaoEntrar.setMaximumSize(new Dimension(240, 40));
        botaoEntrar.addActionListener(this::autenticar);
        cartao.add(botaoEntrar);

        JLabel dica = new JLabel("Dica: gabriel / joao / maria / pedro — senha: 123");
        dica.setForeground(UIConstants.TEXTO_CINZA);
        dica.setFont(new Font("SansSerif", Font.PLAIN, 10));
        dica.setAlignmentX(Component.CENTER_ALIGNMENT);
        cartao.add(Box.createVerticalStrut(15));
        cartao.add(dica);

        // Permite pressionar Enter no campo de senha para logar
        campoSenha.addActionListener(this::autenticar);

        GridBagConstraints gbcCartao = new GridBagConstraints();
        gbcCartao.gridx = 0;
        gbcCartao.gridy = 1;
        gbcCartao.weighty = 1;
        fundo.add(cartao, gbcCartao);
    }

    private JPanel criarCampo(String rotulo, JTextField campo) {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(UIConstants.BRANCO);
        painel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel(rotulo, SwingConstants.CENTER);
        label.setFont(UIConstants.FONTE_NORMAL);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        campo.setMaximumSize(new Dimension(240, 30));
        campo.setAlignmentX(Component.CENTER_ALIGNMENT);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDA),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));

        painel.add(label);
        painel.add(Box.createVerticalStrut(4));
        painel.add(campo);
        return painel;
    }

    private void autenticar(ActionEvent e) {
        String login = campoUsuario.getText().trim();
        String senha = new String(campoSenha.getPassword());

        Usuario usuario = DataStore.getInstancia().autenticar(login, senha);
        if (usuario == null) {
            JOptionPane.showMessageDialog(this,
                    "Usuário ou senha inválidos.",
                    "Falha no acesso", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DataStore.getInstancia().setUsuarioLogado(usuario);
        dispose();
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}

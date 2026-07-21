package com.mercadinhofacil.ui;

import java.awt.*;

/**
 * Paleta e tipografia definidas no documento de UX da Etapa 2
 * ("Design System Efficient Commerce"): verde (#27AE60) para ações/sucesso,
 * tons de cinza/azul-marinho para neutralidade.
 */
public final class UIConstants {

    private UIConstants() { }

    public static final Color VERDE = new Color(0x27, 0xAE, 0x60);
    public static final Color VERDE_ESCURO = new Color(0x1E, 0x8E, 0x4D);
    public static final Color VERMELHO = new Color(0xE7, 0x4C, 0x3C);
    public static final Color LARANJA = new Color(0xF3, 0x9C, 0x12);
    public static final Color AZUL = new Color(0x29, 0x80, 0xB9);
    public static final Color ROXO = new Color(0x8E, 0x44, 0xAD);

    public static final Color MENU_FUNDO = new Color(0x1B, 0x2A, 0x41);
    public static final Color MENU_SELECIONADO = new Color(0x27, 0x3C, 0x5A);
    public static final Color FUNDO_TELA = new Color(0xF4, 0xF6, 0xF8);
    public static final Color BRANCO = Color.WHITE;
    public static final Color TEXTO_CINZA = new Color(0x6B, 0x7A, 0x8F);
    public static final Color BORDA = new Color(0xE0, 0xE4, 0xE8);

    public static final Font FONTE_TITULO = new Font("SansSerif", Font.BOLD, 22);
    public static final Font FONTE_SUBTITULO = new Font("SansSerif", Font.BOLD, 15);
    public static final Font FONTE_NORMAL = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONTE_MENU = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONTE_BOTAO = new Font("SansSerif", Font.BOLD, 13);
}

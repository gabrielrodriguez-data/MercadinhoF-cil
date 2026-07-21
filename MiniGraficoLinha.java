package com.mercadinhofacil.ui.paineis;

import com.mercadinhofacil.ui.UIConstants;

import javax.swing.*;
import java.awt.*;

/**
 * Gráfico de linha simples desenhado com Graphics2D, sem dependências
 * externas, usado na tela Dashboard ("Vendas dos últimos 7 dias").
 */
class MiniGraficoLinha extends JPanel {

    private double[] valores = new double[0];
    private String[] rotulos = new String[0];

    MiniGraficoLinha() {
        setBackground(UIConstants.BRANCO);
        setPreferredSize(new Dimension(400, 180));
    }

    void setDados(double[] valores, String[] rotulos) {
        this.valores = valores;
        this.rotulos = rotulos;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (valores.length == 0) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int margem = 28;
        int largura = getWidth() - margem * 2;
        int altura = getHeight() - margem * 2;

        double max = 0;
        for (double v : valores) max = Math.max(max, v);
        if (max == 0) max = 1;

        int n = valores.length;
        int[] xs = new int[n];
        int[] ys = new int[n];
        for (int i = 0; i < n; i++) {
            xs[i] = margem + (n == 1 ? 0 : (int) (i * (largura / (double) (n - 1))));
            ys[i] = margem + altura - (int) ((valores[i] / max) * altura);
        }

        // linhas guia horizontais
        g2.setColor(UIConstants.BORDA);
        for (int i = 0; i <= 3; i++) {
            int y = margem + (altura * i / 3);
            g2.drawLine(margem, y, margem + largura, y);
        }

        // área sob a linha
        int[] xsArea = new int[n + 2];
        int[] ysArea = new int[n + 2];
        System.arraycopy(xs, 0, xsArea, 0, n);
        System.arraycopy(ys, 0, ysArea, 0, n);
        xsArea[n] = xs[n - 1];
        ysArea[n] = margem + altura;
        xsArea[n + 1] = xs[0];
        ysArea[n + 1] = margem + altura;
        g2.setColor(new Color(0x27, 0xAE, 0x60, 40));
        g2.fillPolygon(xsArea, ysArea, n + 2);

        // linha
        g2.setColor(UIConstants.VERDE);
        g2.setStroke(new BasicStroke(2.2f));
        for (int i = 0; i < n - 1; i++) {
            g2.drawLine(xs[i], ys[i], xs[i + 1], ys[i + 1]);
        }

        // pontos e rótulos
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        for (int i = 0; i < n; i++) {
            g2.setColor(UIConstants.VERDE);
            g2.fillOval(xs[i] - 3, ys[i] - 3, 6, 6);
            if (rotulos != null && i < rotulos.length) {
                g2.setColor(UIConstants.TEXTO_CINZA);
                FontMetrics fm = g2.getFontMetrics();
                int lw = fm.stringWidth(rotulos[i]);
                g2.drawString(rotulos[i], xs[i] - lw / 2, getHeight() - 6);
            }
        }
    }
}

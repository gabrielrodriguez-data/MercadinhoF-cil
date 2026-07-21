package com.mercadinhofacil.data;

import com.mercadinhofacil.model.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Repositório único em memória (padrão Singleton).
 *
 * A Etapa 3 pede telas funcionais e interações que NÃO dependam de banco de
 * dados real. Esta classe cumpre o papel que, futuramente, será assumido
 * pelas classes DAO (ProdutoDAO, ClienteDAO, etc.) mencionadas no documento
 * de UX da Etapa 2 — hoje ela apenas guarda os dados em listas (ArrayList),
 * simulando as tabelas do arquivo etapa_3.sql.
 */
public class DataStore {

    private static DataStore instancia;

    private final List<Fornecedor> fornecedores = new ArrayList<>();
    private final List<Produto> produtos = new ArrayList<>();
    private final List<Cliente> clientes = new ArrayList<>();
    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Venda> vendas = new ArrayList<>();
    private final List<MovimentacaoEstoque> movimentacoes = new ArrayList<>();
    private final List<MovimentoCaixa> caixa = new ArrayList<>();

    private double saldoInicialCaixa = 2000.00;

    /** Totais de venda por dia, usados apenas para alimentar o gráfico do Dashboard. */
    private final Map<LocalDate, Double> vendasPorDia = new LinkedHashMap<>();

    /** Usuário autenticado na sessão atual (definido pela tela de Login). */
    private Usuario usuarioLogado;

    private int proxIdFornecedor = 1;
    private int proxIdProduto = 1;
    private int proxIdCliente = 1;
    private int proxIdUsuario = 1;
    private int proxIdVenda = 1;
    private int proxIdMovimentacao = 1;

    private DataStore() {
        carregarDadosDeExemplo();
    }

    public static synchronized DataStore getInstancia() {
        if (instancia == null) {
            instancia = new DataStore();
        }
        return instancia;
    }

    // ---------------------------------------------------------------
    // Carga inicial (equivalente aos INSERTs de etapa_3.sql)
    // ---------------------------------------------------------------
    private void carregarDadosDeExemplo() {
        Fornecedor f1 = new Fornecedor(proxIdFornecedor++, "Distribuidora Silva", "12.345.678/0001-90", "contato@silva.com");
        Fornecedor f2 = new Fornecedor(proxIdFornecedor++, "Alimentos Bahia", "98.765.432/0001-21", "contato@bahia.com");
        Fornecedor f3 = new Fornecedor(proxIdFornecedor++, "Central Hortifruti", "11.222.333/0001-44", "contato@hortifruti.com");
        Fornecedor f4 = new Fornecedor(proxIdFornecedor++, "Pães & Cia", "22.333.444/0001-55", "contato@paescia.com");
        Fornecedor f5 = new Fornecedor(proxIdFornecedor++, "Bebidas Nordeste", "33.444.555/0001-66", "contato@bebidas.com");
        fornecedores.add(f1); fornecedores.add(f2); fornecedores.add(f3); fornecedores.add(f4); fornecedores.add(f5);

        produtos.add(new Produto(proxIdProduto++, "Arroz 5kg", "Cereais", 28.90, 8, 5, f1));
        produtos.add(new Produto(proxIdProduto++, "Feijão 1kg", "Cereais", 8.99, 4, 12, f2));
        produtos.add(new Produto(proxIdProduto++, "Leite Integral 1L", "Laticínios", 4.99, 4, 10, f3));
        produtos.add(new Produto(proxIdProduto++, "Café 500g", "Bebidas", 15.00, 2, 5, f5));
        produtos.add(new Produto(proxIdProduto++, "Óleo de Soja 900ml", "Mercearia", 6.49, 1, 4, f2));
        produtos.add(new Produto(proxIdProduto++, "Açúcar 1kg", "Mercearia", 3.49, 6, 5, f2));
        produtos.add(new Produto(proxIdProduto++, "Sal Refinado 1kg", "Mercearia", 1.79, 8, 5, f2));
        produtos.add(new Produto(proxIdProduto++, "Pão Francês", "Panificação", 0.59, 200, 50, f4));
        produtos.add(new Produto(proxIdProduto++, "Refrigerante Cola 2L", "Bebidas", 10.50, 40, 10, f5));

        clientes.add(new Cliente(proxIdCliente++, "João da Silva", "123.456.789-00", "(71) 99999-9999"));
        clientes.add(new Cliente(proxIdCliente++, "Maria Souza", "987.654.321-00", "(71) 98888-8888"));
        clientes.add(new Cliente(proxIdCliente++, "Pedro Oliveira", "456.789.123-00", "(71) 97777-7777"));
        clientes.add(new Cliente(proxIdCliente++, "Ana Lima", "321.654.987-00", "(71) 96666-6666"));
        clientes.add(new Cliente(proxIdCliente++, "Carlos Pereira", "159.753.456-00", "(71) 95555-5555"));

        Usuario gerente = new Usuario(proxIdUsuario++, "Gabriel Andrade", "gabriel", "123", Usuario.Permissao.GERENTE);
        usuarios.add(gerente);
        usuarios.add(new Usuario(proxIdUsuario++, "João Silva", "joao", "123", Usuario.Permissao.ATENDENTE));
        usuarios.add(new Usuario(proxIdUsuario++, "Maria Oliveira", "maria", "123", Usuario.Permissao.FINANCEIRO));
        usuarios.add(new Usuario(proxIdUsuario++, "Pedro Santos", "pedro", "123", Usuario.Permissao.CAIXA));

        LocalDate hoje = LocalDate.now();

        // Movimentações de estoque de exemplo (auditoria)
        movimentacoes.add(new MovimentacaoEstoque(proxIdMovimentacao++, produtos.get(0), MovimentacaoEstoque.Tipo.ENTRADA, 50, hoje, gerente, "Compra NF 1234"));
        movimentacoes.add(new MovimentacaoEstoque(proxIdMovimentacao++, produtos.get(1), MovimentacaoEstoque.Tipo.SAIDA, 5, hoje, gerente, "Venda"));
        movimentacoes.add(new MovimentacaoEstoque(proxIdMovimentacao++, produtos.get(3), MovimentacaoEstoque.Tipo.ENTRADA, 20, hoje, gerente, "Compra NF 1230"));
        movimentacoes.add(new MovimentacaoEstoque(proxIdMovimentacao++, produtos.get(4), MovimentacaoEstoque.Tipo.AJUSTE, -1, hoje, gerente, "Perda"));

        // Movimentações de caixa de exemplo (log do dia)
        caixa.add(new MovimentoCaixa(hoje, LocalTime.of(8, 10), MovimentoCaixa.Tipo.ENTRADA, "Venda à vista", 120.00));
        caixa.add(new MovimentoCaixa(hoje, LocalTime.of(9, 30), MovimentoCaixa.Tipo.ENTRADA, "Venda à vista", 85.50));
        caixa.add(new MovimentoCaixa(hoje, LocalTime.of(10, 45), MovimentoCaixa.Tipo.SAIDA, "Compra de mercadorias", 650.00));
        caixa.add(new MovimentoCaixa(hoje, LocalTime.of(14, 20), MovimentoCaixa.Tipo.ENTRADA, "Venda à vista", 230.00));
        caixa.add(new MovimentoCaixa(hoje, LocalTime.of(16, 0), MovimentoCaixa.Tipo.SAIDA, "Despesa diversa", 150.00));

        // Histórico simulado dos últimos 6 dias, apenas para desenhar o gráfico do Dashboard
        double[] historico = {620.00, 540.00, 780.00, 910.00, 860.00, 1080.00};
        for (int i = historico.length; i >= 1; i--) {
            vendasPorDia.put(hoje.minusDays(i), historico[historico.length - i]);
        }
    }

    // ---------------------------------------------------------------
    // Getters das "tabelas"
    // ---------------------------------------------------------------
    public List<Fornecedor> getFornecedores() { return fornecedores; }
    public List<Produto> getProdutos() { return produtos; }
    public List<Cliente> getClientes() { return clientes; }
    public List<Usuario> getUsuarios() { return usuarios; }
    public List<Venda> getVendas() { return vendas; }
    public List<MovimentacaoEstoque> getMovimentacoes() { return movimentacoes; }
    public List<MovimentoCaixa> getCaixa() { return caixa; }

    public double getSaldoInicialCaixa() { return saldoInicialCaixa; }

    public Usuario getUsuarioLogado() { return usuarioLogado; }
    public void setUsuarioLogado(Usuario usuarioLogado) { this.usuarioLogado = usuarioLogado; }

    // ---------------------------------------------------------------
    // Operações de negócio (equivalentes ao que os *DAO.salvar()/excluir() farão)
    // ---------------------------------------------------------------
    public int proximoIdFornecedor() { return proxIdFornecedor++; }
    public int proximoIdProduto() { return proxIdProduto++; }
    public int proximoIdCliente() { return proxIdCliente++; }
    public int proximoIdUsuario() { return proxIdUsuario++; }
    public int proximoIdMovimentacao() { return proxIdMovimentacao++; }

    /** Autentica pelo login/senha informados na tela de Acesso ao Sistema. */
    public Usuario autenticar(String login, String senha) {
        for (Usuario u : usuarios) {
            if (u.getLogin().equalsIgnoreCase(login) && u.getSenha().equals(senha)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Registra uma venda finalizada: adiciona à lista de vendas, baixa o
     * estoque de cada produto envolvido e lança a entrada correspondente no
     * caixa do dia — exatamente o fluxo descrito na Etapa 2 para a tela de
     * Vendas (venda -> itens_venda -> produto -> movimentacaoestoque).
     */
    public void registrarVenda(Venda venda) {
        vendas.add(venda);
        for (ItemVenda item : venda.getItens()) {
            Produto p = item.getProduto();
            p.setEstoqueAtual(p.getEstoqueAtual() - item.getQuantidade());
            movimentacoes.add(new MovimentacaoEstoque(
                    proximoIdMovimentacao(), p, MovimentacaoEstoque.Tipo.SAIDA,
                    item.getQuantidade(), venda.getData(), venda.getVendedor(), "Venda"));
        }
        caixa.add(new MovimentoCaixa(venda.getData(), LocalTime.now(),
                MovimentoCaixa.Tipo.ENTRADA, "Venda #" + venda.getId(), venda.getValorTotal()));

        vendasPorDia.merge(venda.getData(), venda.getValorTotal(), Double::sum);
    }

    /**
     * Retorna os valores vendidos e os rótulos (dia da semana) dos últimos 7
     * dias corridos, usados no gráfico de linha do Dashboard.
     */
    public double[] getValoresUltimos7Dias() {
        LocalDate hoje = LocalDate.now();
        double[] valores = new double[7];
        for (int i = 0; i < 7; i++) {
            LocalDate dia = hoje.minusDays(6 - i);
            valores[i] = vendasPorDia.getOrDefault(dia, 0.0);
        }
        return valores;
    }

    public String[] getRotulosUltimos7Dias() {
        LocalDate hoje = LocalDate.now();
        String[] rotulos = new String[7];
        for (int i = 0; i < 7; i++) {
            LocalDate dia = hoje.minusDays(6 - i);
            rotulos[i] = dia.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("pt", "BR"));
        }
        return rotulos;
    }

    /** Aplica uma movimentação manual de estoque (Entrada, Saída ou Ajuste). */
    public void registrarMovimentacao(MovimentacaoEstoque mov) {
        movimentacoes.add(mov);
        Produto p = mov.getProduto();
        switch (mov.getTipo()) {
            case ENTRADA -> p.setEstoqueAtual(p.getEstoqueAtual() + mov.getQuantidade());
            case SAIDA -> p.setEstoqueAtual(p.getEstoqueAtual() - mov.getQuantidade());
            case AJUSTE -> p.setEstoqueAtual(p.getEstoqueAtual() + mov.getQuantidade());
        }
    }

    public double getTotalEntradasHoje() {
        return somaCaixa(MovimentoCaixa.Tipo.ENTRADA);
    }

    public double getTotalSaidasHoje() {
        return somaCaixa(MovimentoCaixa.Tipo.SAIDA);
    }

    private double somaCaixa(MovimentoCaixa.Tipo tipo) {
        double total = 0;
        for (MovimentoCaixa m : caixa) {
            if (m.getTipo() == tipo) {
                total += m.getValor();
            }
        }
        return total;
    }

    public double getSaldoAtualCaixa() {
        return saldoInicialCaixa + getTotalEntradasHoje() - getTotalSaidasHoje();
    }

    public double getVendasHoje() {
        double total = 0;
        LocalDate hoje = LocalDate.now();
        for (Venda v : vendas) {
            if (v.getData().equals(hoje)) {
                total += v.getValorTotal();
            }
        }
        return total;
    }

    public List<Produto> getProdutosEstoqueBaixo() {
        List<Produto> lista = new ArrayList<>();
        for (Produto p : produtos) {
            if (p.isEstoqueBaixo()) {
                lista.add(p);
            }
        }
        return lista;
    }
}

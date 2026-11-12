package swing;

import domain.Aresta;
import domain.Grafo;
import domain.Kruskal;
import domain.Ponto;
import fixos.GrafosExemplo;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

// Janela única: botões no topo, lista de ecopontos à esquerda e, no centro,
// duas abas: o resultado do Kruskal e a tabela com as ligações dos ecopontos
public class JanelaPrincipal extends JFrame {

    private static final DecimalFormat FORMATO =
            new DecimalFormat("#.##", new DecimalFormatSymbols(new Locale("pt", "BR")));

    // grafo dos ecopontos: começa com os 5 originais e recebe os pontos cadastrados
    private final Grafo ecopontos = GrafosExemplo.grafoEcopontos();
    private final Grafo grafoAF = GrafosExemplo.grafoAF();

    private final DefaultListModel<String> modeloLista = new DefaultListModel<>();
    private final JList<String> lista = new JList<>(modeloLista);
    private final JTextArea areaResultado = new JTextArea();
    private final JTabbedPane abas = new JTabbedPane();
    private final DefaultTableModel modeloLigacoes = criarModeloLigacoes();
    private final JTable tabelaLigacoes = new JTable(modeloLigacoes);

    // guardados para poder mudar a fonte quando a janela muda de tamanho
    private final JButton botaoCadastrar = new JButton("Cadastrar ponto");
    private final JButton botaoLigacao = new JButton("Nova ligação");
    private final JButton botaoEcopontos = new JButton("Kruskal ecopontos");
    private final JButton botaoAF = new JButton("Kruskal A–F");
    private final TitledBorder bordaLista = BorderFactory.createTitledBorder("Ecopontos cadastrados");
    private JScrollPane rolagemLista;

    public JanelaPrincipal() {
        super("Kruskal - Ecopontos Comurg");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        add(criarBarraBotoes(), BorderLayout.NORTH);
        add(criarLista(), BorderLayout.WEST);
        add(criarAbas(), BorderLayout.CENTER);

        atualizarLista();
        exibirLigacoes(ecopontos);
        setSize(1100, 600);
        setMinimumSize(new Dimension(800, 450));
        setLocationRelativeTo(null); // centraliza na tela

        // quando a janela muda de tamanho (ex.: tela cheia), o texto acompanha
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                ajustarTamanhos();
            }
        });
        ajustarTamanhos();
    }

    // Ajusta fontes e a largura da lista de acordo com a largura da janela.
    // A conta garante que a linha mais longa do resultado (~85 caracteres) caiba sem cortar.
    private void ajustarTamanhos() {
        int largura = getWidth();
        int larguraLista = (int) (largura * 0.22);
        int larguraResultado = largura - larguraLista - 60;

        // numa fonte monoespaçada cada letra ocupa ~0,6 do tamanho da fonte
        int tamanhoTexto = limitar((int) (larguraResultado / (85 * 0.6)), 14, 30);
        int tamanhoBotoes = limitar(tamanhoTexto - 2, 14, 24);

        Font fonteTexto = new Font(Font.MONOSPACED, Font.PLAIN, tamanhoTexto);
        Font fonteLista = new Font(Font.SANS_SERIF, Font.PLAIN, tamanhoTexto);
        Font fonteBotoes = new Font(Font.SANS_SERIF, Font.BOLD, tamanhoBotoes);

        areaResultado.setFont(fonteTexto);
        lista.setFont(fonteLista);
        for (JButton b : List.of(botaoCadastrar, botaoLigacao, botaoEcopontos, botaoAF)) {
            b.setFont(fonteBotoes);
        }
        bordaLista.setTitleFont(fonteBotoes);
        abas.setFont(fonteBotoes);

        // tabela: fonte do conteúdo, do cabeçalho e altura das linhas acompanham o tamanho
        tabelaLigacoes.setFont(fonteLista);
        tabelaLigacoes.getTableHeader().setFont(fonteBotoes);
        tabelaLigacoes.setRowHeight((int) (tamanhoTexto * 1.6));

        rolagemLista.setPreferredSize(new Dimension(larguraLista, 0));
        revalidate();
        repaint();
    }

    private int limitar(int valor, int minimo, int maximo) {
        return Math.max(minimo, Math.min(maximo, valor));
    }

    // barra com os 3 botões
    private JPanel criarBarraBotoes() {
        botaoCadastrar.addActionListener(e -> cadastrarPonto());
        botaoLigacao.addActionListener(e -> novaLigacao());
        botaoEcopontos.addActionListener(e -> mostrarResultado(
                "=== Ecopontos Comurg (peso = média ida/volta em km) ===", ecopontos));
        botaoAF.addActionListener(e -> mostrarResultado(
                "=== Grafo A–F (peso em horas) ===", grafoAF));

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        barra.add(botaoCadastrar);
        barra.add(botaoLigacao);
        barra.add(botaoEcopontos);
        barra.add(botaoAF);
        return barra;
    }

    // lista com os nomes dos ecopontos atuais
    private JScrollPane criarLista() {
        rolagemLista = new JScrollPane(lista);
        rolagemLista.setBorder(bordaLista);
        return rolagemLista;
    }

    // as duas abas do centro
    private JTabbedPane criarAbas() {
        abas.addTab("Resultado", criarAreaResultado());
        abas.addTab("Ligações", criarTabelaLigacoes());
        return abas;
    }

    // área de texto onde o Kruskal escreve aceitas, descartadas e custo total
    private JScrollPane criarAreaResultado() {
        areaResultado.setEditable(false);
        areaResultado.setMargin(new Insets(8, 10, 8, 10)); // respiro entre o texto e a borda
        return new JScrollPane(areaResultado);
    }

    // modelo da tabela: 3 colunas e nenhuma célula editável
    private static DefaultTableModel criarModeloLigacoes() {
        return new DefaultTableModel(new Object[]{"Origem", "Destino", "Peso"}, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }

            // a coluna de distância guarda Double, para ordenar como número e não como texto
            @Override
            public Class<?> getColumnClass(int coluna) {
                return coluna == 2 ? Double.class : String.class;
            }
        };
    }

    // tabela com todas as ligações; clicar no cabeçalho ordena pela coluna
    private JScrollPane criarTabelaLigacoes() {
        tabelaLigacoes.setAutoCreateRowSorter(true);
        tabelaLigacoes.getTableHeader().setReorderingAllowed(false);

        // mostra a distância no formato brasileiro (25,85), alinhada à direita
        DefaultTableCellRenderer formatoDistancia = new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object valor) {
                setText(valor == null ? "" : FORMATO.format(valor));
            }
        };
        formatoDistancia.setHorizontalAlignment(SwingConstants.RIGHT);
        tabelaLigacoes.getColumnModel().getColumn(2).setCellRenderer(formatoDistancia);

        return new JScrollPane(tabelaLigacoes);
    }

    // abre o diálogo modal; se o usuário salvar, atualiza a lista e a tabela
    private void cadastrarPonto() {
        DialogoCadastro dialogo = new DialogoCadastro(this, ecopontos);
        dialogo.setVisible(true); // modal: o código só continua quando o diálogo fecha
        if (dialogo.isSalvo()) {
            atualizarLista();
            exibirLigacoes(ecopontos); // o ponto novo é dos ecopontos
        }
    }

    // abre o diálogo de ligação entre pontos existentes; se salvar, atualiza a tabela e mostra a aba
    private void novaLigacao() {
        DialogoLigacao dialogo = new DialogoLigacao(this, ecopontos);
        dialogo.setVisible(true);
        if (dialogo.isSalvo()) {
            exibirLigacoes(ecopontos); // a ligação nova é dos ecopontos
            abas.setSelectedIndex(1);  // aba Ligações, para ver a ligação nova
        }
    }

    private void mostrarResultado(String titulo, Grafo grafo) {
        // true = testa todas as ligações, mesmo depois da árvore completa
        areaResultado.setText(titulo + "\n" + Kruskal.executar(grafo, true));
        areaResultado.setCaretPosition(0); // volta para o topo do texto
        exibirLigacoes(grafo);             // a aba Ligações passa a mostrar este mesmo grafo
        abas.setSelectedIndex(0);          // garante que a aba Resultado fique visível
    }

    private void atualizarLista() {
        modeloLista.clear();
        for (Ponto p : ecopontos.getPontos()) {
            modeloLista.addElement(p.getNome());
        }
    }

    // Preenche a aba Ligações com as arestas do grafo informado.
    // O título da aba e o cabeçalho do peso mudam conforme o grafo (km nos ecopontos, h no A–F).
    private void exibirLigacoes(Grafo grafo) {
        boolean ehAF = grafo == grafoAF;

        modeloLigacoes.setRowCount(0);
        for (Aresta a : grafo.getArestas()) {
            modeloLigacoes.addRow(new Object[]{a.getPonto1().getNome(), a.getPonto2().getNome(), a.getPeso()});
        }

        abas.setTitleAt(1, ehAF ? "Ligações (A–F)" : "Ligações (ecopontos)");
        String cabecalho = ehAF ? "Tempo (h)" : "Distância média (km)";
        tabelaLigacoes.getColumnModel().getColumn(2).setHeaderValue(cabecalho);
        tabelaLigacoes.getTableHeader().repaint();
    }
}

package swing;

import domain.Aresta;
import domain.Grafo;
import domain.Ponto;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

// Diálogo modal para criar uma ligação nova entre dois pontos que já existem.
// O segundo combo só mostra os pontos que ainda NÃO têm ligação com o primeiro,
// então não tem como repetir uma ligação nem ligar um ponto a ele mesmo.
public class DialogoLigacao extends JDialog {

    private final Grafo grafo;
    private final List<Ponto> todos;
    private final List<Ponto> destinosDisponiveis = new ArrayList<>();
    private boolean salvo = false;

    private final JComboBox<String> comboOrigem = new JComboBox<>();
    private final JComboBox<String> comboDestino = new JComboBox<>();
    private final JTextField campoIda = new JTextField(6);
    private final JTextField campoVolta = new JTextField(6);
    private final JLabel aviso = new JLabel(" ");
    private final JButton botaoSalvar = new JButton("Salvar");

    public DialogoLigacao(JFrame dono, Grafo grafo) {
        super(dono, "Nova ligação", true); // true = modal
        this.grafo = grafo;
        this.todos = grafo.getPontos();

        for (Ponto p : todos) {
            comboOrigem.addItem(p.getNome());
        }
        // trocar a origem refaz a lista de destinos possíveis
        comboOrigem.addActionListener(e -> atualizarDestinos());

        setLayout(new BorderLayout(8, 8));
        add(criarFormulario(), BorderLayout.CENTER);
        add(criarBotoes(), BorderLayout.SOUTH);

        atualizarDestinos();
        // calcula o tamanho e corrige de novo quando a janela abrir (evita os botões cortados no Linux)
        AjusteDialogo.preparar(this);
    }

    public boolean isSalvo() {
        return salvo;
    }

    private JPanel criarFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        adicionarLinha(painel, c, 0, "Ponto 1:", comboOrigem);
        adicionarLinha(painel, c, 1, "Ponto 2:", comboDestino);

        JPanel distancias = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        distancias.add(new JLabel("Ida (km):"));
        distancias.add(campoIda);
        distancias.add(new JLabel("Volta (km):"));
        distancias.add(campoVolta);
        adicionarLinha(painel, c, 2, "Distâncias:", distancias);

        // Enter no campo de volta também salva
        campoVolta.addActionListener(e -> salvar());

        c.gridx = 1;
        c.gridy = 3;
        painel.add(aviso, c);
        return painel;
    }

    private void adicionarLinha(JPanel painel, GridBagConstraints c, int linha, String rotulo, Component campo) {
        c.gridx = 0;
        c.gridy = linha;
        c.weightx = 0;
        painel.add(new JLabel(rotulo), c);
        c.gridx = 1;
        c.weightx = 1;
        painel.add(campo, c);
    }

    private JPanel criarBotoes() {
        JButton botaoCancelar = new JButton("Cancelar");
        botaoSalvar.addActionListener(e -> salvar());
        botaoCancelar.addActionListener(e -> dispose());

        JPanel painel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painel.add(botaoSalvar);
        painel.add(botaoCancelar);
        return painel;
    }

    // monta o segundo combo só com pontos diferentes da origem e ainda sem ligação com ela
    private void atualizarDestinos() {
        destinosDisponiveis.clear();
        comboDestino.removeAllItems();

        int indice = comboOrigem.getSelectedIndex();
        if (indice < 0) return;
        Ponto origem = todos.get(indice);

        for (Ponto p : todos) {
            if (p != origem && !existeLigacao(origem, p)) {
                destinosDisponiveis.add(p);
                comboDestino.addItem(p.getNome());
            }
        }

        boolean temDestino = !destinosDisponiveis.isEmpty();
        comboDestino.setEnabled(temDestino);
        campoIda.setEnabled(temDestino);
        campoVolta.setEnabled(temDestino);
        botaoSalvar.setEnabled(temDestino);
        aviso.setText(temDestino ? " " : origem.getNome() + " já está ligado a todos os pontos.");
    }

    private boolean existeLigacao(Ponto a, Ponto b) {
        for (Aresta ar : grafo.getArestas()) {
            if ((ar.getPonto1() == a && ar.getPonto2() == b) || (ar.getPonto1() == b && ar.getPonto2() == a)) {
                return true;
            }
        }
        return false;
    }

    private void salvar() {
        int iOrigem = comboOrigem.getSelectedIndex();
        int iDestino = comboDestino.getSelectedIndex();
        if (iOrigem < 0 || iDestino < 0) {
            Validacoes.erro(this, "Escolha os dois pontos da ligação.");
            return;
        }
        Ponto origem = todos.get(iOrigem);
        Ponto destino = destinosDisponiveis.get(iDestino);

        Double ida = Validacoes.lerDistancia(this, campoIda, "ida");
        if (ida == null) return;
        Double volta = Validacoes.lerDistancia(this, campoVolta, "volta");
        if (volta == null) return;

        try {
            grafo.adicionarArestaDistancia(origem.getNome(), destino.getNome(), ida, volta);
        } catch (RuntimeException e) {
            // segunda proteção: o Grafo também recusa ligação repetida ou de um ponto com ele mesmo
            Validacoes.erro(this, "Não foi possível salvar: " + e.getMessage());
            return;
        }

        salvo = true;
        JOptionPane.showMessageDialog(this, "Ligação " + origem.getNome() + " - " + destino.getNome()
                + " adicionada.", "Ligação adicionada", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}

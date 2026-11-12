package swing;

import domain.Grafo;
import domain.Ponto;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Diálogo modal para cadastrar um ponto novo já ligado a pelo menos um ponto existente.
// As ligações ficam numa lista temporária; o grafo só é alterado quando o usuário clica em Salvar.
public class DialogoCadastro extends JDialog {

    // limites das validações
    private static final int TAMANHO_MAX_NOME = 40;
    private static final int TAMANHO_MAX_ENDERECO = 120;

    private static final DecimalFormat FORMATO =
            new DecimalFormat("#.##", new DecimalFormatSymbols(new Locale("pt", "BR")));

    // uma ligação ainda não salva: destino e distâncias de ida e volta
    private record LigacaoPendente(Ponto destino, double ida, double volta) {
        @Override
        public String toString() {
            return destino.getNome() + "  (ida " + FORMATO.format(ida) + " km, volta "
                    + FORMATO.format(volta) + " km, média " + FORMATO.format((ida + volta) / 2) + " km)";
        }
    }

    private final Grafo grafo;
    private boolean salvo = false;

    // pontos que ainda podem ser escolhidos no combo (sai daqui quem já foi ligado)
    private final List<Ponto> disponiveis;

    private final JTextField campoNome = new JTextField(22);
    private final JTextField campoEndereco = new JTextField(22);
    private final JComboBox<String> comboDestino = new JComboBox<>();
    private final JTextField campoIda = new JTextField(6);
    private final JTextField campoVolta = new JTextField(6);
    private final DefaultListModel<LigacaoPendente> modeloLigacoes = new DefaultListModel<>();
    private final JList<LigacaoPendente> listaLigacoes = new JList<>(modeloLigacoes);
    private final JButton botaoAdicionar = new JButton("Adicionar ligação");
    private final JButton botaoRemover = new JButton("Remover ligação");
    private final JButton botaoSalvar = new JButton("Salvar");

    public DialogoCadastro(JFrame dono, Grafo grafo) {
        super(dono, "Cadastrar ponto", true); // true = modal
        this.grafo = grafo;
        this.disponiveis = new ArrayList<>(grafo.getPontos());
        atualizarCombo();

        setLayout(new BorderLayout(8, 8));
        add(criarFormulario(), BorderLayout.NORTH);
        add(criarListaLigacoes(), BorderLayout.CENTER);
        add(criarBotoes(), BorderLayout.SOUTH);

        atualizarBotoes();
        // calcula o tamanho e corrige de novo quando a janela abrir (evita os botões cortados no Linux)
        AjusteDialogo.preparar(this);
    }

    public boolean isSalvo() {
        return salvo;
    }

    // ---------- montagem da tela ----------

    private JPanel criarFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        adicionarLinha(painel, c, 0, "Nome:", campoNome);
        adicionarLinha(painel, c, 1, "Endereço:", campoEndereco);
        adicionarLinha(painel, c, 2, "Ligar a:", comboDestino);

        JPanel distancias = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        distancias.add(new JLabel("Ida (km):"));
        distancias.add(campoIda);
        distancias.add(new JLabel("Volta (km):"));
        distancias.add(campoVolta);
        adicionarLinha(painel, c, 3, "Distâncias:", distancias);

        botaoAdicionar.addActionListener(e -> adicionarLigacao());
        // Enter no campo de volta também adiciona a ligação
        campoVolta.addActionListener(e -> adicionarLigacao());

        c.gridx = 1;
        c.gridy = 4;
        c.fill = GridBagConstraints.NONE;
        painel.add(botaoAdicionar, c);
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

    // lista das ligações já adicionadas, com botão para remover a selecionada
    private JPanel criarListaLigacoes() {
        listaLigacoes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaLigacoes.addListSelectionListener(e -> atualizarBotoes());
        botaoRemover.addActionListener(e -> removerLigacao());

        JScrollPane rolagem = new JScrollPane(listaLigacoes);
        rolagem.setPreferredSize(new Dimension(0, 130));

        JPanel painelRemover = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelRemover.add(botaoRemover);

        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Ligações do novo ponto"));
        painel.add(rolagem, BorderLayout.CENTER);
        painel.add(painelRemover, BorderLayout.SOUTH);
        return painel;
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

    // ---------- ações ----------

    // valida o destino e as distâncias e coloca a ligação na lista temporária
    private void adicionarLigacao() {
        // valida o nome logo aqui, para o usuário não preencher tudo e só descobrir o erro no final
        if (!validarNomeEEndereco()) return;

        int indice = comboDestino.getSelectedIndex();
        if (indice < 0) {
            erro("Não há mais ecopontos disponíveis para ligar.");
            return;
        }

        Double ida = Validacoes.lerDistancia(this, campoIda, "ida");
        if (ida == null) return;
        Double volta = Validacoes.lerDistancia(this, campoVolta, "volta");
        if (volta == null) return;

        Ponto destino = disponiveis.remove(indice); // sai do combo: não dá para ligar duas vezes ao mesmo ponto
        modeloLigacoes.addElement(new LigacaoPendente(destino, ida, volta));

        campoIda.setText("");
        campoVolta.setText("");
        atualizarCombo();
        atualizarBotoes();
        campoIda.requestFocusInWindow();
    }

    // tira a ligação selecionada da lista e devolve o ponto para o combo
    private void removerLigacao() {
        int indice = listaLigacoes.getSelectedIndex();
        if (indice < 0) return;

        LigacaoPendente removida = modeloLigacoes.remove(indice);
        disponiveis.add(removida.destino());
        // mantém o combo na mesma ordem do grafo
        disponiveis.sort((a, b) -> Integer.compare(a.getIndice(), b.getIndice()));
        atualizarCombo();
        atualizarBotoes();
    }

    // valida tudo, cria o ponto e as ligações no grafo e fecha o diálogo
    private void salvar() {
        if (!validarNomeEEndereco()) return;

        // ligação digitada mas não adicionada: em vez de perder, pergunta ao usuário
        boolean temLigacaoDigitada = !campoIda.getText().isBlank() || !campoVolta.getText().isBlank();
        if (temLigacaoDigitada) {
            int resposta = JOptionPane.showConfirmDialog(this,
                    "Há uma ligação preenchida que ainda não foi adicionada.\nAdicionar antes de salvar?",
                    "Ligação não adicionada", JOptionPane.YES_NO_CANCEL_OPTION);
            if (resposta == JOptionPane.CANCEL_OPTION || resposta == JOptionPane.CLOSED_OPTION) return;
            if (resposta == JOptionPane.YES_OPTION) {
                int antes = modeloLigacoes.size();
                adicionarLigacao();
                if (modeloLigacoes.size() == antes) return; // a ligação era inválida: o erro já apareceu
            }
        }

        if (modeloLigacoes.isEmpty()) {
            erro("O ponto precisa estar ligado a pelo menos um ecoponto.");
            return;
        }

        String nome = campoNome.getText().trim();
        try {
            // tudo validado: agora sim altera o grafo
            grafo.adicionarPonto(nome, campoEndereco.getText().trim());
            for (int i = 0; i < modeloLigacoes.size(); i++) {
                LigacaoPendente l = modeloLigacoes.get(i);
                grafo.adicionarArestaDistancia(nome, l.destino().getNome(), l.ida(), l.volta());
            }
        } catch (RuntimeException e) {
            // não deveria acontecer depois das validações, mas se acontecer o usuário fica sabendo
            erro("Não foi possível salvar: " + e.getMessage());
            return;
        }

        salvo = true;
        JOptionPane.showMessageDialog(this, "Ponto \"" + nome + "\" cadastrado com "
                + modeloLigacoes.size() + " ligação(ões).", "Cadastro concluído", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    // ---------- validações ----------

    private boolean validarNomeEEndereco() {
        String nome = campoNome.getText().trim();
        String endereco = campoEndereco.getText().trim();

        if (nome.isEmpty()) {
            return erroNoCampo("O nome não pode ficar vazio.", campoNome);
        }
        if (nome.length() > TAMANHO_MAX_NOME) {
            return erroNoCampo("O nome pode ter no máximo " + TAMANHO_MAX_NOME + " caracteres.", campoNome);
        }
        if (!nome.matches(".*\\p{L}.*")) {
            return erroNoCampo("O nome precisa ter pelo menos uma letra.", campoNome);
        }
        if (grafo.existePonto(nome)) {
            return erroNoCampo("Já existe um ecoponto chamado \"" + nome + "\".", campoNome);
        }
        if (endereco.isEmpty()) {
            return erroNoCampo("O endereço não pode ficar vazio.", campoEndereco);
        }
        if (endereco.length() > TAMANHO_MAX_ENDERECO) {
            return erroNoCampo("O endereço pode ter no máximo " + TAMANHO_MAX_ENDERECO + " caracteres.", campoEndereco);
        }
        return true;
    }

    // ---------- auxiliares ----------

    private void atualizarCombo() {
        comboDestino.removeAllItems();
        for (Ponto p : disponiveis) {
            comboDestino.addItem(p.getNome());
        }
    }

    // liga e desliga os botões conforme o estado da tela
    private void atualizarBotoes() {
        boolean temDisponivel = !disponiveis.isEmpty();
        botaoAdicionar.setEnabled(temDisponivel);
        comboDestino.setEnabled(temDisponivel);
        campoIda.setEnabled(temDisponivel);
        campoVolta.setEnabled(temDisponivel);
        botaoRemover.setEnabled(listaLigacoes.getSelectedIndex() >= 0);
        botaoSalvar.setEnabled(!modeloLigacoes.isEmpty()); // só salva com pelo menos uma ligação
    }

    private boolean erroNoCampo(String mensagem, JTextField campo) {
        return Validacoes.erroNoCampo(this, mensagem, campo);
    }

    private void erro(String mensagem) {
        Validacoes.erro(this, mensagem);
    }
}

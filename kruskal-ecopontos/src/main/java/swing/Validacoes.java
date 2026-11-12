package swing;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.Component;

// Validações usadas pelos dois diálogos (cadastro de ponto e nova ligação)
public class Validacoes {

    public static final double DISTANCIA_MAXIMA_KM = 200; // nenhuma ligação dentro de Goiânia passa disso

    // Lê a distância do campo. Aceita vírgula ou ponto; exige número maior que zero e até o limite.
    // Se for inválida, mostra o erro, coloca o cursor no campo e devolve null.
    public static Double lerDistancia(Component pai, JTextField campo, String qual) {
        String texto = campo.getText().trim();
        if (texto.isEmpty()) {
            erroNoCampo(pai, "Informe a distância de " + qual + ".", campo);
            return null;
        }
        // só dígitos com no máximo uma vírgula ou ponto (bloqueia "1e5", "NaN", "Infinity", "-3" etc.)
        if (!texto.matches("\\d+([.,]\\d+)?")) {
            erroNoCampo(pai, "Distância de " + qual + " inválida. Use um número, ex.: 11,4", campo);
            return null;
        }
        double valor = Double.parseDouble(texto.replace(",", "."));
        if (valor <= 0) {
            erroNoCampo(pai, "A distância de " + qual + " precisa ser maior que zero.", campo);
            return null;
        }
        if (valor > DISTANCIA_MAXIMA_KM) {
            erroNoCampo(pai, "A distância de " + qual + " passa de " + (int) DISTANCIA_MAXIMA_KM
                    + " km. Confira o valor.", campo);
            return null;
        }
        return valor;
    }

    // mostra o erro e deixa o campo selecionado para o usuário corrigir; sempre devolve false
    public static boolean erroNoCampo(Component pai, String mensagem, JTextField campo) {
        erro(pai, mensagem);
        campo.requestFocusInWindow();
        campo.selectAll();
        return false;
    }

    public static void erro(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}

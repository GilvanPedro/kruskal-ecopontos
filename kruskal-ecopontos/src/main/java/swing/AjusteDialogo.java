package swing;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;

// Corrige o tamanho dos diálogos no Linux (Wayland/COSMIC, GNOME etc.).
// Problema: quando o pack() roda antes da janela aparecer, o Java ainda não sabe a altura
// da barra de título e calcula a janela mais baixa do que precisa. O que fica embaixo
// (botões Salvar e Cancelar) acaba cortado.
// Solução: recalcular o tamanho de novo depois que a janela abriu, quando essa medida já está certa.
public class AjusteDialogo {

    public static void preparar(JDialog dialogo) {
        // margem interna para nada encostar na borda
        ((JComponent) dialogo.getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialogo.setResizable(true);
        dialogo.pack();
        dialogo.setLocationRelativeTo(dialogo.getOwner());

        dialogo.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                // espera a janela terminar de aparecer e mede de novo
                SwingUtilities.invokeLater(() -> {
                    dialogo.pack();
                    Dimension tamanho = dialogo.getSize();
                    // folga extra caso o sistema ainda informe a barra de título menor do que é
                    tamanho.height += 20;
                    dialogo.setSize(tamanho);
                    dialogo.setMinimumSize(tamanho); // não deixa encolher a ponto de esconder os botões
                    dialogo.setLocationRelativeTo(dialogo.getOwner());
                });
            }
        });
    }
}

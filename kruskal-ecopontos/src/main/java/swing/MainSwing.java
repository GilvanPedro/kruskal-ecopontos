package swing;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import java.awt.Font;

public class MainSwing {

    // tamanho base das fontes de botões, rótulos e campos
    private static final int TAMANHO_FONTE_BASE = 16;

    public static void main(String[] args) {
        // suavização do texto (antialiasing): sem isso, no Linux, as letras ficam serrilhadas/borradas.
        // Precisa ser definido antes de qualquer componente Swing ser criado.
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        aumentarFontes();

        // abre a janela pela thread de eventos do Swing (forma correta de iniciar a interface)
        SwingUtilities.invokeLater(() -> new JanelaPrincipal().setVisible(true));
    }

    // troca a fonte padrão de todos os componentes Swing por uma maior
    private static void aumentarFontes() {
        for (Object chave : UIManager.getDefaults().keySet().toArray()) {
            if (UIManager.get(chave) instanceof Font fonte) {
                UIManager.put(chave, new FontUIResource(fonte.getFamily(), fonte.getStyle(), TAMANHO_FONTE_BASE));
            }
        }
    }
}

package domain;

// Guarda em qual grupo cada ponto está, para detectar ciclos
public class UniaoBusca {
    private final int[] grupo;

    // cada ponto começa sozinho no seu próprio grupo
    public UniaoBusca(int quantidadePontos) {
        grupo = new int[quantidadePontos];
        for (int i = 0; i < quantidadePontos; i++) {
            grupo[i] = i;
        }
    }

    // os dois pontos já estão no mesmo grupo? (se sim, ligar formaria ciclo)
    public boolean mesmoGrupo(Ponto a, Ponto b) {
        return grupo[a.getIndice()] == grupo[b.getIndice()];
    }

    // junta o grupo de b no grupo de a
    public void unir(Ponto a, Ponto b) {
        int grupoA = grupo[a.getIndice()];
        int grupoB = grupo[b.getIndice()];
        for (int i = 0; i < grupo.length; i++) {
            if (grupo[i] == grupoB) grupo[i] = grupoA;
        }
    }
}
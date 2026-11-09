package domain;

public class Aresta implements Comparable<Aresta>{
    private Ponto ponto1;
    private Ponto ponto2;
    private double peso; // vai poder receber a hora do A-F e a distancia dos ecopontos, a unidade fica no grafo

    public Aresta(Ponto ponto1, Ponto ponto2, double peso) {
        this.ponto1 = ponto1;
        this.ponto2 = ponto2;
        this.peso = peso;
    }

    public Ponto getPonto1() {
        return ponto1;
    }

    public Ponto getPonto2() {
        return ponto2;
    }

    public double getPeso() {
        return peso;
    }

    @Override
    public int compareTo(Aresta outra) {
        int porPeso = Double.compare(this.peso, outra.peso);
        if (porPeso != 0) return porPeso;
        // empate no peso: desempata pelo nome da origem
        return this.ponto1.getNome().compareTo(outra.ponto1.getNome());
    }
}

package fixos;

import domain.Grafo;

public class GrafosExemplo {

    // Grafo do professor: 6 centros fictícios, peso em horas, tratado como não dirigido
    public static Grafo grafoAF() {
        Grafo g = new Grafo("h");

        g.adicionarPonto("A");
        g.adicionarPonto("B");
        g.adicionarPonto("C");
        g.adicionarPonto("D");
        g.adicionarPonto("E");
        g.adicionarPonto("F");

        g.adicionarAresta("A", "B", 4);
        g.adicionarAresta("A", "C", 2);
        g.adicionarAresta("B", "C", 5);
        g.adicionarAresta("B", "D", 10);
        g.adicionarAresta("C", "E", 3);
        g.adicionarAresta("D", "F", 11);
        g.adicionarAresta("E", "D", 4);
        g.adicionarAresta("E", "F", 8);

        return g;
    }

    // Case real: ecopontos da Comurg
    // Peso = média, em km, das distâncias de ida e volta no Google Maps tiradas no dia 07/11/2026
    public static Grafo grafoEcopontos() {
        Grafo g = new Grafo("km");

        g.adicionarPonto("Guanabara", "Rua GB-5 com Rua GB-6, Jardim Guanabara II");
        g.adicionarPonto("Faiçalville", "Av. Nadra Bufaiçal com Av. Madri, Setor Faiçalville");
        g.adicionarPonto("São José", "Rua Frei Nazareno Confaloni com Rua Irmã Maria Bernarda, Jardim São José");
        g.adicionarPonto("Campos Dourados", "Rua São João Del Rei, APM 07, Residencial Campos Dourados");
        g.adicionarPonto("Parque Eldorado Oeste", "Rua Elo 43, Parque Eldorado Oeste");

        //                         origem              destino                          ida              volta
        g.adicionarArestaDistancia("Guanabara", "Faiçalville",            25.5, 26.2);
        g.adicionarArestaDistancia("Guanabara", "São José",               26.0, 26.6);
        g.adicionarArestaDistancia("Guanabara", "Campos Dourados",        38.8, 40.6);
        g.adicionarArestaDistancia("Guanabara", "Parque Eldorado Oeste",  27.4, 28.1);
        g.adicionarArestaDistancia("Faiçalville", "São José",             14.0, 13.9);
        g.adicionarArestaDistancia("Faiçalville", "Campos Dourados",      11.0, 11.8);
        g.adicionarArestaDistancia("Faiçalville", "Parque Eldorado Oeste",16.1, 16.1);
        g.adicionarArestaDistancia("São José", "Campos Dourados",         18.1, 19.7);
        g.adicionarArestaDistancia("São José", "Parque Eldorado Oeste",    2.7,  2.7);
        g.adicionarArestaDistancia("Campos Dourados", "Parque Eldorado Oeste", 21.8, 20.3);

        return g;
    }
}
package domain;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Kruskal {

    // formato brasileiro com até 2 casas decimais (ex.: 25,85 / 53,9 / 21)
    private static final DecimalFormat FORMATO =
            new DecimalFormat("#.##", new DecimalFormatSymbols(new Locale("pt", "BR")));

    public static List<Aresta> executar(Grafo grafo) {
        List<Aresta> arestas = ordenarArestas(grafo);
        List<Aresta> aceitas = escolherArestas(arestas, grafo.getQuantidadePontos(), grafo.getUnidade());
        imprimirResultado(grafo, aceitas);
        return aceitas;
    }

    // lista de arestas do menor para o maior peso
    private static List<Aresta> ordenarArestas(Grafo grafo) {
        List<Aresta> arestas = grafo.getArestas();
        Collections.sort(arestas);
        return arestas;
    }

    // testa cada aresta e aceita as que não formam ciclo
    private static List<Aresta> escolherArestas(List<Aresta> arestas, int n, String unidade) {
        UniaoBusca grupos = new UniaoBusca(n);
        List<Aresta> aceitas = new ArrayList<>();

        for (Aresta a : arestas) {
            if (aceitas.size() == n - 1) break;   // árvore completa

            if (grupos.mesmoGrupo(a.getPonto1(), a.getPonto2())) {
                System.out.println("Descartada: " + formatar(a, unidade) + " -> formaria ciclo");
            } else {
                System.out.println("Aceita:     " + formatar(a, unidade));
                aceitas.add(a);
                grupos.unir(a.getPonto1(), a.getPonto2());
            }
        }
        return aceitas;
    }

    // custo total e aviso de rede desconectada
    private static void imprimirResultado(Grafo grafo, List<Aresta> aceitas) {
        double custoTotal = 0;
        for (Aresta a : aceitas) {
            custoTotal += a.getPeso();
        }
        System.out.println("Custo total: " + FORMATO.format(custoTotal) + " " + grafo.getUnidade());

        if (aceitas.size() < grafo.getQuantidadePontos() - 1) {
            System.out.println("Atenção: a rede não está conectada.");
            for (Ponto p : grafo.getPontosIsolados()) {
                System.out.println("  Ponto sem nenhuma ligação: " + p.getNome());
            }
        }
    }

    // monta o texto da aresta com o peso formatado e a unidade do grafo
    private static String formatar(Aresta a, String unidade) {
        return a.getPonto1().getNome() + " - " + a.getPonto2().getNome()
                + " (" + FORMATO.format(a.getPeso()) + " " + unidade + ")";
    }
}
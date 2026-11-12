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

    // Executa o Kruskal e devolve o resultado como texto.
    // Quem chama decide onde mostrar: o console imprime, o Swing coloca na JTextArea.
    // Versão padrão (igual à especificação e aos slides): para ao completar n - 1 arestas.
    public static String executar(Grafo grafo) {
        return executar(grafo, false);
    }

    // testarTodas = true: continua testando depois da árvore completa,
    // mostrando também as ligações que seriam descartadas por formar ciclo.
    public static String executar(Grafo grafo, boolean testarTodas) {
        StringBuilder texto = new StringBuilder();
        List<Aresta> arestas = ordenarArestas(grafo);
        List<Aresta> aceitas = escolherArestas(arestas, grafo.getQuantidadePontos(), grafo.getUnidade(), testarTodas, texto);
        escreverResultado(grafo, aceitas, texto);
        return texto.toString();
    }

    // lista de arestas do menor para o maior peso
    private static List<Aresta> ordenarArestas(Grafo grafo) {
        List<Aresta> arestas = grafo.getArestas();
        Collections.sort(arestas);
        return arestas;
    }

    // testa cada aresta e aceita as que não formam ciclo
    private static List<Aresta> escolherArestas(List<Aresta> arestas, int n, String unidade,
                                                boolean testarTodas, StringBuilder texto) {
        UniaoBusca grupos = new UniaoBusca(n);
        List<Aresta> aceitas = new ArrayList<>();

        for (Aresta a : arestas) {
            if (!testarTodas && aceitas.size() == n - 1) break;   // árvore completa: para aqui

            if (grupos.mesmoGrupo(a.getPonto1(), a.getPonto2())) {
                texto.append("Descartada: ").append(formatar(a, unidade)).append(" -> formaria ciclo\n");
            } else {
                texto.append("Aceita:     ").append(formatar(a, unidade)).append("\n");
                aceitas.add(a);
                grupos.unir(a.getPonto1(), a.getPonto2());
            }
        }
        return aceitas;
    }

    // custo total e aviso de rede desconectada
    private static void escreverResultado(Grafo grafo, List<Aresta> aceitas, StringBuilder texto) {
        double custoTotal = 0;
        for (Aresta a : aceitas) {
            custoTotal += a.getPeso();
        }
        texto.append("Custo total: ").append(FORMATO.format(custoTotal)).append(" ").append(grafo.getUnidade()).append("\n");

        if (aceitas.size() < grafo.getQuantidadePontos() - 1) {
            texto.append("Atenção: a rede não está conectada.\n");
            for (Ponto p : grafo.getPontosIsolados()) {
                texto.append("  Ponto sem nenhuma ligação: ").append(p.getNome()).append("\n");
            }
        }
    }

    // monta o texto da aresta com o peso formatado e a unidade do grafo
    private static String formatar(Aresta a, String unidade) {
        return a.getPonto1().getNome() + " - " + a.getPonto2().getNome()
                + " (" + FORMATO.format(a.getPeso()) + " " + unidade + ")";
    }
}

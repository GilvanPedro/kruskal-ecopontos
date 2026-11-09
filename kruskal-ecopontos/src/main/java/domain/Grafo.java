package domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Grafo {
    private final String unidade; // "h" ou "km"
    private final Map<String, Ponto> pontos = new LinkedHashMap<>(); // nome -> Ponto
    private final List<Aresta> arestas = new ArrayList<>(); // todas as ligações

    public Grafo(String unidade) {
        this.unidade = unidade;
    }

    public int getQuantidadePontos() {
        return pontos.size();
    }

    public String getUnidade() {
        return unidade;
    }

    public List<Ponto> getPontos() {
        return new ArrayList<>(pontos.values());
    }

    // verifica se já existe um ponto com esse nome
    public boolean existePonto(String nome) {
        return pontos.containsKey(normalizar(nome));
    }

    // padronizar a nomeclatura para evitar duplicidade
    private String normalizar(String nome){
        return nome.trim().toUpperCase().replaceAll(" ", "_");
    }

    // metodo para buscar um ponto
    public Ponto buscarPonto(String nome){
        Ponto ponto = pontos.get(normalizar(nome));

        if(ponto == null){
            throw new RuntimeException("Ponto \"" + nome + "\" não encontrado");
        }
        return ponto;
    }

    // adicionar um ponto para usar na hora. Nao tendo endereco
    public Ponto adicionarPonto(String nome) {
        return adicionarPonto(nome, null);
    }

    // adicao de um novo ponto
    public Ponto adicionarPonto(String nome, String endereco){
        String chave = normalizar(nome);

        // verifica se o ponto já existe
        if(pontos.containsKey(chave)){
            throw new RuntimeException("Ponto ja existente");
        }

        Ponto novoPonto = new Ponto(nome.trim(), endereco, pontos.size());
        pontos.put(chave, novoPonto);
        return novoPonto;
    }

    public void adicionarAresta(String nome1, String nome2, double peso){
        if(peso <= 0){
            throw new RuntimeException("Peso deve ser maior que zero");
        }

        Ponto p1 = buscarPonto(nome1);
        Ponto p2 = buscarPonto(nome2);

        if(p1 == p2){
            throw new RuntimeException("Não da para ligar um ponto nele mesmo");
        }
        if(existeLigacao(p1, p2)){
            throw new RuntimeException("Essa ligação já existe");
        }

        arestas.add(new Aresta(p1, p2, peso));
    }

    // faz a média da distancia para poder adicionar a aresta, chamando o metodo adicionarAresta
    public void adicionarArestaDistancia(String nome1, String nome2, double distanciaIda, double distanciaVolta) {
        adicionarAresta(nome1, nome2, (distanciaIda + distanciaVolta) / 2);
    }

    // verifica se já existe uma ligaçao entre os 2 pontos fornecidos
    private boolean existeLigacao(Ponto p1, Ponto p2) {
        for (Aresta a : arestas) {
            boolean mesmoSentido = a.getPonto1() == p1 && a.getPonto2() == p2;
            boolean sentidoInverso = a.getPonto1() == p2 && a.getPonto2() == p1;
            if (mesmoSentido || sentidoInverso) return true;
        }
        return false;
    }

    // lista das arestas
    public List<Aresta> getArestas() {
        return new ArrayList<>(arestas);
    }

    // devolve os pontos que não aparecem em nenhuma aresta
    public List<Ponto> getPontosIsolados() {
        List<Ponto> isolados = new ArrayList<>();
        for (Ponto p : pontos.values()) {
            boolean temLigacao = false;
            for (Aresta a : arestas) {
                if (a.getPonto1() == p || a.getPonto2() == p) {
                    temLigacao = true;
                    break;
                }
            }
            if (!temLigacao) isolados.add(p);
        }
        return isolados;
    }
}
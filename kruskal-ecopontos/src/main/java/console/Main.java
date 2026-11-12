package console;

import fixos.GrafosExemplo;
import domain.Aresta;
import domain.Grafo;
import domain.Kruskal;
import domain.Ponto;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final DecimalFormat FORMATO =
            new DecimalFormat("#.##", new DecimalFormatSymbols(new Locale("pt", "BR")));

    // grafo dos ecopontos: começa com os 5 originais e recebe os pontos adicionados pelo usuário
    private static Grafo ecopontos = GrafosExemplo.grafoEcopontos();

    public static void main(String[] args) {
        int opcao;
        do {
            mostrarMenu();
            opcao = lerInteiro("Escolha uma opção: ");
            System.out.println();

            switch (opcao) {
                case 1 -> executarAF();
                case 2 -> executarEcopontos();
                case 3 -> listarGrafo(ecopontos);
                case 4 -> adicionarPonto();
                case 5 -> adicionarLigacao();
                case 6 -> restaurarEcopontos();
                case 0 -> System.out.println("Encerrando o programa.");
                default -> System.out.println("Opção inválida.");
            }
            System.out.println();
        } while (opcao != 0);
    }

    private static void mostrarMenu() {
        System.out.println("========== KRUSKAL - ECOPONTOS COMURG ==========");
        System.out.println("1 - Executar Kruskal no grafo A–F (exemplo do professor)");
        System.out.println("2 - Executar Kruskal nos ecopontos");
        System.out.println("3 - Listar ecopontos e ligações");
        System.out.println("4 - Adicionar novo ecoponto");
        System.out.println("5 - Adicionar ligação entre ecopontos existentes");
        System.out.println("6 - Restaurar os 5 ecopontos originais");
        System.out.println("0 - Sair");
    }

    // Execuções fixas

    private static void executarAF() {
        System.out.println("=== Grafo A–F (peso em horas) ===");
        System.out.print(Kruskal.executar(GrafosExemplo.grafoAF()));
    }

    // Ecopontos

    // roda nos ecopontos atuais: os 5 originais + os que o usuário adicionou
    private static void executarEcopontos() {
        System.out.println("=== Ecopontos Comurg (peso = média ida/volta em km, Google Maps, 07/11/2026) ===");
        System.out.print(Kruskal.executar(ecopontos));
    }

    // descarta os pontos adicionados e volta para o case original (53,9 km)
    private static void restaurarEcopontos() {
        ecopontos = GrafosExemplo.grafoEcopontos();
        System.out.println("Ecopontos restaurados para os 5 originais.");
    }

    private static void listarGrafo(Grafo grafo) {
        System.out.println("Pontos:");
        List<Ponto> pontos = grafo.getPontos();
        for (int i = 0; i < pontos.size(); i++) {
            Ponto p = pontos.get(i);
            String endereco = p.getEndereco() == null ? "" : " - " + p.getEndereco();
            System.out.println("  " + (i + 1) + ". " + p.getNome() + endereco);
        }
        System.out.println("Ligações:");
        for (Aresta a : grafo.getArestas()) {
            System.out.println("  " + a.getPonto1().getNome() + " - " + a.getPonto2().getNome()
                    + " (" + FORMATO.format(a.getPeso()) + " " + grafo.getUnidade() + ")");
        }
    }

    // Cria um ponto novo já ligado a pelo menos um ponto existente.
    // Tudo é pedido e validado primeiro; o ponto só é criado no final.
    private static void adicionarPonto() {
        String nome = lerTexto("Nome do novo ecoponto: ");
        if (ecopontos.existePonto(nome)) {
            System.out.println("Erro: já existe um ponto com esse nome.");
            return;
        }
        String endereco = lerTexto("Endereço: ");

        List<Ponto> destinos = new ArrayList<>();
        List<double[]> distancias = new ArrayList<>();
        List<Ponto> existentes = ecopontos.getPontos();

        System.out.println("O ponto precisa ser ligado a pelo menos um ponto existente.");
        do {
            Ponto destino = escolherPonto(existentes, "A qual ponto ligar? ");
            if (destinos.contains(destino)) {
                System.out.println("Erro: você já escolheu esse ponto.");
                continue;
            }
            double ida = lerPositivo("Distância de ida (km): ");
            double volta = lerPositivo("Distância de volta (km): ");
            destinos.add(destino);
            distancias.add(new double[]{ida, volta});
        } while (lerSimNao("Ligar a mais algum ponto? (s/n): "));

        // só agora cria o ponto e as ligações
        ecopontos.adicionarPonto(nome, endereco);
        for (int i = 0; i < destinos.size(); i++) {
            double[] d = distancias.get(i);
            ecopontos.adicionarArestaDistancia(nome, destinos.get(i).getNome(), d[0], d[1]);
        }
        System.out.println("Ponto \"" + nome.trim() + "\" adicionado com " + destinos.size() + " ligação(ões).");
    }

    // Liga dois pontos que já existem
    private static void adicionarLigacao() {
        List<Ponto> existentes = ecopontos.getPontos();
        Ponto p1 = escolherPonto(existentes, "Primeiro ponto: ");
        Ponto p2 = escolherPonto(existentes, "Segundo ponto: ");
        double ida = lerPositivo("Distância de ida (km): ");
        double volta = lerPositivo("Distância de volta (km): ");
        try {
            ecopontos.adicionarArestaDistancia(p1.getNome(), p2.getNome(), ida, volta);
            System.out.println("Ligação adicionada.");
        } catch (RuntimeException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    // Leitura com validação

    private static Ponto escolherPonto(List<Ponto> pontos, String mensagem) {
        for (int i = 0; i < pontos.size(); i++) {
            System.out.println("  " + (i + 1) + " - " + pontos.get(i).getNome());
        }
        while (true) {
            int escolha = lerInteiro(mensagem);
            if (escolha >= 1 && escolha <= pontos.size()) return pontos.get(escolha - 1);
            System.out.println("Número inválido. Escolha entre 1 e " + pontos.size() + ".");
        }
    }

    private static String lerTexto(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String texto = sc.nextLine().trim();
            if (!texto.isEmpty()) return texto;
            System.out.println("O campo não pode ficar vazio.");
        }
    }

    private static int lerInteiro(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro.");
            }
        }
    }

    // aceita vírgula ou ponto e exige valor maior que zero
    private static double lerPositivo(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            try {
                double valor = Double.parseDouble(sc.nextLine().trim().replace(",", "."));
                if (valor > 0) return valor;
                System.out.println("O valor precisa ser maior que zero.");
            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido (ex.: 11,4).");
            }
        }
    }

    private static boolean lerSimNao(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String r = sc.nextLine().trim().toLowerCase();
            if (r.equals("s")) return true;
            if (r.equals("n")) return false;
            System.out.println("Responda com s ou n.");
        }
    }
}
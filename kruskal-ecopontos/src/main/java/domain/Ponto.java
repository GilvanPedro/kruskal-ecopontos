package domain;

public class Ponto {
    private final String nome;
    private final String endereco;
    private final int indice;   // posição do ponto no vetor da UniaoBusca (não muda)

    public Ponto(String nome, String endereco, int indice) {
        this.nome = nome;
        this.endereco = endereco;
        this.indice = indice;
    }

    public String getNome() { return nome; }
    public String getEndereco() { return endereco; }
    public int getIndice() { return indice; }
}
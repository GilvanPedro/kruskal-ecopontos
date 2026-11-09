package domain;

public class Ponto {
    private String nome;
    private String endereco;
    private int grupo;

    public Ponto(String nome, String endereco, int grupo){
        this.nome = nome;
        this.endereco = endereco;
        this.grupo = grupo;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public int getGrupo() {
        return grupo;
    }

    public void setGrupo(int grupo) {
        this.grupo = grupo;
    }
}

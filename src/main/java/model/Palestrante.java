package model;

public class Palestrante {
    private int id;
    private String nome;
    private String curriculo;
    private String areaDeAtuacao;

    public Palestrante(int id, String nome, String curriculo, String areaDeAtuacao) {
        this.id = id;
        this.nome = nome;
        this.curriculo = curriculo;
        this.areaDeAtuacao = areaDeAtuacao;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCurriculo() { return curriculo; }
    public String getAreaDeAtuacao() { return areaDeAtuacao; }

    @Override
    public String toString() {
        return nome; // Simplificado para exibição em JList
    }
}
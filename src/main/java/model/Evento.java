package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Evento {
    private int id;
    private String nome;
    private String descricao;
    private LocalDate data;
    private String local;
    private int capacidade;
    private List<Palestrante> palestrantes;
    private List<Participante> participantesInscritos;

    public Evento(int id, String nome, String descricao, LocalDate data, String local, int capacidade) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.data = data;
        this.local = local;
        this.capacidade = capacidade;
        this.palestrantes = new ArrayList<>();
        this.participantesInscritos = new ArrayList<>();
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public LocalDate getData() { return data; }
    public String getLocal() { return local; }
    public int getCapacidade() { return capacidade; }
    public List<Palestrante> getPalestrantes() { return palestrantes; }
    public List<Participante> getParticipantesInscritos() { return participantesInscritos; }

    public void setNome(String nome) { this.nome = nome; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setData(LocalDate data) { this.data = data; }
    public void setLocal(String local) { this.local = local; }
    public void setCapacidade(int capacidade) { this.capacidade = capacidade; }
    public void setPalestrantes(List<Palestrante> palestrantes) { this.palestrantes = palestrantes; }
    public void setParticipantesInscritos(List<Participante> participantes) { this.participantesInscritos = participantes; }
    
    public void adicionarPalestrante(Palestrante palestrante) { this.palestrantes.add(palestrante); }
    public int getVagasDisponiveis() { return capacidade - participantesInscritos.size(); }

    @Override
    public String toString() {
        return "ID: " + id + " | Evento: " + nome + " | Data: " + data;
    }
}
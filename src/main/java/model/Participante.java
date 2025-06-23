package model;

import java.util.Objects;

public class Participante {
    private int id;
    private String nome;
    private String email;

    public Participante(int id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }
    
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participante that = (Participante) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ID: " + id + " | Nome: " + nome + " | Email: " + email;
    }
}
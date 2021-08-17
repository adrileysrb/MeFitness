package com.example.mefitness.model;

import java.io.Serializable;

public class Exercicio implements Serializable {

    String nome;
    String image;
    String observacoes;
    String categoria;
    String dificuldade;

    public Exercicio(){}

    public Exercicio(String nome, String image, String observacoes, String categoria, String dificuldade) {
        this.nome = nome;
        this.image = image;
        this.observacoes = observacoes;
        this.categoria = categoria;
        this.dificuldade = dificuldade;
    }

    public String getNumber() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDificuldade() {
        return dificuldade;
    }

    public void setDificuldade(String dificuldade) {
        this.dificuldade = dificuldade;
    }
}
